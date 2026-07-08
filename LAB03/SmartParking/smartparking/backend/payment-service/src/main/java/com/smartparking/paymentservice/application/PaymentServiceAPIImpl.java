package com.smartparking.paymentservice.application;

import com.smartparking.paymentservice.application.ports.PaymentRepository;
import com.smartparking.paymentservice.application.ports.PaymentServiceAPI;
import com.smartparking.paymentservice.application.ports.TicketingServicePort;
import com.smartparking.paymentservice.model.FeeDataMessage;
import com.smartparking.paymentservice.model.TicketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PaymentServiceAPIImpl implements PaymentServiceAPI {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceAPIImpl.class);

    private final PaymentRepository paymentRepository;
    private final TicketingServicePort ticketingService;

    public PaymentServiceAPIImpl(PaymentRepository paymentRepository,
                                 TicketingServicePort ticketingService) {
        this.paymentRepository = paymentRepository;
        this.ticketingService = ticketingService;
        logger.info("✅ PaymentServiceAPIImpl initialized");
    }

    @Override
    public CompletableFuture<Void> setActiveTariff(FeeDataMessage feeData) {
        logger.info("💰 Saving tariff configuration to DB: {}", feeData);
        return paymentRepository.setFee(feeData)
                .exceptionally(ex -> {
                    logger.error("❌ Failed to save tariff: {}", ex.getMessage());
                    throw new RuntimeException(ex);
                });
    }

    @Override
    public CompletableFuture<FeeDataMessage> getActiveTariff() {
        return paymentRepository.getFee()
                .exceptionally(ex -> {
                    logger.error("❌ Failed to load tariff: {}", ex.getMessage());
                    throw new RuntimeException(ex);
                });
    }

    @Override
    public CompletableFuture<Double> calculateFee(TicketMessage ticketMessage) {

        logger.info("TicketMessage used for fee calc: {}", ticketMessage);

        return getActiveTariff()
                .thenApply(feeData -> calculateFeeFromConfig(ticketMessage, feeData))
                .exceptionally(ex -> {
                    logger.error("❌ Failed to calculate fee: {}", ex.getMessage());
                    throw new RuntimeException(ex);
                });
    }

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Override
    public CompletableFuture<TicketMessage> processPayment(String NFCTag, TicketMessage ticket) {
        logger.info("💸 Processing payment for ticket with NFCTag={}", NFCTag);

        return getActiveTariff()

                .thenCompose(feeData ->
                        CompletableFuture.supplyAsync(() -> {
                            double fee = calculateFeeFromConfig(ticket, feeData);
                            ticket.setFee(String.valueOf(fee));
                            ticket.setStatus("PAID");
                            logger.info("💸 Ticket updated with fee={} and status=PAID", fee);
                            return ticket;
                        }, executor)
                )
                .thenCompose(updated ->
                            ticketingService.updateTicket(ticket)

                ).thenCompose( updateHistory ->
                        ticketingService.addToHistory(ticket)
                )
                .exceptionally(ex -> {
                    logger.error("❌ Failed to process payment: {}", ex.getMessage());
                    throw new RuntimeException(ex);
                });
    }

    private double calculateFeeFromConfig(TicketMessage ticket, FeeDataMessage config) {

        try {
            String createdAt = ticket.getCreatedAt();

            LocalDateTime entry = LocalDateTime.parse(
                    createdAt,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            );

            Instant entryInstant = entry.atZone(ZoneId.systemDefault()).toInstant();
            Instant exitInstant = Instant.now();

            long minutes = ChronoUnit.MINUTES.between(entryInstant, exitInstant);
            double hours = Math.ceil(minutes / 60.0);

            logger.info("⏱ Duration: {} minutes → {} hours", minutes, hours);

            double total;

            if (hours >= config.getThresholdHours()) {
                total = config.getDailyFee();
            } else {
                total = hours * config.getHourFee();
            }

            return Math.round(total * 100.0) / 100.0;

        } catch (Exception e) {
            logger.error("❌ Fee calculation error: {}", e.getMessage());
            throw new RuntimeException("Failed to calculate fee", e);
        }
    }
}
