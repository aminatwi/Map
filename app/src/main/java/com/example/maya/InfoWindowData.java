package com.example.maya;

public class InfoWindowData {
    private int Id;
    private Double V;
    private Double V1;
    private String image;
    private String title;
    private String snippet;
    private String hotel;
    private String food;
    private String transport;
    private int audio_number;

    public InfoWindowData() {
    }

    public InfoWindowData(int id, Double V, Double V1, String image, String title, String snippet, String hotel, String food, String transport,int audio_number) {
        this.Id = id;
        this.V = V;
        this.V1 = V1;
        this.image = image;
        this.title = title;
        this.snippet = snippet;
        this.hotel = hotel;
        this.food = food;
        this.transport = transport;
        this.audio_number=audio_number;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Double getV() {
        return V;
    }

    public void setV(Double v) {
        V = v;
    }

    public Double getV1() {
        return V1;
    }

    public void setV1(Double v1) {
        V1 = v1;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public int getAudio_number() {
        return audio_number;
    }

    public void setAudio_number(int audio_number) {
        this.audio_number = audio_number;
    }
}
