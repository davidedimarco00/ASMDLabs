package com.smartparking.parkingservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Car {

    @JsonProperty("plate")
    private String plate;

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    @Override
    public String toString() {
        return "Car{" +
                "plate='" + plate + '\'' +
                '}';
    }
}
