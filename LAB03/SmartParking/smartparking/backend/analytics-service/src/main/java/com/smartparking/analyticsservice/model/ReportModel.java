package com.smartparking.analyticsservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartparking.analyticsservice.ddd.ValueObject;

import java.time.LocalDate;

public class ReportModel implements ValueObject {

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("virtualTicketsGenerated")
    private int virtualTicketsGenerated;

    @JsonProperty("virtualTicketsCompleted")
    private int virtualTicketsCompleted;

    @JsonProperty("totalRevenue")
    private double totalRevenue;

    @JsonProperty("rawData")
    private String rawData;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getVirtualTicketsGenerated() { return virtualTicketsGenerated; }
    public void setVirtualTicketsGenerated(int virtualTicketsGenerated) { this.virtualTicketsGenerated = virtualTicketsGenerated; }

    public int getVirtualTicketsCompleted() { return virtualTicketsCompleted; }
    public void setVirtualTicketsCompleted(int virtualTicketsCompleted) { this.virtualTicketsCompleted = virtualTicketsCompleted; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }
}