package com.smartparking.parkingservice.application.ports;

import com.smartparking.parkingservice.model.Car;
import io.vertx.core.json.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

public interface ParkingRestController {

    @PostMapping("/addCar")
    CompletableFuture<ResponseEntity<JsonObject>> addCar(@RequestBody Car carData);

    @PostMapping("/removeCar")
    CompletableFuture<ResponseEntity<JsonObject>> removeCar(@RequestBody Car carData);

    @GetMapping("/getCar/{id}")
    CompletableFuture<ResponseEntity<JsonObject>> getCar(@PathVariable("id") String id);

    @GetMapping("/getSlots")
    CompletableFuture<ResponseEntity<JsonObject>> getParkingSlots();

    @GetMapping("/getEmbeddedStatus")
    CompletableFuture<ResponseEntity<JsonObject>> getEmbeddedStatus();

    @PostMapping("/openEntryBarrier")
    CompletableFuture<ResponseEntity<JsonObject>> openEntryBarrier(@RequestBody JsonObject payload);

    @PostMapping("/openExitBarrier")
    CompletableFuture<ResponseEntity<JsonObject>> openExitBarrier(@RequestBody JsonObject payload);

    @GetMapping("/getAllHistory")
    CompletableFuture<ResponseEntity<JsonObject>> getAllHistory();

}

