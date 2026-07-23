package com.smartparking.e2e.steps;

import io.cucumber.java.en.*;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;

public class AdminFlowSteps {
    private final Vertx vertx = Vertx.vertx();
    private final WebClient client = WebClient.create(vertx);
    private String token;
    private HttpResponse<?> lastResponse;
    private final String gatewayUrl = "http://localhost:8080";

    @Given("The admin sends a POST request to auth-service with credentials")
    public void adminLogin() {
        client.postAbs(gatewayUrl + "/auth/generate?clientId=smartparking-client&clientSecret=Smart-Parking")
                .send(tokenAr -> {
                    if (tokenAr.succeeded()) {
                        JsonObject tokenBody = tokenAr.result().bodyAsJsonObject();
                        token = tokenBody != null ? tokenBody.getString("token") : null;
                        if (token == null) {
                            fail("Token didn't received from /auth/generate");
                            return;
                        }
                        JsonObject credentials = new JsonObject()
                                .put("username", "ADMIN")
                                .put("password", "ADMIN");
                        client.postAbs(gatewayUrl + "/auth-service/login")
                                .putHeader("X-Auth-Token", token)
                                .sendJsonObject(credentials, ar -> {
                                    if (ar.succeeded()) {
                                        lastResponse = ar.result();
                                        JsonObject body = lastResponse.bodyAsJsonObject();
                                        System.out.println("Login response body: " + body);
                                        assertEquals(200, lastResponse.statusCode(), "Login failed");
                                    } else {
                                        fail("Login failed: " + ar.cause());
                                    }
                                });
                    } else {
                        fail("Error in generating the token: " + tokenAr.cause());
                    }
                });
        await();
    }

    @When("The system register the authentication")
    public void systemRegisterAuthentication() {
        assertNotNull(token, "Token didn't received");
    }

    @Then("The admin can access protected endpoints")
    public void adminCanAccessEndpoints() {
        client.getAbs(gatewayUrl + "/analytics-service/reports")
                .putHeader("X-Auth-Token", token)
                .send(ar -> {
                    if (ar.succeeded()) {
                        lastResponse = ar.result();
                        assertEquals(200, lastResponse.statusCode());
                    } else {
                        fail("Access to protected endpoint failed: " + ar.cause());
                    }
                });
        await();
    }

    @Given("The admin is authenticated")
    public void theAdminIsAuthenticated() {
        adminLogin();
        systemRegisterAuthentication();
    }

    @When("The admin calls POST payment-service with hourFee, dailyFee, and thresholdHours")
    public void adminSetTariff() {
        JsonObject tariff = new JsonObject()
                .put("change", "EUR")
                .put("hourFee", 2)
                .put("dailyFee", 15)
                .put("thresholdHours", 8);

        client.postAbs(gatewayUrl + "/payment-service/setTariff")
                .putHeader("X-Auth-Token", token)
                .sendJsonObject(tariff, ar -> {
                    if (ar.succeeded()) {
                        lastResponse = ar.result();
                    } else {
                        fail("Failed to setting the fee: " + ar.cause());
                    }
                });
        await();
    }

    @Then("The system updates the tariff configuration")
    public void systemUpdatesTariff() {
        assertEquals(200, lastResponse.statusCode());
    }

    @When("The admin calls GET analytics-service to generate reports for the previous day")
    public void adminGeneratesReport() {
        String date = java.time.LocalDate.now().minusDays(1).toString();
        client.getAbs(gatewayUrl + "/analytics-service/report/" + date)
                .putHeader("X-Auth-Token", token)
                .send(ar -> {
                    if (ar.succeeded()) {
                        lastResponse = ar.result();
                    } else {
                        fail("Failed generating report: " + ar.cause());
                    }
                });
        await();
    }

    @Then("The system generates or retrieves the report")
    public void systemGeneratesOrRetrievesReport() {
        assertTrue(lastResponse.statusCode() == 200 || lastResponse.statusCode() == 201);
    }

    @When("The admin calls POST parking-service to addCar with the customer's license plate")
    public void adminAddsCar() {
        JsonObject car = new JsonObject().put("plate", "AB123CD");
        client.postAbs(gatewayUrl + "/parking-service/addCar")
                .putHeader("X-Auth-Token", token)
                .sendJsonObject(car, ar -> {
                    if (ar.succeeded()) {
                        lastResponse = ar.result();
                    } else {
                        fail("Failed add a car: " + ar.cause());
                    }
                });
        await();
    }

    @Then("The system adds the car to the parking lot")
    public void systemAddsCarToParkingLot() {
        assertEquals(200, lastResponse.statusCode());
    }


    @When("The admin calls GET getAllTickets to view active tickets")
    public void adminViewsActiveTickets() {
        client.getAbs(gatewayUrl + "/ticketing-service/getAllTickets")
                .putHeader("X-Auth-Token", token)
                .send(ar -> {
                    if (ar.succeeded()) {
                        lastResponse = ar.result();
                    } else {
                        fail("Failed retrieve active ticket: " + ar.cause());
                    }
                });
        await();
    }

    @When("The admin calls GET getAllHistoryTickets to check payment status")
    public void adminViewsHistoryTickets() {
        client.getAbs(gatewayUrl + "/ticketing-service/getAllHistoryTickets")
                .putHeader("X-Auth-Token", token)
                .send(ar -> {
                    if (ar.succeeded()) {
                        lastResponse = ar.result();
                    } else {
                        fail("Failed retrieve history ticket: " + ar.cause());
                    }
                });
        await();
    }

    @When("If payment is confirmed, the admin calls POST openExitBarrier")
    public void adminOpensExitBarrier() {
        client.postAbs(gatewayUrl + "/parking-service/openExitBarrier")
                .putHeader("X-Auth-Token", token)
                .sendJsonObject(new JsonObject(), ar -> {
                    if (ar.succeeded()) {
                        lastResponse = ar.result();
                    } else {
                        fail("Failed opening exit barrier: " + ar.cause());
                    }
                });
        await();
    }

    @Then("The system opens the exit barrier for the customer")
    public void systemOpensExitBarrier() {
        assertEquals(200, lastResponse.statusCode());
    }

    private void await() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @io.cucumber.java.After
    public void tearDown() {
        vertx.close();
    }
}


