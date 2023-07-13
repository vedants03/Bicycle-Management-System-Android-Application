package com.example.map;

public class Bicycle {
    String id;
    String location;
    boolean isavailable;

    public Bicycle(String id, String location, boolean isavailable) {
        this.id = id;
        this.location = location;
        this.isavailable = isavailable;
    }

    public Bicycle() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isIsavailable() {
        return isavailable;
    }

    public void setIsavailable(boolean isavailable) {
        this.isavailable = isavailable;
    }
}
