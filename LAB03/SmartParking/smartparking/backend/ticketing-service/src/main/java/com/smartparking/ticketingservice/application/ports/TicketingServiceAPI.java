package com.smartparking.ticketingservice.application.ports;

import com.smartparking.ticketingservice.model.TicketMessage;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.CompletableFuture;

public interface TicketingServiceAPI {

    CompletableFuture<TicketMessage> createTicket(TicketMessage ticket);

    CompletableFuture<TicketMessage> createVirtualTicket(TicketMessage ticket);

    CompletableFuture<TicketMessage> getTicketById(String id);

    CompletableFuture<TicketMessage> getVirtualTicketById(String id);

    CompletableFuture<TicketMessage> updateTicket(String id, TicketMessage ticket);

    CompletableFuture<TicketMessage> updateVirtualTicket(String id, TicketMessage ticket);

    CompletableFuture<TicketMessage> updateStatusByPlate(String plate, TicketMessage request);

    CompletableFuture<TicketMessage> getTicketByPlate(String plate);

    CompletableFuture<JsonObject> getAllTickets();

    CompletableFuture<TicketMessage> addToHistory(String plate, TicketMessage request);

    CompletableFuture<JsonObject> getAllHistoryTickets();

    CompletableFuture<TicketMessage> getHistoryTicketByPlate(String plate);
}
