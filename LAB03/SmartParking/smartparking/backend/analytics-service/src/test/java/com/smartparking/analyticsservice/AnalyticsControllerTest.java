package com.smartparking.analyticsservice;

import com.mongodb.client.*;
import com.smartparking.analyticsservice.model.ReportModel;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JUnitAnalyticsServiceTest {

    private final String baseUrl = "http://localhost:8085";
    private final RestTemplate rest = new RestTemplate();

    private static final String mongoUrl = "mongodb://localhost:27021";
    private static final String databaseName = "analyticsservicedb";
    private static final String reportsCollection = "reports";

    private static final LocalDate TEST_DATE = LocalDate.of(2025, 1, 1);

    @BeforeAll
    static void cleanMongo() {
        try (MongoClient client = MongoClients.create(mongoUrl)) {
            MongoDatabase db = client.getDatabase(databaseName);

            db.getCollection(reportsCollection).deleteMany(new Document());

            System.out.println("🔥 AnalyticsService Mongo cleaned (porta 27021)");
        }
    }

    @Test
    @Order(1)
    void testGenerateReport() {
        String url = baseUrl + "/report/" + TEST_DATE;

        ResponseEntity<ReportModel> resp =
                rest.getForEntity(url, ReportModel.class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(TEST_DATE, resp.getBody().getDate());

        System.out.println("✅ Report generato correttamente");
    }

    @Test
    @Order(2)
    void testGetReportByDate_exists() {
        String url = baseUrl + "/report/get/" + TEST_DATE;

        ResponseEntity<ReportModel> resp =
                rest.getForEntity(url, ReportModel.class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(TEST_DATE, resp.getBody().getDate());

        System.out.println("✅ Report recuperato correttamente");
    }

    @Test
    @Order(3)
    void testGetReportByDate_notFound() {
        String url = baseUrl + "/report/get/2025-01-02";

        try {
            rest.getForEntity(url, ReportModel.class);
            fail("Expected 404 NOT FOUND");
        } catch (HttpClientErrorException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
            System.out.println("✅ Report non trovato restituisce 404 correttamente");
        }
    }

    @Test
    @Order(4)
    void testGetAllReports() {
        String url = baseUrl + "/reports";

        ResponseEntity<ReportModel[]> resp =
                rest.getForEntity(url, ReportModel[].class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().length >= 1);

        System.out.println("✅ Lista report recuperata correttamente");
    }
}
