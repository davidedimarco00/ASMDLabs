package com.smartparking.parkingservice.application;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparking.parkingservice.application.ports.ParkingServiceAPI;
import com.smartparking.parkingservice.application.ports.ParkingServiceRepository;

import com.smartparking.parkingservice.infrastracture.adapters.EmbeddedServiceAdapter;
import com.smartparking.parkingservice.infrastracture.adapters.TicketingServiceAdapter;
import com.smartparking.parkingservice.model.DeviceStatusResponse;
import com.smartparking.parkingservice.model.Parking;
import com.smartparking.parkingservice.model.Status;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ParkingServiceApiImpl implements ParkingServiceAPI {

    private static final Logger logger = LoggerFactory.getLogger(ParkingServiceApiImpl.class);
    private final ParkingServiceRepository repository;
    private final Parking parking = new Parking(100);
    Vertx vertx = Vertx.vertx();
    WebClient webClient = WebClient.create(vertx);
    TicketingServiceAdapter ticketingServiceAdapter = new TicketingServiceAdapter(webClient);
    EmbeddedServiceAdapter embeddedServiceAdapter = new EmbeddedServiceAdapter(webClient);

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public ParkingServiceApiImpl(ParkingServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public CompletableFuture<JsonObject> addCar(JsonObject carData) {
        String plate = carData.getString("plate");
        logger.info("🚗 [Service] Request to add car {}", plate);

        return repository.findByPlate(plate)
                .thenCompose(existingCar -> {
                    if (existingCar != null) {
                        logger.warn("⚠️ [Service] Car with plate {} already exists", plate);
                        return CompletableFuture.completedFuture(
                                new JsonObject()
                                        .put("status", "already exists")
                                        .put("message", "Car with plate " + plate + " is already parked"));
                    }
                    String entryTime = FORMATTER.format(Instant.now());
                    JsonObject newCar = new JsonObject()
                            .put("plate", plate)
                            .put("status", "PARKED")
                            .put("entryTimestamp", entryTime);

                    parking.setSlots(parking.getSlots() - 1);
                    this.ticketingServiceAdapter.createVirtualTicket(plate);
                    logger.info("✅ [Service] Car {} added successfully", plate);
                    return repository.save(newCar)
                            .thenApply(v -> new JsonObject()
                                    .put("status", "car added")
                                    .put("plate", plate)
                                    .put("availableSlots", parking.getSlots()));
                })
                .exceptionally(ex -> {
                    logger.error("❌ Error adding car {}: {}", plate, ex.getMessage());
                    return new JsonObject().put("error", ex.getMessage());
                });
    }

    @Override
    public CompletableFuture<JsonObject> removeCar(JsonObject carData) {
        String plate = carData.getString("plate");

        return repository.findByPlate(plate)
                .thenCompose(foundCar -> {
                    if (foundCar == null) {
                        logger.warn("⚠️ [Service] Car with plate {} not found in DB", plate);
                        return CompletableFuture.completedFuture(
                                new JsonObject()
                                        .put("status", "not found")
                                        .put("message", "Car with plate " + plate + " does not exist"));
                    }

                    return ticketingServiceAdapter.verifyPaymentByPlate(plate)
                            .thenCompose(ticket -> {
                                String status = ticket.getString("status");

                                if (!"PAID".equals(status)) {
                                    logger.warn("❌ Ticket for plate {} is not paid Status = {}", plate, status);
                                    return CompletableFuture.completedFuture(
                                            new JsonObject()
                                                    .put("status", "payment required")
                                                    .put("message", "Ticket not paid for plate " + plate)
                                                    .put("ticket", ticket)
                                    );
                                }

                                logger.info("✅ Ticket for plate {} is PAID. Proceeding with car removal", plate);
                                String exitTime = FORMATTER.format(Instant.now());

                                parking.setSlots(parking.getSlots() + 1);

                                return repository.setExitedByPlate(plate, exitTime)
                                        .thenApply(v -> new JsonObject()
                                                .put("status", "car removed")
                                                .put("availableSlots", parking.getSlots())
                                                .put("exitTimestamp", exitTime));
                            });
                })
                .exceptionally(ex -> {
                    logger.error("❌ Error removing car {}: {}", plate, ex.getMessage());
                    return new JsonObject().put("error", ex.getMessage());
                });
    }

    @Override
    public CompletableFuture<JsonObject> getCar(String id) {
        return repository.findById(id)
                .thenApply(car -> {
                    if (car == null) {
                        logger.warn("⚠️ Car with id {} not found", id);
                        return new JsonObject().put("status", "not found");
                    }
                    return new JsonObject().put("car", car);
                })
                .exceptionally(ex -> {
                    logger.error("❌ Error retrieving car {}: {}", id, ex.getMessage());
                    return new JsonObject().put("error", ex.getMessage());
                });
    }

    @Override
    public CompletableFuture<JsonObject> getParkingSlots() {
        return CompletableFuture.supplyAsync(() -> {
            int total = parking.getTotalSlots();
            int available = parking.getSlots();
            int occupied = total - available;

            return new JsonObject()
                    .put("totalSlots", total)
                    .put("occupiedSlots", occupied)
                    .put("availableSlots", available);
        });
    }

    @Override
    public CompletableFuture<JsonObject> getEmbeddedStatus() {
        logger.info("📶 [Service] Getting embedded device status");

        return embeddedServiceAdapter.getEmbeddedStatus()
                .thenApply(response -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper()
                                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        String json = response.encode();
                        Status status = mapper.readValue(json, Status.class);

                        List<DeviceStatusResponse.DeviceInfo> devices = Collections.emptyList();
                        if (status != null &&
                                status.getMap() != null &&
                                status.getMap().getSensors() != null &&
                                status.getMap().getSensors().getList() != null) {

                            devices = status.getMap()
                                    .getSensors()
                                    .getList()
                                    .stream()
                                    .map(wrapper -> {
                                        Status.Device d = wrapper.getMap();
                                        return d == null ? null
                                                : new DeviceStatusResponse.DeviceInfo(d.getDeviceId(), d.getStatus());
                                    })
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());
                        }

                        DeviceStatusResponse simplifiedResponse = new DeviceStatusResponse(devices);
                        return new JsonObject(mapper.writeValueAsString(simplifiedResponse));

                    } catch (Exception e) {
                        logger.error("❌ Error parsing embedded status: {}", e.getMessage());
                        return new JsonObject().put("error", "Failed to parse embedded status");
                    }
                })
                .exceptionally(ex -> {
                    logger.error("❌ Error getting embedded status: {}", ex.getMessage());
                    return new JsonObject().put("error", ex.getMessage());
                });
    }

    @Override
    public CompletionStage<JsonObject> openEntryBarrier(JsonObject payload) {
        logger.info("🔓 [Service] Opening entry barrier");
        return embeddedServiceAdapter.openEntryBarrier(payload);
    }

    @Override
    public CompletionStage<JsonObject> openExitBarrier(JsonObject payload) {
        logger.info("🔓 [Service] Opening exit barrier");
        return embeddedServiceAdapter.openExitBarrier(payload);
    }

    @Override
    public CompletableFuture<JsonObject> getAllHistory() {
        return repository.getAllHistory();
    }
}
