package com.smartparking.paymentservice.application.ports;

import com.smartparking.paymentservice.model.TicketMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public interface TicketingServicePort {

    CompletableFuture<TicketMessage> updateTicket(TicketMessage ticketMessage);

    CompletableFuture<TicketMessage> addToHistory(TicketMessage ticketMessage);
}
