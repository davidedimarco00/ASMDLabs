package com.smartparking.ticketingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketMessage {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("associatedWithPlate")
    private String associatedWithPlate;

    @JsonProperty("exitDetected")
    private boolean exitDetected;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("completedAt")
    private String completedAt;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("fee")
    private String fee;

    public TicketMessage() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAssociatedWithPlate() { return associatedWithPlate; }
    public void setAssociatedWithPlate(String associatedWithPlate) { this.associatedWithPlate = associatedWithPlate; }

    public boolean isExitDetected() { return exitDetected; }
    public void setExitDetected(boolean exitDetected) { this.exitDetected = exitDetected; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "TicketMessage{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", associatedWithPlate='" + associatedWithPlate + '\'' +
                ", exitDetected=" + exitDetected +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", completedAt='" + completedAt + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", fee='" + fee + '\'' +
                '}';
    }
}
