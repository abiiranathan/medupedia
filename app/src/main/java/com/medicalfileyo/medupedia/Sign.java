package com.medicalfileyo.medupedia;

import java.util.ArrayList;

public class Sign {
    private int id;
    private String name;
    private String description;
    private ArrayList<Disease> differentials;

    public Sign(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public ArrayList<Disease> getDifferentials() {
        return differentials;
    }

    public void setDifferentials(ArrayList<Disease> differentials) {
        this.differentials = differentials;
    }

    public Sign(int id, String name, String description, ArrayList<Disease> differentials) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.differentials = differentials;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Sign{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
