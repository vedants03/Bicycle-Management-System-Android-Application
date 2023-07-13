package com.example.map;

public class User {
    String username;
    String password;
    String contact;
    boolean isTaken;

    public User(String name, String password, String contact) {
        this.username = name;
        this.password = password;
        this.contact = contact;
        isTaken = false;
    }

    public User() {
    }

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}
