package com.flinkmart.mahi.model;

public class Branch {
    public String storename,storeLat,storeLon;

    public boolean is_selected;
    public String uid;

    public String pincode;

    public String delivery;


    public Branch() {
    }

    public Branch(String delivery) {
        this.delivery = delivery;
    }

    public Branch(String storename, String storeLat, String storeLon, boolean is_selected, String uid, String pincode, String delivery) {
        this.storename = storename;
        this.storeLat = storeLat;
        this.storeLon = storeLon;
        this.is_selected = is_selected;
        this.uid = uid;
        this.pincode = pincode;
        this.delivery = delivery;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getStoreLat() {
        return storeLat;
    }

    public void setStoreLat(String storeLat) {
        this.storeLat = storeLat;
    }

    public String getStoreLon() {
        return storeLon;
    }

    public void setStoreLon(String storeLon) {
        this.storeLon = storeLon;
    }

    public boolean isIs_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
