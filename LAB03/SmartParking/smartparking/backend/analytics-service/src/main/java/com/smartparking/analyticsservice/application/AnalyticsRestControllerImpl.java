package com.smartparking.analyticsservice.application;

import com.smartparking.analyticsservice.application.ports.AnalyticsRestController;
import com.smartparking.analyticsservice.application.ports.AnalyticsServiceAPI;
import com.smartparking.analyticsservice.model.ReportModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class AnalyticsRestControllerImpl implements AnalyticsRestController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsRestControllerImpl.class);

    private final AnalyticsServiceAPI analyticsService;

    public AnalyticsRestControllerImpl(AnalyticsServiceAPI analyticsService) {
        this.analyticsService = analyticsService;
        logger.info("✅ AnalyticsRestControllerImpl initialized");
    }

    @Override
    public CompletableFuture<ResponseEntity<ReportModel>> generateReport(
            @PathVariable("date") LocalDate date
    ) {
        logger.info("📥 GET /api/analytics/report/{} - Generate report request", date);

        return analyticsService.generateReport(date)
                .thenApply(report -> {
                    logger.info("✅ Report generated/retrieved successfully for date: {}", date);
                    return ResponseEntity.ok(report);
                })
                .exceptionally(ex -> {
                    logger.error("❌ Failed to generate report for date {}: {}", date, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @Override
    public CompletableFuture<ResponseEntity<ReportModel>> getReportByDate(
            @PathVariable("date") LocalDate date
    ) {
        logger.info("📥 GET /api/analytics/report/get/{} - Get report request", date);

        return analyticsService.getReportByDate(date)
                .thenApply(optionalReport -> {
                    if (optionalReport.isPresent()) {
                        logger.info("✅ Report found for date: {}", date);
                        return ResponseEntity.ok(optionalReport.get());
                    } else {
                        logger.warn("⚠️ Report not found for date: {}", date);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).<ReportModel>build();
                    }
                })
                .exceptionally(ex -> {
                    logger.error("❌ Failed to retrieve report for date {}: {}", date, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @Override
    public CompletableFuture<ResponseEntity<List<ReportModel>>> getAllReports() {
        logger.info("📥 GET /api/analytics/reports - Get all reports request");

        return analyticsService.getAllReports()
                .thenApply(reports -> {
                    logger.info("✅ Retrieved {} reports", reports.size());
                    return ResponseEntity.ok(reports);
                })
                .exceptionally(ex -> {
                    logger.error("❌ Failed to retrieve all reports: {}", ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }
}
