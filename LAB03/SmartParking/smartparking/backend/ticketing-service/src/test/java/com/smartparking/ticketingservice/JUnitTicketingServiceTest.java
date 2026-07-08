package com.smartparking.ticketingservice;

import com.mongodb.client.*;
import com.smartparking.ticketingservice.model.TicketMessage;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JUnitTicketingServiceTest {

    private final String baseUrl = "http://localhost:8084";
    private final RestTemplate rest = new RestTemplate();

    private static final String mongoUrl = "mongodb://localhost:27020";
    private static final String databaseName = "ticketingservicedb";
    private static final String collectionName = "ticketingservicedbcollection";

    private static final String PLATE = "AB123CD";
    private static String createdTicketId;

    private void cleanMongo() {
        try (MongoClient client = MongoClients.create(mongoUrl)) {
            MongoDatabase db = client.getDatabase(databaseName);
            MongoCollection<Document> collection = db.getCollection(collectionName);
            collection.deleteMany(new Document());  // DELETE ALL
            System.out.println("🔥 Mongo cleaned!");
        }
    }

    @BeforeAll
    static void cleanMongoOnce() {
        try (MongoClient client = MongoClients.create(mongoUrl)) {
            MongoDatabase db = client.getDatabase(databaseName);
            MongoCollection<Document> collection = db.getCollection(collectionName);
            collection.deleteMany(new Document());  // DELETE ALL DOCUMENTS
            System.out.println("🔥 Mongo cleaned BEFORE ALL tests.");
        }
    }

    @Test
    @Order(1)
    void testCreateTicket() {
        TicketMessage request = new TicketMessage();
        request.setAssociatedWithPlate(PLATE);
        request.setExitDetected(false);

        ResponseEntity<TicketMessage> resp = rest.postForEntity(
                baseUrl + "/createTicket",
                request,
                TicketMessage.class
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(PLATE, resp.getBody().getAssociatedWithPlate());
        assertEquals("IN_PROGRESS", resp.getBody().getStatus());

        createdTicketId = resp.getBody().getId();
        assertNotNull(createdTicketId);

        System.out.println("✅ Created ticket ID: " + createdTicketId);
    }

    @Test
    @Order(2)
    void testGetTicketByPlate() {
        ResponseEntity<String> resp = rest.getForEntity(
                baseUrl + "/getTicketByPlate/" + PLATE,
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().contains(PLATE));
        assertTrue(resp.getBody().contains("IN_PROGRESS"));

        System.out.println("✅ Ticket fetched successfully");
    }

    @Test
    @Order(3)
    void testUpdateStatusByPlate() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                    "status": "COMPLETED",
                    "exitDetected": true
                }
                """;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp = rest.postForEntity(
                baseUrl + "/updateStatusByPlate/" + PLATE,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().contains("COMPLETED"));

        System.out.println("✅ Ticket updated to COMPLETED");
    }

    @Test
    @Order(4)
    void testGetTicketAfterUpdate() {
        ResponseEntity<String> resp = rest.getForEntity(
                baseUrl + "/getTicketByPlate/" + PLATE,
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().contains("COMPLETED"));
        assertTrue(resp.getBody().contains("\"exitDetected\":true"));

        System.out.println("✅ Ticket after update verified");
    }
}
