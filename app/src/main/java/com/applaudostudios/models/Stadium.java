package com.applaudostudios.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * Created by Tarles on 01/04/2017.
 */

public class Stadium {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("team_name")
    @Expose
    private String teamName;
    @SerializedName("since")
    @Expose
    private String since;
    @SerializedName("coach")
    @Expose
    private String coach;
    @SerializedName("team_nickname")
    @Expose
    private String teamNickname;
    @SerializedName("stadium")
    @Expose
    private String stadium;
    @SerializedName("img_logo")
    @Expose
    private String imgLogo;
    @SerializedName("img_stadium")
    @Expose
    private String imgStadium;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("tickets_url")
    @Expose
    private String ticketsUrl;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("video_url")
    @Expose
    private String videoUrl;
    @SerializedName("schedule_games")
    @Expose
    private List<ScheduleGame> scheduleGames;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public String getTeamNickname() {
        return teamNickname;
    }

    public void setTeamNickname(String teamNickname) {
        this.teamNickname = teamNickname;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public String getImgLogo() {
        return imgLogo;
    }

    public void setImgLogo(String imgLogo) {
        this.imgLogo = imgLogo;
    }

    public String getImgStadium() {
        return imgStadium;
    }

    public void setImgStadium(String imgStadium) {
        this.imgStadium = imgStadium;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTicketsUrl() {
        return ticketsUrl;
    }

    public void setTicketsUrl(String ticketsUrl) {
        this.ticketsUrl = ticketsUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<ScheduleGame> getScheduleGames() {
        return scheduleGames;
    }

    public void setScheduleGames(List<ScheduleGame> scheduleGames) {
        this.scheduleGames = scheduleGames;
    }
}
