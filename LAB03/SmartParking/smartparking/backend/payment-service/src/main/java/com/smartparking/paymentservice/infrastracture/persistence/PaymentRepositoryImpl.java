package com.smartparking.paymentservice.infrastracture.persistence;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.smartparking.paymentservice.application.ports.PaymentRepository;
import com.smartparking.paymentservice.model.FeeDataMessage;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class PaymentRepositoryImpl implements PaymentRepository {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRepositoryImpl.class);

    private static final String COLLECTION = "paymentservicedbcollection";
    private final MongoCollection<Document> collection;

    public PaymentRepositoryImpl(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("paymentservicedb");
        this.collection = database.getCollection(COLLECTION);
    }

    @Override
    public CompletableFuture<Void> setFee(FeeDataMessage fee) {
        return CompletableFuture.runAsync(() -> {
            try {
                Document doc = new Document()
                        .append("change", fee.getChange())
                        .append("hourFee", fee.getHourFee())
                        .append("dailyFee", fee.getDailyFee())
                        .append("thresholdHours", fee.getThresholdHours());

                collection.deleteMany(new Document());   // Keeping only 1 tariff
                collection.insertOne(doc);

                logger.info("💾 Tariff saved to MongoDB: {}", doc);

            } catch (Exception e) {
                logger.error("❌ Failed to save fee configuration: {}", e.getMessage());
                throw new RuntimeException("Failed to save fee configuration", e);
            }
        });
    }

    @Override
    public CompletableFuture<FeeDataMessage> getFee() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Document doc = collection.find().first();

                if (doc == null) {
                    throw new RuntimeException("No tariff configuration found");
                }

                FeeDataMessage msg = new FeeDataMessage(
                        doc.getString("change"),
                        doc.getDouble("hourFee"),
                        doc.getDouble("dailyFee"),
                        doc.getInteger("thresholdHours")
                );

                logger.info("📥 Tariff loaded from MongoDB: {}", msg);

                return msg;

            } catch (Exception e) {
                logger.error("❌ Failed to retrieve fee configuration: {}", e.getMessage());
                throw new RuntimeException("Failed to retrieve fee configuration", e);
            }
        });
    }
}