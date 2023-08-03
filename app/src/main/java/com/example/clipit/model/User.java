package com.example.clipit.model;

public class User {
    private String email;
    private String name;
    private Boolean IsAdmin;

    // Required public no-argument constructor
    public User() {
        IsAdmin=false;
    }

    public User(String email, String name) {
        IsAdmin=false;
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIsAdmin(){
        return IsAdmin;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
