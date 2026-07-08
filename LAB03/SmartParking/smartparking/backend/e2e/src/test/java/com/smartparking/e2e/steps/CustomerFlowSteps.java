package com.smartparking.e2e.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.*;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerFlowSteps {

    private final Vertx vertx = Vertx.vertx();
    private final WebClient client = WebClient.create(vertx);
    private String token;
    private HttpResponse<?> lastResponse;
    private final String gatewayUrl = "http://localhost:8080";
    private String licensePlate;
    private String virtualTicketId;
    private double calculatedFee;

    @Before
    public void beforeScenario() {
        try {
            System.out.println("[DEBUG] Reset: remove test car if exists");
            JsonObject req = new JsonObject().put("plate", "TEST");
            postJsonAndWait(gatewayUrl + "/parking-service/removeCar", req, true);
        } catch (Exception e) {
            System.out.println("[DEBUG] Reset: No car to be removed or ignored error: " + e.getMessage());
        }
        obtainToken();
    }

    @Given("The car with license plate {string} arrives at the parking lot")
    public void the_car_with_license_plate_arrives_at_the_parking_lot(String plate) {
        System.out.println("[DEBUG] Step: car arrives, plate=" + plate);
        this.licensePlate = plate;
    }

    @When("The system reads the license plate and calls the addCar endpoint of Parking Management")
    public void the_system_reads_the_license_plate_and_calls_the_add_car_endpoint_of_parking_management() {
        System.out.println("[DEBUG] Step: addCar, plate=" + licensePlate);
        obtainToken();
        JsonObject car = new JsonObject().put("plate", licensePlate);
        lastResponse = postJsonAndWait(gatewayUrl + "/parking-service/addCar", car, true);
        System.out.println("[DEBUG] addCar response: status=" + lastResponse.statusCode() + " body=" + lastResponse.bodyAsString());
        assertEquals(200, lastResponse.statusCode());
    }

    @When("The system creates and saves the virtual ticket associated with the license plate in the database")
    public void the_system_creates_and_saves_the_virtual_ticket_associated_with_the_license_plate_in_the_database() {
        System.out.println("[DEBUG] Step: createVirtualTicket, plate=" + licensePlate);
        JsonObject req = new JsonObject()
                .put("plate", licensePlate)
                .put("associatedWithPlate", licensePlate);
        lastResponse = postJsonAndWait(gatewayUrl + "/ticketing-service/createVirtualTicket", req, true);
        System.out.println("[DEBUG] createVirtualTicket response: status=" + lastResponse.statusCode() + " body=" + lastResponse.bodyAsString());
        assertTrue(lastResponse.statusCode() == 200 || lastResponse.statusCode() == 201);
        JsonObject body = lastResponse.bodyAsJsonObject();
        virtualTicketId = body != null ? body.getString("id") : null;
        System.out.println("[DEBUG] virtualTicketId=" + virtualTicketId);
    }

    @When("The system calls the openEntryBarrier endpoint of Embedded to open the entry gate")
    public void the_system_calls_the_open_entry_barrier_endpoint_of_embedded_to_open_the_entry_gate() {
        System.out.println("[DEBUG] Step: openEntryBarrier");
        lastResponse = postJsonAndWait(gatewayUrl + "/embedded-service/open-entry-barrier", new JsonObject(), true);
        System.out.println("[DEBUG] openEntryBarrier response: status=" + lastResponse.statusCode() + " body=" + lastResponse.bodyAsString());
        assertEquals(200, lastResponse.statusCode());
    }

    @Then("The car enters the parking lot")
    public void the_car_enters_the_parking_lot() {
        System.out.println("[DEBUG] Step: car enters, last status=" + lastResponse.statusCode());
        assertEquals(200, lastResponse.statusCode());
    }

    @Given("The car is inside the parking lot")
    public void the_car_is_inside_the_parking_lot() {
        System.out.println("[DEBUG] Step: car is inside");
    }

    @When("The virtual ticket is active and saved in the database")
    public void the_virtual_ticket_is_active_and_saved_in_the_database() {
        System.out.println("[DEBUG] Step: check virtual ticket, id=" + virtualTicketId);
        assertNotNull(virtualTicketId, "Virtual ticket not present");
    }

    @Then("The customer parks without issues")
    public void the_customer_parks_without_issues() {
        System.out.println("[DEBUG] Step: customer parks, ticketId=" + virtualTicketId);
        assertNotNull(virtualTicketId, "Virtual ticket not present");
    }

    @Given("The customer returns to the cashier and enters the license plate {string}")
    public void the_customer_returns_to_the_cashier_and_enters_the_license_plate(String plate) {
        System.out.println("[DEBUG] Step: customer at cashier, plate=" + plate);
        this.licensePlate = plate;
    }

    @When("The system calls the calculateFee endpoint of Payment Service with the license plate")
    public void the_system_calls_the_calculate_fee_endpoint_of_payment_service_with_the_license_plate() {
        System.out.println("[DEBUG] Step: calculateFee, plate=" + licensePlate);

        HttpResponse<?> ticketResp = getAndWait(gatewayUrl + "/ticketing-service/getTicketByPlate/" + licensePlate, true);
        assertEquals(200, ticketResp.statusCode());
        JsonObject ticket = ticketResp.bodyAsJsonObject();

        lastResponse = postJsonAndWait(gatewayUrl + "/payment-service/calculateFee", ticket, true);
        System.out.println("[DEBUG] calculateFee response: status=" + lastResponse.statusCode() + " body=" + lastResponse.bodyAsString());
        assertEquals(200, lastResponse.statusCode());
        JsonObject body = lastResponse.bodyAsJsonObject();
        calculatedFee = body != null ? body.getDouble("fee", 0.0) : 0.0;
        System.out.println("[DEBUG] calculatedFee=" + calculatedFee);
    }

    @When("Payment Service calls Ticketing Service to get the associated virtual ticket")
    public void payment_service_calls_ticketing_service_to_get_the_associated_virtual_ticket() {
        System.out.println("[DEBUG] Step: getTicketByPlate, plate=" + licensePlate);
        lastResponse = getAndWait(gatewayUrl + "/ticketing-service/getTicketByPlate/" + licensePlate, true);
        System.out.println("[DEBUG] getTicketByPlate response: status=" + lastResponse.statusCode() + " body=" + lastResponse.bodyAsString());
        assertEquals(200, lastResponse.statusCode());
    }

    @When("Payment Service calls getTariff to get the hourly rate")
    public void payment_service_calls_get_tariff_to_get_the_hourly_rate() {
        System.out.println("[DEBUG] Step: getTariff");
        lastResponse = getAndWait(gatewayUrl + "/payment-service/getTariff", true);
        System.out.println("[DEBUG] getTariff response: status=" + lastResponse.statusCode() + " body=" + lastResponse.bodyAsString());
        assertEquals(200, lastResponse.statusCode());
    }

    @Then("The system calculates the total amount to pay and shows it to the customer")
    public void the_system_calculates_the_total_amount_to_pay_and_shows_it_to_the_customer() {
        System.out.println("[DEBUG] Step: show fee, calculatedFee=" + calculatedFee);
        assertEquals(200, lastResponse.statusCode(), "Calulation fee failed");
    }

    @Given("The customer pays the calculated amount")
    public void the_customer_pays_the_calculated_amount() {
        System.out.println("[DEBUG] Step: customer pays, fee=" + calculatedFee);
        assertEquals(200, lastResponse.statusCode(), "Calculation fee failed");
    }

    @When("The system calls the processPayment with nfcTag endpoint of Payment Service")
    public void the_system_calls_the_process_payment_with_nfc_tag_endpoint_of_payment_service() {
        System.out.println("[DEBUG] Step: processPayment, plate=" + licensePlate + " fee=" + calculatedFee);

        HttpResponse<?> ticketResp = getAndWait(gatewayUrl + "/ticketing-service/getTicketByPlate/" + licensePlate, true);
        assertEquals(200, ticketResp.statusCode());
        JsonObject ticket = ticketResp.bodyAsJsonObject();

        ticket.put("fee", calculatedFee);
        ticket.put("nfctag", "FAKE_NFC_TAG");

        lastResponse = postJsonAndWait(gatewayUrl + "/payment-service/processPayment/FAKE_NFC_TAG", ticket, true);
        System.out.println("[DEBUG] processPayment response: status=" + lastResponse.statusCode() + " body=" + lastResponse.bodyAsString());
        assertEquals(200, lastResponse.statusCode());
    }

    @Then("The payment is confirmed and the ticket is updated")
    public void the_payment_is_confirmed_and_the_ticket_is_updated() {
        System.out.println("[DEBUG] Step: payment confirmed, last status=" + lastResponse.statusCode());
        assertEquals(200, lastResponse.statusCode());
    }

    @Given("The customer heads to the exit")
    public void the_customer_heads_to_the_exit() {
        System.out.println("[DEBUG] Step: customer heads to exit");
    }

    @When("Embedded Service reads the license plate {string} at the exit")
    public void embedded_service_reads_the_license_plate_at_the_exit(String plate) {
        System.out.println("[DEBUG] Step: embedded reads plate at exit, plate=" + plate);
        this.licensePlate = plate;
    }

    @When("Embedded Service calls the removeCar endpoint of Parking Management")
    public void embedded_service_calls_the_remove_car_endpoint_of_parking_management() {
        System.out.println("[DEBUG] Step: removeCar, plate=" + licensePlate);
        JsonObject req = new JsonObject().put("plate", licensePlate);
        lastResponse = postJsonAndWait(gatewayUrl + "/parking-service/removeCar", req, true);
        System.out.println("[DEBUG] removeCar response: status=" + lastResponse.statusCode() + " body=" + lastResponse.bodyAsString());
        assertEquals(200, lastResponse.statusCode());
    }

    @When("Embedded Service calls openExitBarrier to open the exit gate")
    public void embedded_service_calls_open_exit_barrier_to_open_the_exit_gate() {
        System.out.println("[DEBUG] Step: openExitBarrier");
        lastResponse = postJsonAndWait(gatewayUrl + "/embedded-service/open-exit-barrier", new JsonObject(), true);
        System.out.println("[DEBUG] openExitBarrier response: status=" + lastResponse.statusCode() + " body=" + lastResponse.bodyAsString());
        assertEquals(200, lastResponse.statusCode());
    }

    @Then("The customer leaves the parking lot without administrator intervention")
    public void the_customer_leaves_the_parking_lot_without_administrator_intervention() {
        System.out.println("[DEBUG] Step: customer leaves, last status=" + lastResponse.statusCode());
        assertEquals(200, lastResponse.statusCode());
    }

    private void obtainToken() {
        try {
            System.out.println("[DEBUG] Richiesta token a /auth/generate");
            HttpResponse<?> resp = postJsonAndWait(gatewayUrl + "/auth/generate?clientId=smartparking-client&clientSecret=Smart-Parking", new JsonObject(), false);
            assertNotNull(resp, "No response from auth endpoint");
            JsonObject tokenBody = resp.bodyAsJsonObject();
            System.out.println("[DEBUG] Risposta /auth/generate: " + tokenBody);
            if (tokenBody == null) {
                fail("Auth response body is null");
            }
            token = tokenBody.getString("token");
            if (token == null) token = tokenBody.getString("accessToken");
            if (token == null) token = tokenBody.getString("access_token");
            if (token == null) token = tokenBody.getString("jwt");
            if (token == null) {
                fail("Token didn't received from /auth/generate. Body: " + tokenBody.encode());
            }
            System.out.println("[DEBUG] Token obtained: " + token);
        } catch (Exception e) {
            System.out.println("[DEBUG] Error in generate the token: " + e.getMessage());
            fail("Error in generate the token: " + e.getMessage());
        }
    }

    private HttpResponse<?> postJsonAndWait(String url, JsonObject json, boolean withAuth) {
        System.out.println("[DEBUG] POST " + url + " body: " + json.encode() + " withAuth: " + withAuth + " token: " + token);
        CompletableFuture<HttpResponse<?>> future = new CompletableFuture<>();
        var req = client.postAbs(url).putHeader("Content-Type", "application/json; charset=UTF-8");
        if (withAuth && token != null) {
            req.putHeader("X-Auth-Token", token);
        }
        req.sendJsonObject(json, ar -> {
            if (ar.succeeded()) {
                System.out.println("[DEBUG] Answer POST " + url + ": status=" + ar.result().statusCode() + " body=" + ar.result().bodyAsString());
                future.complete(ar.result());
            } else {
                System.out.println("[DEBUG] Error POST " + url + ": " + ar.cause());
                future.completeExceptionally(ar.cause());
            }
        });
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception POST " + url + ": " + e.getMessage());
            fail("Request failed to " + url + " : " + e.getMessage());
            return null;
        }
    }

    private HttpResponse<?> getAndWait(String url, boolean withAuth) {
        System.out.println("[DEBUG] GET " + url + " withAuth: " + withAuth + " token: " + token);
        CompletableFuture<HttpResponse<?>> future = new CompletableFuture<>();
        var req = client.getAbs(url);
        if (withAuth && token != null) {
            req.putHeader("X-Auth-Token", token);
        }
        req.send(ar -> {
            if (ar.succeeded()) {
                System.out.println("[DEBUG] Answer GET " + url + ": status=" + ar.result().statusCode() + " body=" + ar.result().bodyAsString());
                future.complete(ar.result());
            } else {
                System.out.println("[DEBUG] Error GET " + url + ": " + ar.cause());
                future.completeExceptionally(ar.cause());
            }
        });
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("[DEBUG] Exception GET " + url + ": " + e.getMessage());
            fail("Request failed to " + url + " : " + e.getMessage());
            return null;
        }
    }

    @io.cucumber.java.After
    public void tearDown() {
        System.out.println("[DEBUG] TearDown: closing Vert.x");
        vertx.close();
    }
}
