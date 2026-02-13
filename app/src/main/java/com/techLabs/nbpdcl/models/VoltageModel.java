package com.techLabs.nbpdcl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VoltageModel {

    @SerializedName("Voltage")
    @Expose
    private Float voltage;

    public Float getVoltage() {
        return voltage;
    }

    public void setVoltage(Float voltage) {
        this.voltage = voltage;
    }

}