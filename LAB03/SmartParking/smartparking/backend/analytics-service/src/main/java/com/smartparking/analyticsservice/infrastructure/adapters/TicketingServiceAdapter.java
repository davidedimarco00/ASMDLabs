package com.smartparking.analyticsservice.infrastructure.adapters;

import com.smartparking.analyticsservice.application.ports.TicketingServicePort;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.concurrent.CompletableFuture;

public class TicketingServiceAdapter implements TicketingServicePort {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(TicketingServiceAdapter.class);

    public TicketingServiceAdapter() {
        this.webClient = WebClient.builder().build();
    }

    @Override
    public CompletableFuture<JsonObject> fetchTickets() {
        String url = "http://ticketing-service:8084/getAllTickets" ;
        logger.info("🔄 Calling TicketingService @ GET {}", url);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .doOnError(err -> logger.error("❌ Error calling TicketingService", err))
                .toFuture();
    }

    @Override
    public CompletableFuture<JsonObject> fetchHistoryTickets() {
        String url = "http://ticketing-service:8084/getAllHistoryTickets";
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .doOnError(err -> logger.error("❌ Error calling TicketingService (history)", err))
                .toFuture();
    }
}
