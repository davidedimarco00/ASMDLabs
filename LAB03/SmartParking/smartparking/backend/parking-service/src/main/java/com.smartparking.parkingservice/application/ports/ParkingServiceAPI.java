package com.smartparking.parkingservice.application.ports;

import io.vertx.core.json.JsonObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface ParkingServiceAPI {

    CompletableFuture<JsonObject> addCar(JsonObject carData);

    CompletableFuture<JsonObject> removeCar(JsonObject carData);

    CompletableFuture<JsonObject> getCar(String id);

    CompletableFuture<JsonObject> getParkingSlots();

    CompletionStage<JsonObject> getEmbeddedStatus();

    CompletionStage<JsonObject> openEntryBarrier(JsonObject payload);

    CompletionStage<JsonObject> openExitBarrier(JsonObject payload);

    CompletableFuture<JsonObject> getAllHistory();
}
