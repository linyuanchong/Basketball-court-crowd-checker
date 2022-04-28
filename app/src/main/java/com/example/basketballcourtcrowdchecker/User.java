package com.example.basketballcourtcrowdchecker;

public class User {

    private String email;
    private String password;
    private String name;
    private String phone;
    private String currentCourt;
    private boolean presence;

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCurrentCourt() {
        return currentCourt;
    }

    public void setCurrentCourt(String currentCourt) {
        this.currentCourt = currentCourt;
    }

    public boolean getPresence() {
        return presence;
    }

    public void setPresence(boolean presence) {
        this.presence = presence;
    }
}
