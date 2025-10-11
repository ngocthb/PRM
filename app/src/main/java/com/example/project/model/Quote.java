package com.example.project.model;

import com.google.gson.annotations.SerializedName;

public class Quote {

    @SerializedName("USD")
    private double usd;

    public double getUsd() {
        return usd;
    }
    public void setUsd(double usd) {
        this.usd = usd;
    }

}
