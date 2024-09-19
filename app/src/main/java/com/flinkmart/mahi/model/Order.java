package com.flinkmart.mahi.model;

public class Order {
    private int  id;
    private String buyer,address,status,code,payment_status,total_fees;

    public Order(int id, String buyer, String address, String status, String code, String payment_status, String total_fees) {
        this.id = id;
        this.buyer = buyer;
        this.address = address;
        this.status = status;
        this.code = code;
        this.payment_status = payment_status;
        this.total_fees = total_fees;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getTotal_fees() {
        return total_fees;
    }

    public void setTotal_fees(String total_fees) {
        this.total_fees = total_fees;
    }
}