package com.medicalfileyo.medupedia;

import java.util.ArrayList;

public class Disease {
    private int id;
    private String name;
    private String about;
    private ArrayList<Symptom> symptoms;
    private ArrayList<Sign> signs;

    public Disease(int id, String name, String about, ArrayList<Symptom> symptoms, ArrayList<Sign> signs) {
        this.id = id;
        this.name = name;
        this.about = about;
        this.symptoms = symptoms;
        this.signs = signs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public ArrayList<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(ArrayList<Symptom> symptoms) {
        this.symptoms = symptoms;
    }

    public ArrayList<Sign> getSigns() {
        return signs;
    }

    public void setSigns(ArrayList<Sign> signs) {
        this.signs = signs;
    }

    @Override
    public String toString() {
        return "Disease{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", about='" + about + '\'' +
                '}';
    }
}
