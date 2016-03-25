package com.example.dacnaviapp.bean;

/**
 * Created by sameera on 2/11/16.
 */
public class Vertex {

    private double longitude;
    private double latitude;

    public Vertex(){

    }

    public Vertex(double latitude,double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }



}
