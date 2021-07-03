package com.example.maya;

public class Hotel {
    private int id;
    private double v;
    private double v1;
    private String name;
    private String description;

    public Hotel(){}

    public Hotel(int id, double v, double v1, String name, String description) {
        this.id = id;
        this.v = v;
        this.v1 = v1;
        this.name = name;
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getV() {
        return v;
    }

    public double getV1() {
        return v1;
    }

    public void setV(double v) {
        this.v = v;
    }

    public void setV1(double v1) {
        this.v1 = v1;
    }
}

