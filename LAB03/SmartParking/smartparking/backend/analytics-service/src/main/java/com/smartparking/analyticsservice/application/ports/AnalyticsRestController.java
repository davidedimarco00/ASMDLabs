package com.smartparking.analyticsservice.application.ports;

import com.smartparking.analyticsservice.model.ReportModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsRestController {

    @GetMapping("/report/{date}")
    CompletableFuture<ResponseEntity<ReportModel>> generateReport(
            @PathVariable("date") LocalDate date
    );

    @GetMapping("/report/get/{date}")
    CompletableFuture<ResponseEntity<ReportModel>> getReportByDate(
            @PathVariable("date") LocalDate date
    );

    @GetMapping("/reports")
    CompletableFuture<ResponseEntity<List<ReportModel>>> getAllReports();
}
