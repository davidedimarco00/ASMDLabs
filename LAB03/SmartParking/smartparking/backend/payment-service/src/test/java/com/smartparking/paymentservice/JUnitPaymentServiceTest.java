package com.smartparking.paymentservice;

import com.mongodb.client.*;
import com.smartparking.paymentservice.model.FeeDataMessage;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JUnitPaymentServiceTest {

    private final String baseUrl = "http://localhost:8083";
    private final RestTemplate rest = new RestTemplate();

    private static final String mongoUrl = "mongodb://localhost:27019/payment-service";
    private static final String dbName = "paymentservicedb";
    private static final String collectionName = "paymentservicedbcollection";

    private static final String NFC_TAG = "NFC12345";

    @BeforeAll
    static void resetPaymentDatabase() {
        try (MongoClient client = MongoClients.create(mongoUrl)) {
            MongoDatabase db = client.getDatabase(dbName);
            MongoCollection<Document> collection = db.getCollection(collectionName);
            collection.deleteMany(new Document());
            System.out.println("💳 Mongo PaymentService cleaned BEFORE ALL tests.");
        }
    }

    @Test @Order(1)
    void testSetTariff() {

        FeeDataMessage tariff = new FeeDataMessage(
                "EUR",   // currency
                2.50,    // hourFee
                15.00,   // dailyFee
                10    // thresholdHours
        );

        ResponseEntity<String> resp = rest.postForEntity(
                baseUrl + "/setTariff",
                tariff,
                String.class
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());

        System.out.println("💰 Tariff set OK");
    }

    @Test @Order(2)
    void testGetTariff() {

        ResponseEntity<FeeDataMessage> resp = rest.getForEntity(
                baseUrl + "/getTariff",
                FeeDataMessage.class
        );

        assertEquals(HttpStatus.OK, resp.getStatusCode());

        FeeDataMessage tariff = resp.getBody();
        assertNotNull(tariff);

        assertEquals("EUR", tariff.getChange());
        assertEquals(2.50, tariff.getHourFee());
        assertEquals(15.00, tariff.getDailyFee());
        assertEquals(10, tariff.getThresholdHours());

        System.out.println("📄 Tariff retrieved OK");
    }
}
