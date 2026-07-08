package com.smartparking.analyticsservice.infrastructure.persistence;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.smartparking.analyticsservice.application.ports.AnalyticsRepository;
import com.smartparking.analyticsservice.model.ReportModel;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AnalyticsRepositoryImpl implements AnalyticsRepository {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsRepositoryImpl.class);
    private static final String COLLECTION_NAME = "reports";

    private final MongoCollection<Document> reportsCollection;

    public AnalyticsRepositoryImpl(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("analyticsservicedb");
        this.reportsCollection = database.getCollection(COLLECTION_NAME);
        initializeIndexes();
    }

    private void initializeIndexes() {
        try {
            reportsCollection.createIndex(
                    Indexes.ascending("date"),
                    new IndexOptions().unique(true)
            );
            logger.info("✅ MongoDB indexes created successfully for reports collection");
        } catch (Exception e) {
            logger.error("❌ Failed to create indexes: {}", e.getMessage());
        }
    }

    @Override
    public CompletableFuture<Optional<ReportModel>> findByDate(LocalDate date) {
        CompletableFuture<Optional<ReportModel>> future = new CompletableFuture<>();

        try {
            if (date == null) {
                throw new IllegalArgumentException("Date cannot be null");
            }

            Document query = new Document("date", date.toString());
            Document result = reportsCollection.find(query).first();

            logger.debug("Report lookup for date '{}': {}", date, result != null ? "found" : "not found");

            Optional<ReportModel> report = Optional.empty();
            if (result != null) {
                report = Optional.of(documentToReportModel(result));
            }

            future.complete(report);
        } catch (Exception e) {
            logger.error("Error finding report for date '{}': {}", date, e.getMessage());
            future.completeExceptionally(
                    new RuntimeException("Failed to find report: " + e.getMessage(), e));
        }

        return future;
    }

    @Override
    public CompletableFuture<ReportModel> save(ReportModel report) {
        CompletableFuture<ReportModel> future = new CompletableFuture<>();

        try {
            if (report == null || report.getDate() == null) {
                throw new IllegalArgumentException("Invalid report or date");
            }

            Document doc = reportModelToDocument(report);
            reportsCollection.insertOne(doc);

            logger.info("✅ Report saved for date: {}", report.getDate());
            future.complete(report);
        } catch (Exception e) {
            logger.error("Failed to save report for date '{}': {}",
                    report != null ? report.getDate() : "null", e.getMessage());
            future.completeExceptionally(
                    new RuntimeException("Failed to save report: " + e.getMessage(), e));
        }

        return future;
    }

    @Override
    public CompletableFuture<List<ReportModel>> findAll() {
        CompletableFuture<List<ReportModel>> future = new CompletableFuture<>();

        try {
            List<ReportModel> reports = new ArrayList<>();

            for (Document doc : reportsCollection.find()) {
                reports.add(documentToReportModel(doc));
            }

            logger.debug("Retrieved {} reports from database", reports.size());
            future.complete(reports);
        } catch (Exception e) {
            logger.error("Error retrieving all reports: {}", e.getMessage());
            future.completeExceptionally(
                    new RuntimeException("Failed to retrieve reports: " + e.getMessage(), e));
        }

        return future;
    }

    private ReportModel documentToReportModel(Document doc) {
        ReportModel report = new ReportModel();
        report.setDate(LocalDate.parse(doc.getString("date")));
        report.setVirtualTicketsGenerated(doc.getInteger("virtualTicketsGenerated", 0));
        report.setVirtualTicketsCompleted(doc.getInteger("virtualTicketsCompleted", 0));
        report.setTotalRevenue(doc.getDouble("totalRevenue"));
        report.setRawData(doc.getString("rawData"));
        return report;
    }

    private Document reportModelToDocument(ReportModel report) {
        Document doc = new Document()
                .append("date", report.getDate().toString())
                .append("virtualTicketsGenerated", report.getVirtualTicketsGenerated())
                .append("virtualTicketsCompleted", report.getVirtualTicketsCompleted())
                .append("totalRevenue", report.getTotalRevenue())
                .append("rawData", report.getRawData());
        return doc;
    }

}
