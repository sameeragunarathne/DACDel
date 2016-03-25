package com.example.dacnaviapp.bean;

import java.util.HashMap;

public class DAObject {
    private String digitalAddressTag2;
    private String digitalAddressTag3;
    private Location location;
    private String type;
    private int reliability;
    private HashMap attributes;
    private String digitalAddress;
    private String digitalAddresTag1;

    public String getDigitalAddresTag1() {
        return digitalAddresTag1;
    }

    public void setDigitalAddresTag1(String digitalAddresTag1) {
        this.digitalAddresTag1 = digitalAddresTag1;
    }

    public String getDigitalAddress() {
        return digitalAddress;
    }

    public void setDigitalAddress(String digitalAddress) {
        this.digitalAddress = digitalAddress;
    }

    public HashMap getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap attributes) {
        this.attributes = attributes;
    }

    public int getReliability() {
        return reliability;
    }

    public void setReliability(int reliability) {
        this.reliability = reliability;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDigitalAddressTag3() {
        return digitalAddressTag3;
    }

    public void setDigitalAddressTag3(String digitalAddressTag3) {
        this.digitalAddressTag3 = digitalAddressTag3;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDigitalAddressTag2() {
        return digitalAddressTag2;
    }

    public void setDigitalAddressTag2(String digitalAddressTag2) {
        this.digitalAddressTag2 = digitalAddressTag2;
    }


    public DAObject(){

    }
}
