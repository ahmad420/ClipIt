package com.example.clipit.model;

public class Appointment {
    private String id;
    private String title;
    private String date;
    private String time;
    private String location;
    private String userId;
    private String userName;
    private String status; // Added status property

    public Appointment() {
        // Empty constructor required for Firestore
        // Set default values here
        title = "Haircut and Trim";
        location = "Haifa st35"; // Set default location
        status = "Pending";
    }

    public Appointment(String date) {
        this.date = date;
        // Set default values here
        title = "Haircut and Trim";
        location = "Haifa st35"; // Set default location
        status = "Pending";
    }

    public Appointment(String userId, String userName, String date, String time) {
        this.userId = userId;
        this.userName = userName;
        this.date = date;
        this.time = time;
        // Set default values here
        title = "Haircut and Trim";
        location = "Haifa st35"; // Set default location
        status = "Pending";
    }

    public Appointment(String title, String date, String time, String location, String userId, String userName) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.userId = userId;
        this.userName = userName;
        // Set default values here
        status = "Pending";
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
