package com.smartparking.parkingservice.infrastracture.adapters;

import io.vertx.core.json.JsonObject;

import java.util.concurrent.CompletableFuture;

public interface TicketingClientPort {

    CompletableFuture<JsonObject> createVirtualTicket(String plate);

    CompletableFuture<JsonObject> verifyPaymentByPlate(String plate);
}
