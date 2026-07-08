package com.smartparking.paymentservice.application.ports;

import com.smartparking.paymentservice.model.FeeDataMessage;
import com.smartparking.paymentservice.model.TicketMessage;
import java.util.concurrent.CompletableFuture;

public interface PaymentServiceAPI {

    CompletableFuture<Void> setActiveTariff(FeeDataMessage tariffConfig);

    CompletableFuture<FeeDataMessage> getActiveTariff();

    CompletableFuture<TicketMessage> processPayment(String NFCTag, TicketMessage ticket);

    CompletableFuture<Double> calculateFee(TicketMessage ticketMessage);
}
