package com.example.maya;

public class Image {
    private  int id;
    private  int id_place;
    private  String pic;

    public  Image(){}

    public Image(int id, int id_place, String pic) {
        this.id = id;
        this.id_place = id_place;
        this.pic = pic;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId_place(int id_place) {
        this.id_place = id_place;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getId() {
        return id;
    }

    public int getId_place() {
        return id_place;
    }

    public String getPic() {
        return pic;
    }
}

