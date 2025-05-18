package com.example.appointmentapp.model;

import java.util.List;

public class Hospital {
    private String name, nextApp;
    List<String> doctors;

    public Hospital() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNextApp() {
        return nextApp;
    }

    public void setNextApp(String nextApp) {
        this.nextApp = nextApp;
    }

    public List<String> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<String> doctors) {
        this.doctors = doctors;
    }
}
