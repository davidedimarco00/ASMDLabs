package com.smartparking.ticketingservice.application.ports;

import com.smartparking.ticketingservice.model.TicketMessage;
import io.vertx.core.json.JsonObject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TicketingRepository {

    CompletableFuture<Void> save(JsonObject ticket);

    CompletableFuture<Optional<JsonObject>> findById(String id);

    CompletableFuture<JsonObject> update(String id, JsonObject updatedData);

    CompletableFuture<Optional<JsonObject>> findByPlate(String plate);

    CompletableFuture<JsonObject> updateByPlate(String plate, JsonObject updatedData);

    CompletableFuture<JsonObject> getAllTickets();

    CompletableFuture<JsonObject> saveToHistoryCollection(TicketMessage ticket);

    CompletableFuture<JsonObject> getAllHistoryTickets();

    CompletableFuture<Optional<JsonObject>> findHistoryByPlate(String plate);
}
