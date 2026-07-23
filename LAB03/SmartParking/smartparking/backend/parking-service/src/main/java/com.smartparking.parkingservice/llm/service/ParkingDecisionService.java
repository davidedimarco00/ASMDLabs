package com.smartparking.parkingservice.llm.service;

import com.smartparking.parkingservice.llm.model.ParkingContext;
import com.smartparking.parkingservice.llm.model.ParkingDecision;

import java.util.concurrent.CompletableFuture;

/**
 * Contratto del componente che interpreta un contesto del parcheggio
 * e propone una decisione.
 */
public interface ParkingDecisionService {

    CompletableFuture<ParkingDecision> evaluate(ParkingContext context);
}