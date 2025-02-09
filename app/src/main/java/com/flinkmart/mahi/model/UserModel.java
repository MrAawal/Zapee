package com.flinkmart.mahi.model;

import android.widget.EditText;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone ;
    public  String username;
    public  String address;
    public  String pin;
    public  String mapaddress;
    public  String mappin;
    public  String sublocality;
    public String feature;

    public  String store;

    public String lat;
    public  String lon;
    public Timestamp createdTimestamp;
    public UserModel() {
    }
    public UserModel(String store) {
        this.store = store;
    }

    public UserModel(String phone, String username, String address, String pin, String mapaddress, String mappin, String sublocality, String feature, String lat, String lon, Timestamp createdTimestamp) {
        this.phone = phone;
        this.username = username;
        this.address = address;
        this.pin = pin;
        this.mapaddress = mapaddress;
        this.mappin = mappin;
        this.sublocality = sublocality;
        this.feature = feature;
        this.lat = lat;
        this.lon = lon;
        this.createdTimestamp = createdTimestamp;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setMapaddress(String mapaddress) {
        this.mapaddress = mapaddress;
    }

    public void setMappin(String mappin) {
        this.mappin = mappin;
    }

    public void setSublocality(String sublocality) {
        this.sublocality = sublocality;
    }
    public void setFeature(String feature) {
        this.feature = feature;
    }


    public String getStore() {
        return store;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
