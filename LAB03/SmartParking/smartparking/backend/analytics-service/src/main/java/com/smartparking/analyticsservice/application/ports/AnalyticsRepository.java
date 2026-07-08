package com.smartparking.analyticsservice.application.ports;

import com.smartparking.analyticsservice.model.ReportModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


public interface AnalyticsRepository {

    CompletableFuture<Optional<ReportModel>> findByDate(LocalDate date);

    CompletableFuture<ReportModel> save(ReportModel report);

    CompletableFuture<List<ReportModel>> findAll();

}
