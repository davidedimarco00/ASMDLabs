package com.smartparking.parkingservice.model;

public class Parking {

    private int availableSlots;
    private final int totalSlots;

    public Parking(int slots) {
        this.totalSlots = slots;
        this.availableSlots = slots;
    }

    public int getSlots() {
        return this.availableSlots;
    }

    public void setSlots(int slots) {
        this.availableSlots = slots;
    }

    public int getTotalSlots() {
        return this.totalSlots;
    }
}
