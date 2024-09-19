package com.flinkmart.mahi.model;

import android.widget.EditText;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone ;
    public  String username;
    public  String address;
    public Timestamp createdTimestamp;

    public UserModel() {
    }

    public UserModel(String phone, String username, String address, Timestamp createdTimestamp) {
        this.phone = phone;
        this.username = username;
        this.address = address;
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

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
