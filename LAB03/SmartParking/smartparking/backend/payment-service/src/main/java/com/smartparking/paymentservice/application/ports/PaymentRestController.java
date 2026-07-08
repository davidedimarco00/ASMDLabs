package com.smartparking.paymentservice.application.ports;

import com.smartparking.paymentservice.model.FeeDataMessage;
import com.smartparking.paymentservice.model.TicketMessage;
import io.vertx.core.json.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

public interface PaymentRestController {

    @PostMapping("/setTariff")
    CompletableFuture<ResponseEntity<Object>> setTariff(@RequestBody FeeDataMessage tariffConfig);

    @GetMapping("/getTariff")
    CompletableFuture<ResponseEntity<FeeDataMessage>> getTariff();

    @PostMapping("/processPayment/{NFCTag}")
    CompletableFuture<ResponseEntity<TicketMessage>> processPayment(@PathVariable String NFCTag, @RequestBody TicketMessage ticket);

    @PostMapping("/calculateFee")
    CompletableFuture<ResponseEntity<JsonObject>> calculateFee(@RequestBody TicketMessage ticketMessage);

}
