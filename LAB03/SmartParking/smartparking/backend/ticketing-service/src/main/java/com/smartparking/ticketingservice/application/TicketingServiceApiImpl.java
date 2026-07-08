package com.smartparking.ticketingservice.application;

import com.smartparking.ticketingservice.application.ports.TicketingRepository;
import com.smartparking.ticketingservice.application.ports.TicketingServiceAPI;
import com.smartparking.ticketingservice.model.TicketMessage;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TicketingServiceApiImpl implements TicketingServiceAPI {

    private static final Logger logger = LoggerFactory.getLogger(TicketingServiceApiImpl.class);
    private final TicketingRepository repository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TicketingServiceApiImpl(TicketingRepository repository) {
        this.repository = repository;
    }

    @Override
    public CompletableFuture<TicketMessage> createTicket(TicketMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            String id = UUID.randomUUID().toString();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String createdAt = LocalDateTime.now().format(FORMATTER);

            message.setId(id);
            message.setType("physical");
            message.setTimestamp(timestamp);
            message.setCreatedAt(createdAt);
            message.setStatus("IN_PROGRESS");

            JsonObject doc = JsonObject.mapFrom(message);
            repository.save(doc);

            logger.info("🎫 Created new ticket for plate {} at {}", message.getAssociatedWithPlate(), createdAt);
            return message;
        });
    }

    @Override
    public CompletableFuture<TicketMessage> createVirtualTicket(TicketMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            String id = UUID.randomUUID().toString();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String createdAt = LocalDateTime.now().format(FORMATTER);

            message.setId(id);
            message.setType("virtual");
            message.setTimestamp(timestamp);
            message.setCreatedAt(createdAt);
            message.setStatus("IN_PROGRESS");

            JsonObject doc = JsonObject.mapFrom(message);
            repository.save(doc);

            logger.info("💻 Created new virtual ticket for plate {} at {}", message.getAssociatedWithPlate(), createdAt);
            return message;
        });
    }

    @Override
    public CompletableFuture<TicketMessage> getTicketById(String id) {
        return repository.findById(id)
                .thenApply(optional -> optional.map(json -> json.mapTo(TicketMessage.class)).orElse(null));
    }

    @Override
    public CompletableFuture<TicketMessage> getVirtualTicketById(String id) {
        return repository.findById(id)
                .thenApply(optional -> optional.map(json -> json.mapTo(TicketMessage.class)).orElse(null));
    }

    @Override
    public CompletableFuture<TicketMessage> updateTicket(String id, TicketMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            if (message.isExitDetected()) {
                message.setStatus("COMPLETED");
                message.setCompletedAt(LocalDateTime.now().format(FORMATTER));
            }
            return message;
        }).thenCompose(updated -> {
            JsonObject updateJson = JsonObject.mapFrom(updated);
            return repository.update(id, updateJson)
                    .thenApply(res -> res != null ? updated : null);
        });
    }

    @Override
    public CompletableFuture<TicketMessage> updateVirtualTicket(String id, TicketMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            if (message.isExitDetected()) {
                message.setStatus("COMPLETED");
                message.setCompletedAt(LocalDateTime.now().format(FORMATTER));
            }
            return message;
        }).thenCompose(updated -> {
            JsonObject updateJson = JsonObject.mapFrom(updated);
            return repository.update(id, updateJson)
                    .thenApply(res -> res != null ? updated : null);
        });
    }

    @Override
    public CompletableFuture<TicketMessage> getTicketByPlate(String plate) {
        return repository.findByPlate(plate)
                .thenApply(optionalTicket -> optionalTicket
                        .map(json -> json.mapTo(TicketMessage.class))
                        .orElse(null));
    }

    @Override
    public CompletableFuture<JsonObject> getAllTickets() {
        return repository.getAllTickets();
    }

    @Override
    public CompletableFuture<TicketMessage> addToHistory(String plate, TicketMessage ticket) {
        CompletableFuture<TicketMessage> future = new CompletableFuture<>();

        repository.saveToHistoryCollection(ticket)
                .whenComplete((savedJson, ex) -> {
                    if (ex != null) {
                        future.completeExceptionally(ex);
                        return;
                    }

                    TicketMessage savedTicket = savedJson.mapTo(TicketMessage.class);

                    future.complete(savedTicket);
                });

        return future;
    }

    @Override
    public CompletableFuture<JsonObject> getAllHistoryTickets() {
        return repository.getAllHistoryTickets();
    }

    @Override
    public CompletableFuture<TicketMessage> getHistoryTicketByPlate(String plate) {
        return repository.findHistoryByPlate(plate)
                .thenApply(optionalTicket -> optionalTicket
                        .map(json -> json.mapTo(TicketMessage.class))
                        .orElse(null));
    }

    @Override
    public CompletableFuture<TicketMessage> updateStatusByPlate(String plate, TicketMessage request) {
        CompletableFuture<TicketMessage> future = new CompletableFuture<>();

        repository.findByPlate(plate)
                .thenCompose(optional -> {
                    if (optional.isEmpty()) {
                        logger.warn("⚠️ No ticket found for plate {}", plate);
                        return CompletableFuture.completedFuture(null);
                    }

                    TicketMessage ticket = optional.get().mapTo(TicketMessage.class);

                    if (request.getStatus() != null) {
                        ticket.setStatus(request.getStatus());
                    }

                    if (request.getStatus() != null) {
                        ticket.setFee(request.getFee());
                    }

                    if (request.isExitDetected()) {
                        ticket.setExitDetected(true);
                        ticket.setStatus("COMPLETED");
                        ticket.setCompletedAt(LocalDateTime.now().format(FORMATTER));
                    }

                    JsonObject updatedJson = JsonObject.mapFrom(ticket);
                    return repository.updateByPlate(plate, updatedJson)
                            .thenApply(res -> ticket);
                })
                .thenAccept(future::complete)
                .exceptionally(ex -> {
                    logger.error("❌ Error updating ticket for plate {}: {}", plate, ex.getMessage());
                    future.completeExceptionally(ex);
                    return null;
                });

        return future;
    }
}
