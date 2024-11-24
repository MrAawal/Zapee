package com.flinkmart.mahi.map;

import com.google.firebase.Timestamp;

public class LocationModel {

    String uid,latitude,longitude;
    Timestamp createdTimestamp;

    public LocationModel() {
    }

    public LocationModel(String uid, String latitude, String longitude, Timestamp createdTimestamp) {
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdTimestamp = createdTimestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
