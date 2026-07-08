package com.smartparking.parkingservice.application;

import com.smartparking.parkingservice.application.ports.ParkingRestController;
import com.smartparking.parkingservice.application.ports.ParkingServiceAPI;
import com.smartparking.parkingservice.model.Car;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
public class ParkingRestControllerImpl implements ParkingRestController {

    private static final Logger logger = LoggerFactory.getLogger(ParkingRestControllerImpl.class);
    private final ParkingServiceAPI service;

    public ParkingRestControllerImpl(ParkingServiceAPI service) {
        this.service = service;
    }

    @PostMapping("/addCar")
    public CompletableFuture<ResponseEntity<JsonObject>> addCar(@RequestBody Car car) {
        logger.info("🚗 [REST] Adding car {}", car.getPlate());
        return service.addCar(new JsonObject().put("plate", car.getPlate()))
                .thenApply(result -> ResponseEntity.ok(result))
                .exceptionally(ex -> {
                    logger.error("❌ Error adding car: {}", ex.getMessage());
                    return ResponseEntity.internalServerError()
                            .body(new JsonObject().put("error", ex.getMessage()));
                });
    }

    @PostMapping("/removeCar")
    public CompletableFuture<ResponseEntity<JsonObject>> removeCar(@RequestBody Car car) {
        logger.info("🚙 [REST] Removing car {}", car.getPlate());
        return service.removeCar(new JsonObject().put("plate", car.getPlate()))
                .thenApply(result -> ResponseEntity.ok(result))
                .exceptionally(ex -> {
                    logger.error("❌ Error removing car: {}", ex.getMessage());
                    return ResponseEntity.internalServerError()
                            .body(new JsonObject().put("error", ex.getMessage()));
                });
    }

    @GetMapping("/get/{id}")
    public CompletableFuture<ResponseEntity<JsonObject>> getCar(@PathVariable String id) {
        logger.info("🔍 [REST] Getting car {}", id);
        return service.getCar(id)
                .thenApply(result -> ResponseEntity.ok(result))
                .exceptionally(ex -> ResponseEntity.internalServerError()
                        .body(new JsonObject().put("error", ex.getMessage())));
    }

    @GetMapping("/slots")
    public CompletableFuture<ResponseEntity<JsonObject>> getParkingSlots() {
        logger.info("📊 [REST] Getting parking slot info");
        return service.getParkingSlots()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError()
                        .body(new JsonObject().put("error", ex.getMessage())));
    }

    @GetMapping("/getEmbeddedStatus")
    public CompletableFuture<ResponseEntity<JsonObject>> getEmbeddedStatus() {
        logger.info("📶 [REST] Getting embedded device status");
        return service.getEmbeddedStatus()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError()
                        .body(new JsonObject().put("error", ex.getMessage())))
                .toCompletableFuture();
    }

    @Override
    public CompletableFuture<ResponseEntity<JsonObject>> openEntryBarrier(@RequestBody JsonObject payload) {
        logger.info("🔓 [REST] Opening entry barrier");
        return service.openEntryBarrier(payload)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError()
                        .body(new JsonObject().put("error", ex.getMessage())))
                .toCompletableFuture();
    }

    @Override
    public CompletableFuture<ResponseEntity<JsonObject>> openExitBarrier(@RequestBody JsonObject payload) {
        logger.info("🔓 [REST] Opening exit barrier");
        return service.openExitBarrier(payload)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError()
                        .body(new JsonObject().put("error", ex.getMessage())))
                .toCompletableFuture();
    }

    @Override
    @GetMapping("/getAllHistory")
    public CompletableFuture<ResponseEntity<JsonObject>> getAllHistory() {
        return service.getAllHistory()
                .thenApply(history -> ResponseEntity.ok(history));
    }
}

