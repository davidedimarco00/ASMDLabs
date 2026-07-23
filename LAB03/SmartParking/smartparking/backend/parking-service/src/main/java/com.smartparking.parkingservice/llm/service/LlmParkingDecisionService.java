package com.smartparking.parkingservice.llm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparking.parkingservice.llm.model.ParkingContext;
import com.smartparking.parkingservice.llm.model.ParkingDecision;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;


@Service
public class LlmParkingDecisionService implements ParkingDecisionService {

    private static final Logger logger = LoggerFactory.getLogger(LlmParkingDecisionService.class);

    private final OllamaChatModel chatModel;
    private final ObjectMapper objectMapper;

    public LlmParkingDecisionService(
            @Value("${llm.ollama.base-url}") String baseUrl,
            @Value("${llm.ollama.model-name}") String modelName,
            @Value("${llm.ollama.timeout-seconds:60}") long timeoutSeconds,
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;

        this.chatModel = OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.0)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();

        logger.info("LLM service initialized: model={}, baseUrl={}", modelName, baseUrl);
    }

    @Override
    public CompletableFuture<ParkingDecision> evaluate(ParkingContext context) {
        return CompletableFuture.supplyAsync(() -> {
            validateContext(context);
            String prompt = buildPrompt(context);
            logger.info("Sending context to Ollama: {}", context);
            String response = chatModel.chat(prompt);
            logger.info("Ollama raw response: {}", response);
            return parseDecision(response);
        });
    }

    private ParkingDecision parseDecision(String response) {
        if (response == null || response.isBlank()) {
            throw new IllegalStateException(
                    "Ollama returned an empty response"
            );
        }

        String cleanedResponse = extractJson(response);

        try {
            ParkingDecision decision = objectMapper.readValue(
                    cleanedResponse,
                    ParkingDecision.class
            );

            validateDecision(decision);

            return decision;

        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Invalid JSON returned by Ollama: " + cleanedResponse,
                    exception
            );
        }
    }

    private String extractJson(String response) {
        String cleaned = response.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(
                    0,
                    cleaned.length() - 3
            );
        }

        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');

        if (firstBrace < 0 || lastBrace <= firstBrace) {
            throw new IllegalStateException(
                    "No valid JSON object found in Ollama response: "
                            + response
            );
        }

        return cleaned.substring(
                firstBrace,
                lastBrace + 1
        ).trim();
    }

    private void validateContext(ParkingContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Parking context is required");
        }

        if (context.getOperation() == null || context.getOperation().isBlank()) {
            throw new IllegalArgumentException("Operation is required");
        }

        String operation = context.getOperation().trim().toUpperCase();
        if (!operation.equals("ENTRY")
                && !operation.equals("EXIT")) {
            throw new IllegalArgumentException(
                    "Operation must be ENTRY or EXIT"
            );
        }
        if (context.getPlate() == null || context.getPlate().isBlank()) {
            throw new IllegalArgumentException(
                    "Plate is required"
            );
        }
    }

    private void validateDecision(ParkingDecision decision) {
        if (decision == null) {
            throw new IllegalStateException(
                    "Ollama returned a null decision"
            );
        }

        if (decision.getClassification() == null
                || decision.getClassification().isBlank()) {
            throw new IllegalStateException(
                    "Missing classification"
            );
        }

        if (decision.getSuggestedAction() == null
                || decision.getSuggestedAction().isBlank()) {
            throw new IllegalStateException(
                    "Missing suggestedAction"
            );
        }

        if (decision.getReason() == null
                || decision.getReason().isBlank()) {
            throw new IllegalStateException(
                    "Missing reason"
            );
        }

        if (decision.getConfidence() < 0.0
                || decision.getConfidence() > 1.0) {
            throw new IllegalStateException(
                    "Confidence must be between 0 and 1"
            );
        }
    }

    private String buildPrompt(ParkingContext context) {
        String contextJson;

        try {
            contextJson = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(context);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Unable to serialize parking context",
                    exception
            );
        }

        return """
            You are a deterministic decision engine for a smart parking system.

            Your task is to classify exactly one parking situation.

            Read every field from the INPUT JSON exactly as written.

            Mandatory constraints:

            - Never invert a boolean value.
            - Never invent a field value.
            - Never ignore an exceptional condition.
            - Never return NORMAL_ENTRY or NORMAL_EXIT when a blocking
              condition is present.
            - Apply the rules below in the exact order shown.
            - Stop at the first matching rule.
            - Always provide a non-empty reason.
            - Return only one valid JSON object.

            FIELD MEANING:

            operation:
            - ENTRY means the vehicle is trying to enter.
            - EXIT means the vehicle is trying to leave.

            vehicleAlreadyParked:
            - true means the vehicle is registered as currently inside.
            - false means the vehicle is not registered as currently inside.

            ticketStatus:
            - PAID means payment is complete.
            - UNPAID means payment is required.
            - PENDING means payment is not complete.
            - UNKNOWN is acceptable for ENTRY and must not block a normal entry.

            cameraAvailable:
            - true means the camera works.
            - false means the camera is unavailable.

            coilTriggered:
            - true means the physical vehicle-presence sensor has been triggered.


            RULE 1 — CAMERA FAILURE

            If:
            - cameraAvailable is false
            - and coilTriggered is true

            return:

            {
              "classification": "DEVICE_FAILURE",
              "suggestedAction": "REQUEST_MANUAL_REVIEW",
              "reason": "The vehicle sensor is triggered but the camera is unavailable.",
              "confidence": 1.0
            }

            RULE 2 — PLATE AND NFC MISMATCH

            If:
            - detectedPlate is different from nfcAssociatedPlate

            return:

            {
              "classification": "PLATE_NFC_MISMATCH",
              "suggestedAction": "REQUEST_MANUAL_REVIEW",
              "reason": "The plate detected by the camera does not match the NFC-associated plate.",
              "confidence": 1.0
            }
            
            
             RULE 3 — EXIT PAYMENT REQUIRED

            If:
            - operation is EXIT
            - and ticketStatus is UNPAID or PENDING

            return:

            {
              "classification": "PAYMENT_REQUIRED",
              "suggestedAction": "REQUEST_PAYMENT",
              "reason": "The ticket payment is not complete.",
              "confidence": 1.0
            }

            RULE 4 — EXIT FOR VEHICLE NOT REGISTERED INSIDE

            If:
            - operation is EXIT
            - and vehicleAlreadyParked is false

            return:

            {
              "classification": "VEHICLE_NOT_FOUND",
              "suggestedAction": "DENY_EXIT",
              "reason": "The vehicle is not registered as currently inside the parking area.",
              "confidence": 1.0
            }

           

            RULE 5 — DUPLICATE ENTRY

            If:
            - operation is ENTRY
            - and vehicleAlreadyParked is true

            return:

            {
              "classification": "VEHICLE_ALREADY_PRESENT",
              "suggestedAction": "DENY_ENTRY",
              "reason": "The vehicle is already registered inside the parking area.",
              "confidence": 1.0
            }

            RULE 6 — NORMAL EXIT

            If all these conditions are true:
            - operation is EXIT
            - vehiclePresent is true
            - vehicleAlreadyParked is true
            - ticketStatus is PAID
            - cameraAvailable is true
            - coilTriggered is true
            - detectedPlate equals plate
            - nfcAssociatedPlate equals plate

            return:

            {
              "classification": "NORMAL_EXIT",
              "suggestedAction": "ALLOW_EXIT",
              "reason": "The vehicle is registered inside, the ticket is paid, and all identity checks match.",
              "confidence": 1.0
            }

            RULE 7 — NORMAL ENTRY

            If all these conditions are true:
            - operation is ENTRY
            - vehiclePresent is true
            - vehicleAlreadyParked is false
            - cameraAvailable is true
            - coilTriggered is true
            - detectedPlate equals plate
            - nfcAssociatedPlate equals plate
            - ticketStatus may be UNKNOWN

            return:

            {
              "classification": "NORMAL_ENTRY",
              "suggestedAction": "ALLOW_ENTRY",
              "reason": "The vehicle is not already inside and all entry checks are valid.",
              "confidence": 1.0
            }

            FALLBACK RULE

            If none of the previous rules matches, return:

            {
              "classification": "MANUAL_ASSISTANCE_REQUIRED",
              "suggestedAction": "REQUEST_MANUAL_REVIEW",
              "reason": "The parking context is incomplete or inconsistent.",
              "confidence": 0.7
            }

            INPUT JSON:

            %s

            FINAL VERIFICATION BEFORE ANSWERING:

            1. Compare cameraAvailable with the exact JSON value.
            2. Compare vehicleAlreadyParked with the exact JSON value.
            3. Compare ticketStatus with the exact JSON value.
            4. Compare detectedPlate and nfcAssociatedPlate character by character.
            5. Apply only the first matching rule.
            6. Make sure reason is not empty.

            Return only the JSON object selected by the first matching rule.

            Do not use Markdown.
            Do not use code fences.
            Do not write explanations before or after the JSON.
            """.formatted(contextJson);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "UNKNOWN";
        }

        return value.trim().toUpperCase();
    }
}