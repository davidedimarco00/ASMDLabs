package com.smartparking.analyticsservice.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparking.analyticsservice.application.ports.AnalyticsRepository;
import com.smartparking.analyticsservice.application.ports.AnalyticsServiceAPI;
import com.smartparking.analyticsservice.application.ports.TicketingServicePort;
import com.smartparking.analyticsservice.model.ReportModel;
import com.smartparking.analyticsservice.model.TicketList;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Service
public class AnalyticsServiceAPIImpl implements AnalyticsServiceAPI {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceAPIImpl.class);
    private static final DateTimeFormatter CREATED_AT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AnalyticsRepository repository;
    private final TicketingServicePort ticketingServicePort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalyticsServiceAPIImpl(
            AnalyticsRepository repository,
            TicketingServicePort ticketingServicePort
    ) {
        this.repository = repository;
        this.ticketingServicePort = ticketingServicePort;
    }

    @Override
    public CompletableFuture<ReportModel> generateReport(LocalDate date) {
        logger.info("Generazione report per data {}", date);
        return repository.findByDate(date)
                .thenCompose(existing -> existing
                        .map(CompletableFuture::completedFuture)
                        .orElseGet(() -> aggregateDataAndSaveReport(date).toCompletableFuture()));
    }

    private CompletionStage<ReportModel> aggregateDataAndSaveReport(LocalDate date) {
        CompletableFuture<JsonObject> futInProgress = safeGetAllTickets();
        CompletableFuture<JsonObject> futHistory = safeGetAllHistoryTickets();

        return CompletableFuture.allOf(futInProgress, futHistory)
                .thenCompose(v -> {
                    List<TicketList.TicketData> inProgressTickets = extractTicketData(futInProgress.join());
                    List<TicketList.TicketData> historyTickets = extractTicketData(futHistory.join());
                    return buildAndSaveReport(inProgressTickets, historyTickets, date);
                });
    }

    private CompletableFuture<JsonObject> safeGetAllTickets() {
        try {
            CompletableFuture<JsonObject> fut = ticketingServicePort.fetchTickets();
            if (fut == null) {
                logger.warn("fetchTickets ha restituito null, uso payload vuoto");
                return CompletableFuture.completedFuture(new JsonObject());
            }
            return fut.thenApply(json -> {
                logger.debug("Raw JSON ricevuto da ticketing-service: {}", json.encodePrettily());
                return json;
            });
        } catch (Exception e) {
            logger.error("Errore chiamando fetchTickets: {}", e.getMessage(), e);
            return CompletableFuture.completedFuture(new JsonObject());
        }
    }

    private CompletableFuture<JsonObject> safeGetAllHistoryTickets() {
        try {
            CompletableFuture<JsonObject> fut = ticketingServicePort.fetchHistoryTickets();
            if (fut == null) return CompletableFuture.completedFuture(new JsonObject());
            return fut.thenApply(json -> {
                logger.debug("Raw JSON ricevuto da ticketing-service (history): {}", json.encodePrettily());
                return json;
            });
        } catch (Exception e) {
            logger.error("Errore chiamando fetchHistoryTickets: {}", e.getMessage(), e);
            return CompletableFuture.completedFuture(new JsonObject());
        }
    }

    private List<TicketList.TicketData> extractTicketData(JsonObject raw) {
        if (raw == null || raw.isEmpty()) {
            logger.warn("Payload vuoto ricevuto");
            return Collections.emptyList();
        }
        try {
            TicketList ticketList = objectMapper.readValue(raw.encode(), TicketList.class);

            if (ticketList.getTickets() == null) {
                logger.warn("Struttura JSON non valida o vuota");
                return Collections.emptyList();
            }

            return ticketList.getTickets().getList().stream()
                    .map(TicketList.TicketEntry::getMap)
                    .filter(td -> td != null)
                    .toList();
        } catch (Exception e) {
            logger.error("Parsing JSON tickets fallito: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private CompletionStage<ReportModel> buildAndSaveReport(List<TicketList.TicketData> inProgressTickets, List<TicketList.TicketData> historyTickets, LocalDate date) {
        int virtualTicketsGenerated = 0;
        int virtualTicketsCompleted = 0;
        double totalRevenue = 0.0;

        for (TicketList.TicketData t : inProgressTickets) {
            if (matchesDate(t.getCreatedAt(), date)) {
                virtualTicketsGenerated++;
            }
        }

        for (TicketList.TicketData t : historyTickets) {
            if (matchesDate(t.getHistoryAddedAt(), date)) {
                virtualTicketsCompleted++;
                Double fee = parseFee(t.getFee());
                if (fee != null && fee > 0) totalRevenue += fee;
            }
        }

        ReportModel report = new ReportModel();
        report.setDate(date);
        report.setVirtualTicketsGenerated(virtualTicketsGenerated);
        report.setVirtualTicketsCompleted(virtualTicketsCompleted);
        report.setTotalRevenue(totalRevenue);

        try {
            String rawData = objectMapper.writeValueAsString(inProgressTickets) +
                    objectMapper.writeValueAsString(historyTickets);
            report.setRawData(rawData);
        } catch (Exception e) {
            logger.warn("Errore serializzazione rawData: {}", e.getMessage());
        }

        logger.info("Report {}: virtualTicketsGenerated={}, virtualTicketsCompleted={}, revenue={}",
                date, virtualTicketsGenerated, virtualTicketsCompleted, totalRevenue);

        return repository.save(report);
    }

    private boolean matchesDate(String rawDate, LocalDate target) {
        if (rawDate == null || rawDate.isBlank()) return false;
        try {
            LocalDate d = LocalDate.parse(rawDate, CREATED_AT_FMT);
            return d.equals(target);
        } catch (Exception e) {
            logger.warn("Errore parsing data '{}': {}", rawDate, e.getMessage());
            return false;
        }
    }

    private Double parseFee(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            logger.debug("Formato fee non valido: '{}'", raw);
            return null;
        }
    }

    @Override
    public CompletableFuture<Optional<ReportModel>> getReportByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    @Override
    public CompletableFuture<List<ReportModel>> getAllReports() {
        return repository.findAll()
                .thenApply(list -> list.stream().distinct().toList());
    }
}
