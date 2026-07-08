package com.smartparking.paymentservice.application;

import com.smartparking.paymentservice.application.ports.PaymentRestController;
import com.smartparking.paymentservice.application.ports.PaymentServiceAPI;
import com.smartparking.paymentservice.model.FeeDataMessage;
import com.smartparking.paymentservice.model.TicketMessage;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class PaymentRestControllerImpl implements PaymentRestController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRestControllerImpl.class);
    private final PaymentServiceAPI paymentService;

    public PaymentRestControllerImpl(PaymentServiceAPI paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public CompletableFuture<ResponseEntity<Object>> setTariff(FeeDataMessage feeData) {
        logger.info("💰 Setting new tariff configuration: {}", feeData);

        return paymentService.setActiveTariff(feeData)
                .thenApply(v -> ResponseEntity.ok().build())
                .exceptionally(ex -> {
                    logger.error("❌ Error setting tariff: {}", ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @Override
    public CompletableFuture<ResponseEntity<FeeDataMessage>> getTariff() {
        logger.info("💰 Retrieving active tariff configuration");

        return paymentService.getActiveTariff()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    logger.error("❌ Error retrieving tariff: {}", ex.getMessage());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @Override
    public CompletableFuture<ResponseEntity<TicketMessage>> processPayment(@PathVariable String NFCTag, @RequestBody TicketMessage ticket) {

        logger.info("💳 Processing payment for NFC Tag: {}", NFCTag);

        return paymentService.processPayment(NFCTag, ticket)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    logger.error("❌ Error processing payment for NFC Tag {}: {}", NFCTag, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new TicketMessage());
                });
    }

    @Override
    public CompletableFuture<ResponseEntity<JsonObject>> calculateFee(TicketMessage ticketMessage) {
        logger.info("💵 Calculating fee for ticket: {}", ticketMessage);

        return paymentService.calculateFee(ticketMessage)
                .thenApply(fee -> {
                    JsonObject resp = new JsonObject()
                            .put("fee", fee)
                            .put("plate", ticketMessage.getAssociatedWithPlate())
                            .put("status", "OK");

                    return ResponseEntity.ok(resp);
                })
                .exceptionally(ex -> {
                    logger.error("❌ Error calculating fee: {}", ex.getMessage());

                    JsonObject errorResp = new JsonObject()
                            .put("fee", 0.0)
                            .put("status", "ERROR");

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(errorResp);
                });
    }
}
