package com.techLabs.nbpdcl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FindDeviceModel {

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

        @SerializedName("DeviceNumber")
        @Expose
        private String deviceNumber;
        @SerializedName("DeviceType")
        @Expose
        private Integer deviceType;
        @SerializedName("NetworkId")
        @Expose
        private String networkId;
        @SerializedName("SectionId")
        @Expose
        private String sectionId;
        @SerializedName("ToNodeId")
        @Expose
        private String toNodeId;
        @SerializedName("Location")
        @Expose
        private String location;

        public String getDeviceNumber() {
            return deviceNumber;
        }

        public void setDeviceNumber(String deviceNumber) {
            this.deviceNumber = deviceNumber;
        }

        public Integer getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(Integer deviceType) {
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

        public String getToNodeId() {
            return toNodeId;
        }

        public void setToNodeId(String toNodeId) {
            this.toNodeId = toNodeId;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

    }

}
