package com.example.dacnaviapp.bean;

import java.util.ArrayList;
import java.util.List;

public class Polygon {

    private List<Vertex> vertices;

    public Polygon(){
        vertices = new ArrayList<>();
    }

    public Polygon(List<Vertex> vertices){
        this.vertices = vertices;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }


}
