package com.smartparking.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeeDataMessage {

    @JsonProperty("change")
    private String change;

    @JsonProperty("hourFee")
    private double hourFee;

    @JsonProperty("dailyFee")
    private double dailyFee;

    @JsonProperty("thresholdHours")
    private int thresholdHours;

    public FeeDataMessage() {}

    public FeeDataMessage(String change, double hourFee, double dailyFee, int thresholdHours) {
        this.change = change;
        this.hourFee = hourFee;
        this.dailyFee = dailyFee;
        this.thresholdHours = thresholdHours;
    }

    public String getChange() {
        return change;
    }

    public double getHourFee() {
        return hourFee;
    }

    public double getDailyFee() {
        return dailyFee;
    }

    public int getThresholdHours() {
        return thresholdHours;
    }

    @Override
    public String toString() {
        return "FeeDataMessage{" +
                "change='" + change + '\'' +
                ", hourFee=" + hourFee +
                ", dailyFee=" + dailyFee +
                ", thresholdHours=" + thresholdHours +
                '}';
    }
}
