package com.smartparking.e2e.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparking.parkingservice.llm.model.ParkingContext;
import com.smartparking.parkingservice.llm.model.ParkingDecision;
import com.smartparking.parkingservice.llm.service.LlmParkingDecisionService;
import com.smartparking.parkingservice.llm.service.ParkingDecisionService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingLlmSteps {


    private ParkingDecisionService parkingDecisionService;

    private ParkingContext context;
    private ParkingDecision decision;

    @Before("@llm")
    public void initializeScenario() {
        parkingDecisionService = new LlmParkingDecisionService(
                "http://localhost:11434",
                "llama3.2:3b",
                120,
                new ObjectMapper()
        );
        context = new ParkingContext();
        decision = null;
    }


    @Given("the requested parking operation is {string}")
    public void theRequestedParkingOperationIs(String operation) {
        context.setOperation(operation);
    }

    @And("the vehicle plate is {string}")
    public void theVehiclePlateIs(String plate) {
        context.setPlate(plate);
    }

    @And("the vehicle is present")
    public void theVehicleIsPresent() {
        context.setVehiclePresent(true);
    }

    @And("the vehicle is not present")
    public void theVehicleIsNotPresent() {
        context.setVehiclePresent(false);
    }

    @And("the vehicle is already registered inside the parking area")
    public void theVehicleIsAlreadyRegisteredInside() {
        context.setVehicleAlreadyParked(true);
    }

    @And("the vehicle is not registered inside the parking area")
    public void theVehicleIsNotRegisteredInside() {
        context.setVehicleAlreadyParked(false);
    }

    @And("the ticket status is {string}")
    public void theTicketStatusIs(String ticketStatus) {
        context.setTicketStatus(ticketStatus);
    }

    @And("the camera is available")
    public void theCameraIsAvailable() {
        context.setCameraAvailable(true);
    }

    @And("the camera is unavailable")
    public void theCameraIsUnavailable() {
        context.setCameraAvailable(false);
    }

    @And("the induction coil is triggered")
    public void theInductionCoilIsTriggered() {
        context.setCoilTriggered(true);
    }

    @And("the induction coil is not triggered")
    public void theInductionCoilIsNotTriggered() {
        context.setCoilTriggered(false);
    }

    @And("the camera detects plate {string}")
    public void theCameraDetectsPlate(String detectedPlate) {
        context.setDetectedPlate(detectedPlate);
    }

    @And("the NFC device reports plate {string}")
    public void theNfcDeviceReportsPlate(String nfcPlate) {
        context.setNfcAssociatedPlate(nfcPlate);
    }

    @When("the parking decision is requested from the language model")
    public void theParkingDecisionIsRequested() throws Exception {
        decision = parkingDecisionService
                .evaluate(context)
                .get(120, TimeUnit.SECONDS);
    }

    @Then("the classification should be {string}")
    public void theClassificationShouldBe(String expectedClassification) {
        assertNotNull(decision, "The LLM decision must not be null");

        assertEquals(
                expectedClassification,
                decision.getClassification(),
                () -> "Unexpected classification. Complete decision: " + decision
        );
    }

    @Then("the suggested action should be {string}")
    public void theSuggestedActionShouldBe(String expectedAction) {
        assertNotNull(decision, "The LLM decision must not be null");

        assertEquals(
                expectedAction,
                decision.getSuggestedAction(),
                () -> "Unexpected suggested action. Complete decision: " + decision
        );
    }

    @Then("the decision reason should not be empty")
    public void theDecisionReasonShouldNotBeEmpty() {
        assertNotNull(decision);
        assertNotNull(decision.getReason());
        assertFalse(decision.getReason().isBlank());
    }

    @Then("the confidence should be between {double} and {double}")
    public void theConfidenceShouldBeBetween(
            double minimum,
            double maximum
    ) {
        assertNotNull(decision);

        assertTrue(
                decision.getConfidence() >= minimum
                        && decision.getConfidence() <= maximum,
                () -> "Confidence outside the expected range: "
                        + decision.getConfidence()
        );
    }
}