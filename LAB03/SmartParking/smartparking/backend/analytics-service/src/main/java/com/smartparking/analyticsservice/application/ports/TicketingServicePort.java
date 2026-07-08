package com.smartparking.analyticsservice.application.ports;


import io.vertx.core.json.JsonObject;

import java.util.concurrent.CompletableFuture;

public interface TicketingServicePort {

    CompletableFuture<JsonObject> fetchTickets();

    CompletableFuture<JsonObject> fetchHistoryTickets();
}
