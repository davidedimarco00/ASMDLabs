package com.smartparking.parkingservice.application.ports;

import io.vertx.core.json.JsonObject;

import java.util.concurrent.CompletableFuture;

public interface ParkingServiceRepository {

    CompletableFuture<Void> save(JsonObject carData);

    CompletableFuture<Void> deleteByPlate(String plate);

    CompletableFuture<JsonObject> findById(String id);

    CompletableFuture<JsonObject> findByPlate(String plate);

    CompletableFuture<Void> setExitedByPlate(String plate, String exitTimestamp);

    CompletableFuture<JsonObject> getAllHistory();
}
