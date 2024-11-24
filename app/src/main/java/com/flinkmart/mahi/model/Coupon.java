package com.flinkmart.mahi.model;

public class Coupon {
    String code,contact;
    int value,target;

    public Coupon() {
    }

    public Coupon(String code, String contact, int value, int target) {
        this.code = code;
        this.contact = contact;
        this.value = value;
        this.target = target;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
