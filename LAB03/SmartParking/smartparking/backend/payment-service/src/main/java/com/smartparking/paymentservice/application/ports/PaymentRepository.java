package com.smartparking.paymentservice.application.ports;

import com.smartparking.paymentservice.model.FeeDataMessage;

import java.util.concurrent.CompletableFuture;

public interface PaymentRepository {

    CompletableFuture<Void> setFee(FeeDataMessage fee);

    CompletableFuture<FeeDataMessage> getFee();
}
