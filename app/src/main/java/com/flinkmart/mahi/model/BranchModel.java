package com.flinkmart.mahi.model;

public class BranchModel{
    public String storename,storeuid,storeLat,storeLon,delivery,pincode,useruid,storecate,minAmount,policy,radius,announcement,kmcharge,packing;

    public boolean is_selected;
    public BranchModel() {
    }

    public BranchModel(String storename, String storeuid, String storeLat, String storeLon, String delivery, String pincode, String useruid, String storecate, String minAmount, String policy, String radius, String announcement, String kmcharge, String packing, boolean is_selected) {
        this.storename = storename;
        this.storeuid = storeuid;
        this.storeLat = storeLat;
        this.storeLon = storeLon;
        this.delivery = delivery;
        this.pincode = pincode;
        this.useruid = useruid;
        this.storecate = storecate;
        this.minAmount = minAmount;
        this.policy = policy;
        this.radius = radius;
        this.announcement = announcement;
        this.kmcharge = kmcharge;
        this.packing = packing;
        this.is_selected = is_selected;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getStoreuid() {
        return storeuid;
    }

    public void setStoreuid(String storeuid) {
        this.storeuid = storeuid;
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

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getUseruid() {
        return useruid;
    }

    public void setUseruid(String useruid) {
        this.useruid = useruid;
    }

    public String getStorecate() {
        return storecate;
    }

    public void setStorecate(String storecate) {
        this.storecate = storecate;
    }

    public String getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(String minAmount) {
        this.minAmount = minAmount;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getKmcharge() {
        return kmcharge;
    }

    public void setKmcharge(String kmcharge) {
        this.kmcharge = kmcharge;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public boolean isIs_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }
}
