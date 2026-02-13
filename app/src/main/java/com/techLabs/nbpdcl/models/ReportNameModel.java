package com.techLabs.nbpdcl.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportNameModel {

    @SerializedName("output")
    @Expose
    private List<String> output;

    public List<String> getOutput() {
        return output;
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

}