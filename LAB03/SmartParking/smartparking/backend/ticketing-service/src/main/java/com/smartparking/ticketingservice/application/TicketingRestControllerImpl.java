package com.smartparking.ticketingservice.application;

import com.smartparking.ticketingservice.application.ports.TicketingRestController;
import com.smartparking.ticketingservice.application.ports.TicketingServiceAPI;
import com.smartparking.ticketingservice.model.TicketMessage;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController

public class TicketingRestControllerImpl implements TicketingRestController {

    private static final Logger logger = LoggerFactory.getLogger(TicketingRestControllerImpl.class);
    private final TicketingServiceAPI service;

    public TicketingRestControllerImpl(TicketingServiceAPI service) {
        this.service = service;
    }

    @Override
    @PostMapping("/createTicket")
    public CompletableFuture<ResponseEntity<TicketMessage>> createTicket(@RequestBody TicketMessage request) {
        logger.info("🎫 Creating ticket for plate: {}", request.getAssociatedWithPlate());
        return service.createTicket(request)
                .handle((result, ex) -> ex != null
                        ? ResponseEntity.internalServerError().body(null)
                        : ResponseEntity.ok(result));
    }

    @Override
    @PostMapping("/createVirtualTicket")
    public CompletableFuture<ResponseEntity<TicketMessage>> createVirtualTicket(@RequestBody TicketMessage request) {
        logger.info("💻 Creating virtual ticket for plate: {}", request.getAssociatedWithPlate());
        if (request.getAssociatedWithPlate() == null) {
            TicketMessage error = new TicketMessage();
            error.setStatus("ERROR: associatedWithPlate is null or blank");
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(error)
            );
        }
        return service.createVirtualTicket(request)
                .handle((result, ex) -> ex != null
                        ? ResponseEntity.internalServerError().body(null)
                        : ResponseEntity.ok(result));
    }

    @Override
    @GetMapping("/ticket/{id}")
    public CompletableFuture<ResponseEntity<TicketMessage>> getTicket(@PathVariable String id) {
        logger.info("🔍 Retrieving ticket {}", id);
        return service.getTicketById(id)
                .handle((ticket, ex) -> {
                    if (ex != null) return ResponseEntity.internalServerError().body(null);
                    if (ticket == null) return ResponseEntity.notFound().build();
                    return ResponseEntity.ok(ticket);
                });
    }

    @Override
    @GetMapping("/virtualTicket/{id}")
    public CompletableFuture<ResponseEntity<TicketMessage>> getVirtualTicket(@PathVariable String id) {
        logger.info("🔍 Retrieving virtual ticket {}", id);
        return service.getVirtualTicketById(id)
                .handle((ticket, ex) -> {
                    if (ex != null) return ResponseEntity.internalServerError().body(null);
                    if (ticket == null) return ResponseEntity.notFound().build();
                    return ResponseEntity.ok(ticket);
                });
    }

    @Override
    @PostMapping("/ticket/{id}")
    public CompletableFuture<ResponseEntity<TicketMessage>> updateTicket(@PathVariable String id,
                                                                         @RequestBody TicketMessage request) {
        logger.info("✏️ Updating ticket {} exitDetected={}", id, request.isExitDetected());
        return service.updateTicket(id, request)
                .handle((updated, ex) -> ex != null
                        ? ResponseEntity.internalServerError().body(null)
                        : ResponseEntity.ok(updated));
    }

    @Override
    @PostMapping("/virtualTicket/{id}")
    public CompletableFuture<ResponseEntity<TicketMessage>> updateVirtualTicket(@PathVariable String id,
                                                                                @RequestBody TicketMessage request) {
        logger.info("✏️ Updating virtual ticket {} exitDetected={}", id, request.isExitDetected());
        return service.updateVirtualTicket(id, request)
                .handle((updated, ex) -> ex != null
                        ? ResponseEntity.internalServerError().body(null)
                        : ResponseEntity.ok(updated));
    }

    @Override
    @GetMapping("/getTicketByPlate/{plate}")
    public CompletableFuture<ResponseEntity<TicketMessage>> getTicketByPlate(@PathVariable("plate") String plate) {
        logger.info("🔎 Searching ticket by plate: {}", plate);
        return service.getTicketByPlate(plate)
                .handle((ticket, ex) -> {
                    if (ex != null) {
                        logger.error("❌ Error retrieving ticket by plate {}: {}", plate, ex.getMessage());
                        return ResponseEntity.internalServerError().body(null);
                    }
                    if (ticket == null) {

                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok(ticket);
                });
    }

    @GetMapping("/verifyPaymentByPlate/{plate}")
    @Override
    public CompletableFuture<ResponseEntity<TicketMessage>> verifyPayment(@PathVariable("plate") String plate) {
        logger.info("🔎 Verify ticket by plate: {}", plate);
        return service.getHistoryTicketByPlate(plate)
                .handle((ticket, ex) -> {
                    if (ex != null) {
                        logger.error("❌ Error verifyng ticket by plate {}: {}", plate, ex.getMessage());
                        return ResponseEntity.internalServerError().body(null);
                    }
                    if (ticket == null) {

                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok(ticket);
                });
    }

    @Override
    @GetMapping("/getAllTickets")
    public CompletableFuture<JsonObject> getAllTicket() {
        logger.info("Returning all tickets");
        return service.getAllTickets();
    }

    @GetMapping("/getAllHistoryTickets")
    @Override
    public CompletableFuture<JsonObject> getAllHistoryTicket() {

        logger.info("Returning all HISTORY tickets");
        return service.getAllHistoryTickets();
    }

    @Override
    @PostMapping("/updateStatusByPlate/{plate}")
    public CompletableFuture<ResponseEntity<TicketMessage>> updateStatusByPlate(
            @PathVariable("plate") String plate,
            @RequestBody TicketMessage request) {

        logger.info("🔄 Updating ticket status for plate {} to {}, exitDetected={}, fee = {}",
                plate, request.getStatus(), request.isExitDetected(), request.getFee()) ;

        return service.updateStatusByPlate(plate, request)
                .handle((updated, ex) -> {
                    if (ex != null) {
                        logger.error("❌ Error updating ticket for plate {}: {}", plate, ex.getMessage());
                        return ResponseEntity.internalServerError().body(null);
                    }
                    if (updated == null) {
                        logger.warn("⚠️ No ticket found for plate {}", plate);
                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok(updated);
                });
    }

    @Override
    @PostMapping("/addToHistory/{plate}")
    public CompletableFuture<ResponseEntity<TicketMessage>> addToHistory(
            @PathVariable("plate") String plate,
            @RequestBody TicketMessage request) {

        logger.info("📚 Adding ticket to history for plate {} with status {}",
                plate, request.getStatus());

        return service.addToHistory(plate, request)
                .handle((saved, ex) -> {
                    if (ex != null) {
                        logger.error("❌ Error adding ticket to history for plate {}: {}",
                                plate, ex.getMessage());
                        return ResponseEntity.internalServerError().body(null);
                    }
                    if (saved == null) {
                        logger.warn("⚠️ No ticket found for plate {}. Cannot add to history.", plate);
                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok(saved);
                });
    }

}