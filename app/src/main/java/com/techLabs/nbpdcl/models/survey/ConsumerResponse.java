package com.techLabs.nbpdcl.models.survey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ConsumerResponse {

    @SerializedName("output")
    @Expose
    private Output output;

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public static class Output {

        @SerializedName("id")
        @Expose
        private List<Integer> id;

        @SerializedName("group1")
        @Expose
        private List<String> group1;

        @SerializedName("group2")
        @Expose
        private List<String> group2;

        @SerializedName("group3")
        @Expose
        private List<String> group3;

        @SerializedName("group4")
        @Expose
        private List<String> group4;

        @SerializedName("group5")
        @Expose
        private List<String> group5;

        @SerializedName("group6")
        @Expose
        private List<String> group6;

        @SerializedName("networkid")
        @Expose
        private List<String> networkid;


        public List<Integer> getId() {
            return id;
        }

        public void setId(List<Integer> id) {
            this.id = id;
        }

        public List<String> getGroup1() {
            return group1;
        }

        public void setGroup1(List<String> group1) {
            this.group1 = group1;
        }

        public List<String> getGroup2() {
            return group2;
        }

        public void setGroup2(List<String> group2) {
            this.group2 = group2;
        }

        public List<String> getGroup3() {
            return group3;
        }

        public void setGroup3(List<String> group3) {
            this.group3 = group3;
        }

        public List<String> getGroup4() {
            return group4;
        }

        public void setGroup4(List<String> group4) {
            this.group4 = group4;
        }

        public List<String> getGroup5() {
            return group5;
        }

        public void setGroup5(List<String> group5) {
            this.group5 = group5;
        }

        public List<String> getGroup6() {
            return group6;
        }

        public void setGroup6(List<String> group6) {
            this.group6 = group6;
        }

        public List<String> getNetworkid() {
            return networkid;
        }

        public void setNetworkid(List<String> networkid) {
            this.networkid = networkid;
        }
    }
}

