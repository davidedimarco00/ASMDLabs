package com.smartparking.analyticsservice.application.ports;

import com.smartparking.analyticsservice.model.ReportModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsServiceAPI {

    CompletableFuture<ReportModel> generateReport(LocalDate date);

    CompletableFuture<Optional<ReportModel>> getReportByDate(LocalDate date);

    CompletableFuture<List<ReportModel>> getAllReports();
}
