package com.smartparking.parkingservice.application.ports;

import com.smartparking.parkingservice.llm.model.ParkingContext;
import com.smartparking.parkingservice.llm.model.ParkingDecision;
import com.smartparking.parkingservice.model.Car;
import io.vertx.core.json.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * Contratto REST esposto dal Parking Service.
 *
 * I mapping HTTP vengono dichiarati in questa interfaccia e implementati
 * da ParkingRestControllerImpl.
 */
public interface ParkingRestController {

    @PostMapping("/addCar")
    CompletableFuture<ResponseEntity<JsonObject>> addCar(
            @RequestBody Car carData
    );

    @PostMapping("/removeCar")
    CompletableFuture<ResponseEntity<JsonObject>> removeCar(
            @RequestBody Car carData
    );

    @GetMapping("/getCar/{id}")
    CompletableFuture<ResponseEntity<JsonObject>> getCar(
            @PathVariable("id") String id
    );

    @GetMapping("/getSlots")
    CompletableFuture<ResponseEntity<JsonObject>> getParkingSlots();

    @GetMapping("/getEmbeddedStatus")
    CompletableFuture<ResponseEntity<JsonObject>> getEmbeddedStatus();

    @PostMapping("/openEntryBarrier")
    CompletableFuture<ResponseEntity<JsonObject>> openEntryBarrier(
            @RequestBody JsonObject payload
    );

    @PostMapping("/openExitBarrier")
    CompletableFuture<ResponseEntity<JsonObject>> openExitBarrier(
            @RequestBody JsonObject payload
    );

    @GetMapping("/getAllHistory")
    CompletableFuture<ResponseEntity<JsonObject>> getAllHistory();

    /**
     * Endpoint sperimentale del laboratorio AI-APP DESIGN.
     *
     * Riceve il contesto del parcheggio e restituisce la decisione
     * suggerita dal componente basato su LLM.
     */
    @PostMapping("/llmEvaluate")
    CompletableFuture<ResponseEntity<ParkingDecision>> evaluate(@RequestBody ParkingContext context);
}
