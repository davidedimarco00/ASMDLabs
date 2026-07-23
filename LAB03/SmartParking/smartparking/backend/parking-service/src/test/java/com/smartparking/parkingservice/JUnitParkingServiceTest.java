package com.smartparking.parkingservice;

import com.mongodb.client.*;
import com.smartparking.parkingservice.model.Car;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JUnitParkingServiceTest {

    private final String baseUrl = "http://localhost:8082";
    private final RestTemplate rest = new RestTemplate();

    private static final String mongoUrl = "mongodb://localhost:27018/parking-service";
    private static final String dbName = "parkingservicedb";
    private static final String collectionName = "parkingservicedbcollection";

    private static final String PLATE = "AB123CD";

    @BeforeAll
    static void resetParkingDatabase() {
        try (MongoClient client = MongoClients.create(mongoUrl)) {
            MongoDatabase db = client.getDatabase(dbName);
            MongoCollection<Document> collection = db.getCollection(collectionName);
            collection.deleteMany(new Document());
            System.out.println("Mongo ParkingService cleaned BEFORE ALL tests.");
        }
    }

    @Test @Order(1)
    void testAddCar() {
        Car car = new Car();
        car.setPlate(PLATE);

        ResponseEntity<String> resp = rest.postForEntity(
                baseUrl + "/addCar",
                car,
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().contains(PLATE));

        System.out.println("🚗 Car added: " + PLATE);
    }

    @Test @Order(3)
    void testGetSlots() {
        ResponseEntity<String> resp = rest.getForEntity(
                baseUrl + "/slots",
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().contains("totalSlots"));
        assertTrue(resp.getBody().contains("availableSlots"));

        System.out.println("📊 Slots info OK");
    }

    @Test @Order(6)
    void testEmbeddedStatus() {
        ResponseEntity<String> resp = rest.getForEntity(
                baseUrl + "/getEmbeddedStatus",
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().contains("devices"));

        System.out.println("📶 Embedded status OK");
    }
}
