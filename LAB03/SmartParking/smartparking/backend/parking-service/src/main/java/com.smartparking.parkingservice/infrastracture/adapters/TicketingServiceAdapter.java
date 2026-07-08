package com.smartparking.parkingservice.infrastracture.adapters;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class TicketingServiceAdapter implements TicketingClientPort {

    private static final Logger logger = LoggerFactory.getLogger(TicketingServiceAdapter.class);
    private final WebClient webClient;
    private static final String BASE_URL = "http://ticketing-service:8084";

    public TicketingServiceAdapter(WebClient webclient) {
        this.webClient = webclient;
    }

    @Override
    public CompletableFuture<JsonObject> createVirtualTicket(String plate) {
        logger.info("Calling Ticketing Service to create ticket for plate: {}", plate);

        JsonObject requestBody = new JsonObject().put("associatedWithPlate", plate);

        CompletableFuture<JsonObject> future = new CompletableFuture<>();

        webClient.postAbs(BASE_URL + "/createVirtualTicket")
                .putHeader("Content-Type", "application/json")
                .as(BodyCodec.jsonObject())
                .sendJsonObject(requestBody, ar -> {
                    if (ar.succeeded()) {
                        JsonObject response = ar.result().body();
                        logger.info("🎟️ Ticket created successfully: {}", response.encodePrettily());
                        future.complete(response);
                    } else {
                        logger.error("❌ Failed to contact Ticketing-Service: {}", ar.cause().getMessage());
                        future.completeExceptionally(ar.cause());
                    }
                });

        return future;
    }

    @Override
    public CompletableFuture<JsonObject> verifyPaymentByPlate(String plate) {
        logger.info("Calling Ticketing Service to verify ticket for plate: {}", plate);

        JsonObject requestBody = new JsonObject().put("associatedWithPlate", plate);

        CompletableFuture<JsonObject> future = new CompletableFuture<>();

        webClient.getAbs(BASE_URL + "/verifyPaymentByPlate/"+plate)
                .putHeader("Content-Type", "application/json")
                .as(BodyCodec.jsonObject())
                .sendJsonObject(requestBody, ar -> {
                    if (ar.succeeded()) {
                        JsonObject response = ar.result().body();
                        logger.info("🎟️ Ticket retrievied successfully: {}", response.encodePrettily());

                        future.complete(response);
                    } else {
                        logger.error("❌ Failed to contact Ticketing-Service: {}", ar.cause().getMessage());
                        future.completeExceptionally(ar.cause());
                    }
                });

        return future;
    }
}