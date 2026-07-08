package com.smartparking.parkingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
    @JsonProperty("map")
    private SensorsMap map;

    public Status() {}

    public SensorsMap getMap() {
        return map;
    }

    public void setMap(SensorsMap map) {
        this.map = map;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SensorsMap {
        @JsonProperty("sensors")
        private SensorsList sensors;

        public SensorsList getSensors() {
            return sensors;
        }

        public void setSensors(SensorsList sensors) {
            this.sensors = sensors;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SensorsList {
        @JsonProperty("list")
        private List<DeviceWrapper> list;

        @JsonProperty("empty")
        private boolean empty;

        public List<DeviceWrapper> getList() {
            return list;
        }

        public void setList(List<DeviceWrapper> list) {
            this.list = list;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeviceWrapper {
        @JsonProperty("map")
        private Device map;

        @JsonProperty("empty")
        private boolean empty;

        public Device getMap() {
            return map;
        }

        public void setMap(Device map) {
            this.map = map;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Device {
        @JsonProperty("deviceId")
        private String deviceId;

        @JsonProperty("status")
        private String status;

        public Device() {}

        public Device(String deviceId, String status) {
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
