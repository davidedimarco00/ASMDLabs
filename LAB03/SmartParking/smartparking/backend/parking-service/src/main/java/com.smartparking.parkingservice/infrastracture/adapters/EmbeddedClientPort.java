package com.smartparking.parkingservice.infrastracture.adapters;

import io.vertx.core.json.JsonObject;

import java.util.concurrent.CompletableFuture;

public interface EmbeddedClientPort {

    CompletableFuture<JsonObject> getEmbeddedStatus();

    CompletableFuture<JsonObject> openEntryBarrier(JsonObject payload);

    CompletableFuture<JsonObject> openExitBarrier(JsonObject payload);
}