package com.smartparking.analyticsservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketList {

    @JsonProperty("count")
    private int count;


    @JsonProperty("tickets")
    private TicketsWrapper tickets;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public TicketsWrapper getTickets() {
        return tickets;
    }

    public void setTickets(TicketsWrapper tickets) {
        this.tickets = tickets;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TicketsWrapper {

        @JsonProperty("list")
        private List<TicketEntry> list;

        @JsonProperty("empty")
        private boolean empty;

        public List<TicketEntry> getList() {
            return list;
        }

        public void setList(List<TicketEntry> list) {
            this.list = list;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TicketEntry {

        @JsonProperty("map")
        private TicketData map;

        @JsonProperty("empty")
        private boolean empty;

        public TicketData getMap() {
            return map;
        }

        public void setMap(TicketData map) {
            this.map = map;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TicketData {
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

        @JsonProperty("historyAddedAt")
        private String historyAddedAt;

        @JsonProperty("timestamp")
        private String timestamp;

        @JsonProperty("fee")
        private String fee;

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

        public String getHistoryAddedAt() { return historyAddedAt; }
        public void setCompletedAt(String completedAt) { this.historyAddedAt = completedAt; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public String getFee() { return fee; }
        public void setFee(String fee) { this.fee = fee; }
    }
}
