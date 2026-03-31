package com.techLabs.nbpdcl.models.zoom;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ZoomToLayer {

    @SerializedName("output")
    @Expose
    private List<Output> output;

    public List<Output> getOutput() {
        return output;
    }

    public void setOutput(List<Output> output) {
        this.output = output;
    }

    public class Output {

        @SerializedName(value = "X", alternate = {"x"})
        @Expose
        private String x;
        @SerializedName(value = "Y", alternate = {"y"})
        @Expose
        private String y;
        @SerializedName(value = "DeviceNumber", alternate = {"devicenumber"})
        @Expose
        private String deviceNumber;
        @SerializedName(value = "Location", alternate = {"location"})
        @Expose
        private String location;
        @SerializedName(value = "DeviceType", alternate = {"devicetype"})
        @Expose
        private String deviceType;
        @SerializedName(value = "NetworkId", alternate = {"networkid"})
        @Expose
        private String networkId;
        @SerializedName(value = "SectionId", alternate = {"sectionid"})
        @Expose
        private String sectionId;

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public String getDeviceNumber() {
            return deviceNumber;
        }

        public void setDeviceNumber(String deviceNumber) {
            this.deviceNumber = deviceNumber;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getNetworkId() {
            return networkId;
        }

        public void setNetworkId(String networkId) {
            this.networkId = networkId;
        }

        public String getSectionId() {
            return sectionId;
        }

        public void setSectionId(String sectionId) {
            this.sectionId = sectionId;
        }

    }

}
