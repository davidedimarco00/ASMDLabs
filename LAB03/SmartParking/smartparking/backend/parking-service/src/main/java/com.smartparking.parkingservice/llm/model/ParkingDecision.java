package com.smartparking.parkingservice.llm.model;

public class ParkingDecision {

    private String classification;
    private String suggestedAction;
    private String reason;
    private double confidence;

    public ParkingDecision() {
    }

    public ParkingDecision(
            String classification,
            String suggestedAction,
            String reason,
            double confidence
    ) {
        this.classification = classification;
        this.suggestedAction = suggestedAction;
        this.reason = reason;
        this.confidence = confidence;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getSuggestedAction() {
        return suggestedAction;
    }

    public void setSuggestedAction(String suggestedAction) {
        this.suggestedAction = suggestedAction;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "ParkingDecision{" +
                "classification='" + classification + '\'' +
                ", suggestedAction='" + suggestedAction + '\'' +
                ", reason='" + reason + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}
