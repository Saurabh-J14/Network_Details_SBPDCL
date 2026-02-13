package com.techLabs.nbpdcl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SelectedFeedersModel {

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

        @SerializedName("Group1")
        @Expose
        private String group1;
        @SerializedName("Group2")
        @Expose
        private String group2;
        @SerializedName("Group3")
        @Expose
        private String group3;
        @SerializedName("Group4")
        @Expose
        private String group4;
        @SerializedName("Group5")
        @Expose
        private String group5;
        @SerializedName("Group6")
        @Expose
        private String group6;
        @SerializedName("MeterDeviceNumber")
        @Expose
        private String meterDeviceNumber;

        public String getGroup1() {
            return group1;
        }

        public void setGroup1(String group1) {
            this.group1 = group1;
        }

        public String getGroup2() {
            return group2;
        }

        public void setGroup2(String group2) {
            this.group2 = group2;
        }

        public String getGroup3() {
            return group3;
        }

        public void setGroup3(String group3) {
            this.group3 = group3;
        }

        public String getGroup4() {
            return group4;
        }

        public void setGroup4(String group4) {
            this.group4 = group4;
        }

        public String getGroup5() {
            return group5;
        }

        public void setGroup5(String group5) {
            this.group5 = group5;
        }

        public String getGroup6() {
            return group6;
        }

        public void setGroup6(String group6) {
            this.group6 = group6;
        }

        public String getMeterDeviceNumber() {
            return meterDeviceNumber;
        }

        public void setMeterDeviceNumber(String meterDeviceNumber) {
            this.meterDeviceNumber = meterDeviceNumber;
        }

    }

}