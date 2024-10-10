package com.flinkmart.mahi.model;

import com.google.firebase.Timestamp;

public class UserModel1 {
    private String phone ;
    public  String username;
    public  String address;
    public  String pin;
    public Timestamp createdTimestamp;

    public UserModel1() {
    }


    public UserModel1(String phone, String username, String address, String pin, Timestamp createdTimestamp) {
        this.phone = phone;
        this.username = username;
        this.address = address;
        this.pin = pin;
        this.createdTimestamp = createdTimestamp;
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
