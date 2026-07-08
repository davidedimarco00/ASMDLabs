package com.smartparking.parkingservice.infrastracture.adapters;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class EmbeddedServiceAdapter implements EmbeddedClientPort {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedServiceAdapter.class);
    private final WebClient webClient;
    private static final String BASE_URL = "http://embedded-service:8086";

    public EmbeddedServiceAdapter(WebClient webclient) {
        this.webClient = webclient;
    }

    @Override
    public CompletableFuture<JsonObject> getEmbeddedStatus() {

        logger.info("Retrieve Status of the Embedded System");

        CompletableFuture<JsonObject> future = new CompletableFuture<>();

        webClient.getAbs(BASE_URL + "/status")
                .send()
                .onSuccess(response -> {
                    if (response.statusCode() == 200) {
                        JsonObject body = response.bodyAsJsonObject();
                        logger.info("✅ Embedded status retrieved successfully");
                        logger.debug("📦 Response: {}", body.encodePrettily());
                        future.complete(body);
                    } else {
                        String error = "Unexpected status code: " + response.statusCode();
                        logger.error("❌ {}", error);
                        future.completeExceptionally(new RuntimeException(error));
                    }
                })
                .onFailure(err -> {
                    logger.error("❌ Failed to call embedded service: {}", err.getMessage());
                    future.completeExceptionally(err);
                });

        return future;
    }

    @Override
    public CompletableFuture<JsonObject> openEntryBarrier(JsonObject payload) {
        logger.info("Opening the entry barrier");
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        webClient.postAbs(BASE_URL + "/open-entry-barrier")
                .sendJsonObject(payload)
                .onSuccess(response -> {
                    if (response.statusCode() == 200) {
                        JsonObject body = response.bodyAsJsonObject();
                        logger.info("✅ Entry barrier opened");
                        future.complete(body);
                    } else {
                        String error = "Status code: " + response.statusCode();
                        logger.error("❌ {}", error);
                        future.completeExceptionally(new RuntimeException(error));
                    }
                })
                .onFailure(err -> {
                    logger.error("❌ Error opening the entry barrier: {}", err.getMessage());
                    future.completeExceptionally(err);
                });
        return future;
    }

    @Override
    public CompletableFuture<JsonObject> openExitBarrier(JsonObject payload) {
        logger.info("Opening the exit barrier");
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        webClient.postAbs(BASE_URL + "/open-exit-barrier")
                .sendJsonObject(payload)
                .onSuccess(response -> {
                    if (response.statusCode() == 200) {
                        JsonObject body = response.bodyAsJsonObject();
                        logger.info("✅ Exit barrier opened");
                        future.complete(body);
                    } else {
                        String error = "Status code: " + response.statusCode();
                        logger.error("❌ {}", error);
                        future.completeExceptionally(new RuntimeException(error));
                    }
                })
                .onFailure(err -> {
                    logger.error("❌ Error opening the exit barrier: {}", err.getMessage());
                    future.completeExceptionally(err);
                });
        return future;
    }
}
