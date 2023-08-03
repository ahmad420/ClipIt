package com.example.clipit.model;

public class Appointment {
    private String id;
    private String title;
    private String date;
    private String time;
    private String location;
    private String userId;
    private String userName;

    public Appointment() {
        // Empty constructor required for Firestore
    }

    public Appointment(String date) {
        this.date = date;
    }

    public Appointment(String userId, String userName, String date, String time) {
        this.userId = userId;
        this.userName = userName;
        this.date = date;
        this.time = time;
    }

    public Appointment(String title, String date, String time, String location, String userId, String userName) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.userId = userId;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
