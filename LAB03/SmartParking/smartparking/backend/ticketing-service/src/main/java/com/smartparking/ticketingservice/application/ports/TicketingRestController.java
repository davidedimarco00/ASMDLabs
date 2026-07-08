package com.smartparking.ticketingservice.application.ports;

import com.smartparking.ticketingservice.model.TicketMessage;
import io.vertx.core.json.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

public interface TicketingRestController {

    @PostMapping("/createTicket")
    CompletableFuture<ResponseEntity<TicketMessage>> createTicket(@RequestBody TicketMessage request);

    @PostMapping("/createVirtualTicket")
    CompletableFuture<ResponseEntity<TicketMessage>> createVirtualTicket(@RequestBody TicketMessage request);

    @GetMapping("/ticket/{id}")
    CompletableFuture<ResponseEntity<TicketMessage>> getTicket(@PathVariable("id") String id);

    @GetMapping("/virtualTicket/{id}")
    CompletableFuture<ResponseEntity<TicketMessage>> getVirtualTicket(@PathVariable("id") String id);

    @PostMapping("/ticket/{id}")
    CompletableFuture<ResponseEntity<TicketMessage>> updateTicket(@PathVariable("id") String id, @RequestBody TicketMessage request);

    @PostMapping("/virtualTicket/{id}")
    CompletableFuture<ResponseEntity<TicketMessage>> updateVirtualTicket(@PathVariable("id") String id,
                                                                         @RequestBody TicketMessage request);
    @GetMapping("/ticket/getByPlate/{plate}")
    CompletableFuture<ResponseEntity<TicketMessage>> getTicketByPlate(@PathVariable("plate") String plate);

    @GetMapping("/verifyPaymentByPlate/{plate}")
    CompletableFuture<ResponseEntity<TicketMessage>> verifyPayment(@PathVariable("plate") String plate);

    @GetMapping("/getAllTickets")
    CompletableFuture<JsonObject> getAllTicket();

    @GetMapping("/getAllHistoryTickets")
    CompletableFuture<JsonObject> getAllHistoryTicket();

    @PostMapping("/updateStatus/{plate}")
    CompletableFuture<ResponseEntity<TicketMessage>> updateStatusByPlate(@PathVariable("plate") String plate, @RequestBody TicketMessage request);


    @PostMapping("/addToHistory/{plate}")
    CompletableFuture<ResponseEntity<TicketMessage>> addToHistory( @PathVariable("plate") String plate, @RequestBody TicketMessage request);


}
