package com.smartparking.parkingservice.model;

import java.util.List;

public class DeviceStatusResponse {
    private List<DeviceInfo> devices;

    public DeviceStatusResponse() {
    }

    public DeviceStatusResponse(List<DeviceInfo> devices) {
        this.devices = devices;
    }

    public List<DeviceInfo> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceInfo> devices) {
        this.devices = devices;
    }

    public static class DeviceInfo {
        private String deviceId;
        private String status;

        public DeviceInfo() {
        }

        public DeviceInfo(String deviceId, String status) {
            this.deviceId = deviceId;
            this.status = status;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
