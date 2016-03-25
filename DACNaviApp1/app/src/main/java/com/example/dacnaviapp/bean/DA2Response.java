package com.example.dacnaviapp.bean;

import java.util.HashMap;

public class DA2Response {

    private String Status;
    private String registered;
    private DAObject DAObject;

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public DAObject getDAObject() {
        return DAObject;
    }

    public void setDAObject(DAObject DAObject) {
        this.DAObject = DAObject;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }


    public DA2Response(){

    }
}
