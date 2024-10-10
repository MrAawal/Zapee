package com.flinkmart.mahi.model;

public class Branch {
    public String storename;

    public boolean is_selected;
    public String uid;
    public Branch() {
    }

    public Branch(String storename, String uid) {
        this.storename = storename;
        this.uid = uid;
    }
    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
