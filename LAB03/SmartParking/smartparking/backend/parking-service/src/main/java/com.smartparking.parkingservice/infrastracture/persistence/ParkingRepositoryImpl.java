package com.smartparking.parkingservice.infrastracture.persistence;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.smartparking.parkingservice.application.ports.ParkingServiceRepository;
import io.vertx.core.json.JsonObject;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CompletableFuture;

import static com.mongodb.client.model.Filters.eq;

public class ParkingRepositoryImpl implements ParkingServiceRepository {

    private static final Logger logger = LoggerFactory.getLogger(ParkingRepositoryImpl.class);
    private static final String COLLECTION = "parkingservicedbcollection";
    private static final String COLLECTION_HISTORY = "parkingservicedbcollectionhistory";

    private final MongoCollection<Document> collection;
    private final MongoCollection<Document> collectionHistory;

    public ParkingRepositoryImpl(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("parkingservicedb");
        this.collection = database.getCollection(COLLECTION);
        this.collectionHistory = database.getCollection(COLLECTION_HISTORY);

        logger.info("✅ ParkingRepositoryImpl initialized with DB: parkingservicedb, collection: {}, history: {}",
                COLLECTION, COLLECTION_HISTORY);
    }

    @Override
    public CompletableFuture<Void> save(JsonObject carData) {
        return CompletableFuture.runAsync(() -> {
            try {
                Document doc = Document.parse(carData.encode());
                collection.insertOne(doc);
                logger.info("🚗 Car inserted into MongoDB: {}", carData);
            } catch (Exception e) {
                logger.error("❌ Error inserting car into MongoDB: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteByPlate(String plate) {
        return CompletableFuture.runAsync(() -> {
            try {
                collection.deleteMany(eq("plate", plate));
                logger.info("🗑️ Car/Cars with plate {} removed from MongoDB", plate);
            } catch (Exception e) {
                logger.error("❌ Error deleting car with plate {}: {}", plate, e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<JsonObject> findById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Document doc = collection.find(eq("_id", id)).first();
                if (doc == null) {
                    logger.warn("⚠️ No car found in DB with id {}", id);
                    return null;
                }
                JsonObject car = new JsonObject(doc.toJson());
                logger.info("✅ Car retrieved from MongoDB: {}", car);
                return car;
            } catch (Exception e) {
                logger.error("❌ Error retrieving car {}: {}", id, e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<JsonObject> findByPlate(String plate) {
        return CompletableFuture.supplyAsync(() -> {
            Document doc = collection.find(new Document("plate", plate)).first();
            return doc != null ? new JsonObject(doc.toJson()) : null;
        });
    }

    @Override
    public CompletableFuture<Void> setExitedByPlate(String plate, String exitTimestamp) {
        return CompletableFuture.runAsync(() -> {
            try {
                Document original = collection.find(eq("plate", plate)).first();
                if (original == null) {
                    logger.warn("⚠️ No car with plate {} found to set EXITED.", plate);
                    return;
                }

                original.put("status", "EXITED");
                original.put("exitTimestamp", exitTimestamp);

                collectionHistory.insertOne(original);
                logger.info("📁 Car {} moved to HISTORY (parkingservicedbcollectionhistory)", plate);

                collection.deleteMany(eq("plate", plate));
                logger.info("🗑️ Car {} removed from main collection after EXITED", plate);

            } catch (Exception e) {
                logger.error("❌ Error moving car {} to history: {}", plate, e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<JsonObject> getAllHistory() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject response = new JsonObject();
                var historyArray = new io.vertx.core.json.JsonArray();

                for (Document doc : collectionHistory.find()) {
                    historyArray.add(new JsonObject(doc.toJson()));
                }

                response.put("history", historyArray);
                logger.info("📚 Retrieved {} history entries from Parking History", historyArray.size());

                return response;
            } catch (Exception e) {
                logger.error("❌ Error retrieving parking history: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
}

