package com.smartparking.paymentservice.infrastracture.adapters;

import com.smartparking.paymentservice.application.ports.TicketingServicePort;
import com.smartparking.paymentservice.model.TicketMessage;
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
    public CompletableFuture<TicketMessage> updateTicket(TicketMessage ticketMessage) {

        String url = "http://ticketing-service:8084/updateStatusByPlate/" + ticketMessage.getAssociatedWithPlate() ;

        logger.info("🔄 Calling TicketingService @ POST {}", url);

        return webClient.post()
                .uri(url)
                .bodyValue(ticketMessage)
                .retrieve()
                .bodyToMono(TicketMessage.class)
                .doOnError(err -> logger.error("❌ Error calling TicketingService", err))
                .toFuture();
    }

    @Override
    public CompletableFuture<TicketMessage> addToHistory(TicketMessage ticketMessage) {
        String url = "http://ticketing-service:8084/addToHistory/" + ticketMessage.getAssociatedWithPlate() ;

        logger.info("🔄 Calling TicketingService HISTORY @ POST {}", url);

        return webClient.post()
                .uri(url)
                .bodyValue(ticketMessage)
                .retrieve()
                .bodyToMono(TicketMessage.class)
                .doOnError(err -> logger.error("❌ Error calling TicketingService HISTORY", err))
                .toFuture();
    }
}
