package com.techLabs.nbpdcl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteFeature {

    @SerializedName("Message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}