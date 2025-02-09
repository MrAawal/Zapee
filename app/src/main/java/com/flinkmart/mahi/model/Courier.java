package com.flinkmart.mahi.model;

public class Courier {

    String uid,id,longitude,latitude,pickAddress,pickNaame,dropAddress,picContact,dropContact,pickPin,dropPin;


    public Courier(String uid, String id, String longitude, String latitude, String pickAddress, String pickNaame, String dropAddress, String picContact, String dropContact, String pickPin, String dropPin) {
        this.uid = uid;
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.pickAddress = pickAddress;
        this.pickNaame = pickNaame;
        this.dropAddress = dropAddress;
        this.picContact = picContact;
        this.dropContact = dropContact;
        this.pickPin = pickPin;
        this.dropPin = dropPin;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPickAddress() {
        return pickAddress;
    }

    public void setPickAddress(String pickAddress) {
        this.pickAddress = pickAddress;
    }

    public String getPickNaame() {
        return pickNaame;
    }

    public void setPickNaame(String pickNaame) {
        this.pickNaame = pickNaame;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public String getPicContact() {
        return picContact;
    }

    public void setPicContact(String picContact) {
        this.picContact = picContact;
    }

    public String getDropContact() {
        return dropContact;
    }

    public void setDropContact(String dropContact) {
        this.dropContact = dropContact;
    }

    public String getPickPin() {
        return pickPin;
    }

    public void setPickPin(String pickPin) {
        this.pickPin = pickPin;
    }

    public String getDropPin() {
        return dropPin;
    }

    public void setDropPin(String dropPin) {
        this.dropPin = dropPin;
    }
}
