package com.smartparking.parkingservice.llm.model;

public class ParkingContext {

    private String operation;
    private String plate;
    private boolean vehiclePresent;
    private boolean vehicleAlreadyParked;
    private String ticketStatus;
    private boolean cameraAvailable;
    private boolean coilTriggered;
    private String detectedPlate;
    private String nfcAssociatedPlate;

    public ParkingContext() {
    }

    public ParkingContext(
            String operation,
            String plate,
            boolean vehiclePresent,
            boolean vehicleAlreadyParked,
            String ticketStatus,
            boolean cameraAvailable,
            boolean coilTriggered,
            String detectedPlate,
            String nfcAssociatedPlate
    ) {
        this.operation = operation;
        this.plate = plate;
        this.vehiclePresent = vehiclePresent;
        this.vehicleAlreadyParked = vehicleAlreadyParked;
        this.ticketStatus = ticketStatus;
        this.cameraAvailable = cameraAvailable;
        this.coilTriggered = coilTriggered;
        this.detectedPlate = detectedPlate;
        this.nfcAssociatedPlate = nfcAssociatedPlate;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public boolean isVehiclePresent() {
        return vehiclePresent;
    }

    public void setVehiclePresent(boolean vehiclePresent) {
        this.vehiclePresent = vehiclePresent;
    }

    public boolean isVehicleAlreadyParked() {
        return vehicleAlreadyParked;
    }

    public void setVehicleAlreadyParked(boolean vehicleAlreadyParked) {
        this.vehicleAlreadyParked = vehicleAlreadyParked;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public boolean isCameraAvailable() {
        return cameraAvailable;
    }

    public void setCameraAvailable(boolean cameraAvailable) {
        this.cameraAvailable = cameraAvailable;
    }

    public boolean isCoilTriggered() {
        return coilTriggered;
    }

    public void setCoilTriggered(boolean coilTriggered) {
        this.coilTriggered = coilTriggered;
    }

    public String getDetectedPlate() {
        return detectedPlate;
    }

    public void setDetectedPlate(String detectedPlate) {
        this.detectedPlate = detectedPlate;
    }

    public String getNfcAssociatedPlate() {
        return nfcAssociatedPlate;
    }

    public void setNfcAssociatedPlate(String nfcAssociatedPlate) {
        this.nfcAssociatedPlate = nfcAssociatedPlate;
    }

    @Override
    public String toString() {
        return "ParkingContext{" +
                "operation='" + operation + '\'' +
                ", plate='" + plate + '\'' +
                ", vehiclePresent=" + vehiclePresent +
                ", vehicleAlreadyParked=" + vehicleAlreadyParked +
                ", ticketStatus='" + ticketStatus + '\'' +
                ", cameraAvailable=" + cameraAvailable +
                ", coilTriggered=" + coilTriggered +
                ", detectedPlate='" + detectedPlate + '\'' +
                ", nfcAssociatedPlate='" + nfcAssociatedPlate + '\'' +
                '}';
    }
}