package com.medicalfileyo.medupedia.diseases;

import com.medicalfileyo.medupedia.presentation.Feature;
import java.util.ArrayList;

public class Disease {
    private int id;
    private String name;
    private String about;
    private ArrayList<Feature> symptoms;
    private ArrayList<Feature> signs;

    public Disease(int id, String name, String about, ArrayList<Feature> symptoms, ArrayList<Feature> signs) {
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

    public ArrayList<Feature> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(ArrayList<Feature> symptoms) {
        this.symptoms = symptoms;
    }

    public ArrayList<Feature> getSigns() {
        return signs;
    }

    public void setSigns(ArrayList<Feature> signs) {
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
