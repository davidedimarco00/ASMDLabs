package com.smartparking.parkingservice.application;

import com.smartparking.parkingservice.application.ports.ParkingRestController;
import com.smartparking.parkingservice.application.ports.ParkingServiceAPI;
import com.smartparking.parkingservice.llm.model.ParkingContext;
import com.smartparking.parkingservice.llm.model.ParkingDecision;
import com.smartparking.parkingservice.llm.service.ParkingDecisionService;
import com.smartparking.parkingservice.model.Car;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class ParkingRestControllerImpl implements ParkingRestController {

    private static final Logger logger =
            LoggerFactory.getLogger(ParkingRestControllerImpl.class);

    private final ParkingServiceAPI parkingService;
    private final ParkingDecisionService parkingDecisionService;

    /**
     * Le dipendenze vengono inserite automaticamente da Spring.

     * ParkingDecisionService gestisce la valutazione tramite LLM.
     */
    public ParkingRestControllerImpl(
            ParkingServiceAPI parkingService,
            ParkingDecisionService parkingDecisionService
    ) {
        this.parkingService = parkingService;
        this.parkingDecisionService = parkingDecisionService;
    }

    @Override
    public CompletableFuture<ResponseEntity<JsonObject>> addCar(Car car) {
        logger.info("🚗 [REST] Adding car {}", car.getPlate());

        JsonObject request = new JsonObject()
                .put("plate", car.getPlate());

        return parkingService.addCar(request)
                .thenApply(ResponseEntity::ok)
                .exceptionally(exception -> {
                    logger.error(
                            "❌ Error adding car {}: {}",
                            car.getPlate(),
                            exception.getMessage()
                    );

                    return ResponseEntity.internalServerError()
                            .body(new JsonObject()
                                    .put("error", exception.getMessage()));
                });
    }

    @Override
    public CompletableFuture<ResponseEntity<JsonObject>> removeCar(Car car) {
        logger.info("🚙 [REST] Removing car {}", car.getPlate());

        JsonObject request = new JsonObject()
                .put("plate", car.getPlate());

        return parkingService.removeCar(request)
                .thenApply(ResponseEntity::ok)
                .exceptionally(exception -> {
                    logger.error(
                            "❌ Error removing car {}: {}",
                            car.getPlate(),
                            exception.getMessage()
                    );

                    return ResponseEntity.internalServerError()
                            .body(new JsonObject()
                                    .put("error", exception.getMessage()));
                });
    }

    @Override
    public CompletableFuture<ResponseEntity<JsonObject>> getCar(String id) {
        logger.info("🔍 [REST] Getting car {}", id);

        return parkingService.getCar(id)
                .thenApply(ResponseEntity::ok)
                .exceptionally(exception -> {
                    logger.error(
                            "❌ Error getting car {}: {}",
                            id,
                            exception.getMessage()
                    );

                    return ResponseEntity.internalServerError()
                            .body(new JsonObject()
                                    .put("error", exception.getMessage()));
                });
    }

    @Override
    public CompletableFuture<ResponseEntity<JsonObject>> getParkingSlots() {
        logger.info("📊 [REST] Getting parking slot information");

        return parkingService.getParkingSlots()
                .thenApply(ResponseEntity::ok)
                .exceptionally(exception -> {
                    logger.error(
                            "❌ Error getting parking slots: {}",
                            exception.getMessage()
                    );

                    return ResponseEntity.internalServerError()
                            .body(new JsonObject()
                                    .put("error", exception.getMessage()));
                });
    }

    @Override
    public CompletableFuture<ResponseEntity<JsonObject>> getEmbeddedStatus() {
        logger.info("📶 [REST] Getting embedded device status");

        return parkingService.getEmbeddedStatus()
                .thenApply(ResponseEntity::ok)
                .exceptionally(exception -> {
                    logger.error(
                            "❌ Error getting embedded status: {}",
                            exception.getMessage()
                    );

                    return ResponseEntity.internalServerError()
                            .body(new JsonObject()
                                    .put("error", exception.getMessage()));
                })
                .toCompletableFuture();
    }

    @Override
    public CompletableFuture<ResponseEntity<JsonObject>> openEntryBarrier(
            JsonObject payload
    ) {
        logger.info("🔓 [REST] Opening entry barrier");

        return parkingService.openEntryBarrier(payload)
                .thenApply(ResponseEntity::ok)
                .exceptionally(exception -> {
                    logger.error(
                            "❌ Error opening entry barrier: {}",
                            exception.getMessage()
                    );

                    return ResponseEntity.internalServerError()
                            .body(new JsonObject()
                                    .put("error", exception.getMessage()));
                })
                .toCompletableFuture();
    }

    @Override
    public CompletableFuture<ResponseEntity<JsonObject>> openExitBarrier(
            JsonObject payload
    ) {
        logger.info("🔓 [REST] Opening exit barrier");

        return parkingService.openExitBarrier(payload)
                .thenApply(ResponseEntity::ok)
                .exceptionally(exception -> {
                    logger.error(
                            "❌ Error opening exit barrier: {}",
                            exception.getMessage()
                    );

                    return ResponseEntity.internalServerError()
                            .body(new JsonObject()
                                    .put("error", exception.getMessage()));
                })
                .toCompletableFuture();
    }

    @Override
    public CompletableFuture<ResponseEntity<JsonObject>> getAllHistory() {
        logger.info("📚 [REST] Getting parking history");

        return parkingService.getAllHistory()
                .thenApply(ResponseEntity::ok)
                .exceptionally(exception -> {
                    logger.error(
                            "❌ Error getting parking history: {}",
                            exception.getMessage()
                    );

                    return ResponseEntity.internalServerError()
                            .body(new JsonObject()
                                    .put("error", exception.getMessage()));
                });
    }

    @Override
    public CompletableFuture<ResponseEntity<ParkingDecision>> evaluate(@RequestBody ParkingContext context) {
        logger.info("🤖 [REST] Evaluating parking context with LLM: {}", context);

        return parkingDecisionService.evaluate(context)
                .thenApply(decision -> {
                    logger.info(
                            "✅ [REST] LLM decision: classification={}, action={}, confidence={}",
                            decision.getClassification(),
                            decision.getSuggestedAction(),
                            decision.getConfidence()
                    );

                    return ResponseEntity.ok(decision);
                })
                .exceptionally(exception -> {
                    logger.error(
                            "❌ Error evaluating parking context with LLM: {}",
                            exception.getMessage()
                    );

                    ParkingDecision fallbackDecision = new ParkingDecision(
                            "UNKNOWN",
                            "REQUEST_MANUAL_REVIEW",
                            "LLM evaluation failed. Manual review is required.",
                            0.0
                    );

                    return ResponseEntity.internalServerError()
                            .body(fallbackDecision);
                });
    }
}