package com.applaudostudios.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Tarles on 01/04/2017.
 */

public class ScheduleGame {
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("stadium")
    @Expose
    private String stadium;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }
}
