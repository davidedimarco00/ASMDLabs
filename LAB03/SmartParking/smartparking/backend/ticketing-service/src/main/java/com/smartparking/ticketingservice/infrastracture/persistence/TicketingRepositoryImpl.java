package com.smartparking.ticketingservice.infrastracture.persistence;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.smartparking.ticketingservice.application.ports.TicketingRepository;
import com.smartparking.ticketingservice.model.TicketMessage;
import io.vertx.core.json.JsonObject;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TicketingRepositoryImpl implements TicketingRepository {

    private static final String COLLECTION = "ticketingservicedbcollection";
    private static final String COLLECTIONHISTORY = "ticketingservicedbcollectionhistory";
    private final MongoCollection<Document> collection;
    private final MongoCollection<Document> collectionHistory;

    public TicketingRepositoryImpl(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("ticketingservicedb");
        this.collection = database.getCollection(COLLECTION);
        this.collectionHistory = database.getCollection(COLLECTIONHISTORY);
    }

    @Override
    public CompletableFuture<Void> save(JsonObject ticket) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            if (ticket == null || !ticket.containsKey("associatedWithPlate")) {
                throw new IllegalArgumentException("Invalid ticket data");
            }

            Document doc = Document.parse(ticket.encode());
            doc.put("createdAt",
                    java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            doc.put("timestamp", System.currentTimeMillis());

            collection.insertOne(doc);
            future.complete(null);
        } catch (Exception e) {
            future.completeExceptionally(new RuntimeException("Failed to save ticket: " + e.getMessage(), e));
        }
        return future;
    }

    @Override
    public CompletableFuture<Optional<JsonObject>> findById(String id) {
        CompletableFuture<Optional<JsonObject>> future = new CompletableFuture<>();
        try {
            Document query = new Document("_id", id);
            Document result = collection.find(query).first();
            future.complete(Optional.ofNullable(result != null ? new JsonObject(result.toJson()) : null));
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<JsonObject> update(String id, JsonObject updatedData) {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        try {
            Bson filter = new Document("_id", id);
            Bson update = new Document("$set", Document.parse(updatedData.encode()));
            collection.updateOne(filter, update);
            future.complete(updatedData);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<Optional<JsonObject>> findByPlate(String plate) {
        CompletableFuture<Optional<JsonObject>> future = new CompletableFuture<>();
        try {
            if (plate == null || plate.isBlank()) {
                throw new IllegalArgumentException("Invalid plate value");
            }

            Document query = new Document("associatedWithPlate", plate);
            Document result = collection.find(query).sort(new Document("timestamp", -1)).first();

            if (result != null) {
                future.complete(Optional.of(new JsonObject(result.toJson())));
            } else {
                future.complete(Optional.empty());
            }
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<JsonObject> updateByPlate(String plate, JsonObject updatedData) {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        try {
            if (plate == null || plate.isBlank()) {
                throw new IllegalArgumentException("Invalid plate value");
            }

            Document filter = new Document("associatedWithPlate", plate);
            Document update = new Document("$set", Document.parse(updatedData.encode()));

            collection.updateOne(filter, update);
            future.complete(updatedData);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    @Override
    public CompletableFuture<JsonObject> getAllTickets() {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        try {
            var cursor = collection.find().iterator();
            io.vertx.core.json.JsonArray ticketsArray = new io.vertx.core.json.JsonArray();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                ticketsArray.add(new JsonObject(doc.toJson()));
            }

            JsonObject response = new JsonObject()
                    .put("count", ticketsArray.size())
                    .put("tickets", ticketsArray);

            future.complete(response);
        } catch (Exception e) {
            future.completeExceptionally(new RuntimeException("Failed to retrieve all tickets: " + e.getMessage(), e));
        }
        return future;
    }

    @Override
    public CompletableFuture<JsonObject> saveToHistoryCollection(TicketMessage ticket) {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();

        try {
            if (ticket == null || ticket.getAssociatedWithPlate() == null) {
                throw new IllegalArgumentException("Invalid ticket: missing plate");
            }

            String plate = ticket.getAssociatedWithPlate();
            String type =  ticket.getType();

            JsonObject json = JsonObject.mapFrom(ticket);
            Document doc = Document.parse(json.encode());

            doc.put("historyAddedAt",
                    java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            doc.put("historyTimestamp", System.currentTimeMillis());

            collectionHistory.insertOne(doc);

            Document deleteFilter = new Document("associatedWithPlate", plate);
            collection.deleteMany(deleteFilter);
            System.out.println("Removed plate from current tickets:  " + plate);

            future.complete(json);

        } catch (Exception e) {
            future.completeExceptionally(
                    new RuntimeException("Failed to move ticket to history: " + e.getMessage(), e));
        }
        return future;
    }

    @Override
    public CompletableFuture<JsonObject> getAllHistoryTickets() {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        try {
            var cursor = collectionHistory.find().iterator();
            io.vertx.core.json.JsonArray ticketsArray = new io.vertx.core.json.JsonArray();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                ticketsArray.add(new JsonObject(doc.toJson()));
            }

            JsonObject response = new JsonObject()
                    .put("count", ticketsArray.size())
                    .put("tickets", ticketsArray);

            future.complete(response);
        } catch (Exception e) {
            future.completeExceptionally(new RuntimeException("Failed to retrieve all tickets: " + e.getMessage(), e));
        }
        return future;
    }

    @Override
    public CompletableFuture<Optional<JsonObject>> findHistoryByPlate(String plate) {
        CompletableFuture<Optional<JsonObject>> future = new CompletableFuture<>();
        try {
            if (plate == null || plate.isBlank()) {
                throw new IllegalArgumentException("Invalid plate value");
            }

            Document query = new Document("associatedWithPlate", plate);
            Document result = collectionHistory.find(query).sort(new Document("timestamp", -1)).first();

            if (result != null) {
                future.complete(Optional.of(new JsonObject(result.toJson())));
            } else {
                future.complete(Optional.empty());
            }
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }
}
