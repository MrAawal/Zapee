package com.flinkmart.mahi.model;

import com.google.firebase.Timestamp;

public class OrderPlaceModel {
    private String orderNumber;
    private String uid;
    private String customerName;
    private String customerNumber;
    private String customerAddress;
    private String customerStore;
    private String totalPrice;
    private String deliveryCharge;
    private Timestamp orderPlaceDate;
    private String lattitude;
    private String longitude;
    private String status;
    private String payment;
    private String partner;


    public OrderPlaceModel() {
    }

    public OrderPlaceModel(String orderNumber, String uid, String customerName, String customerNumber, String customerAddress, String customerStore, String totalPrice, String deliveryCharge, Timestamp orderPlaceDate, String lattitude, String longitude, String status, String payment,String partner) {
        this.orderNumber = orderNumber;
        this.uid = uid;
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.customerAddress = customerAddress;
        this.customerStore = customerStore;
        this.totalPrice = totalPrice;
        this.deliveryCharge = deliveryCharge;
        this.orderPlaceDate = orderPlaceDate;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.status = status;
        this.payment = payment;
        this.partner=partner;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerStore() {
        return customerStore;
    }

    public void setCustomerStore(String customerStore) {
        this.customerStore = customerStore;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public Timestamp getOrderPlaceDate() {
        return orderPlaceDate;
    }

    public void setOrderPlaceDate(Timestamp orderPlaceDate) {
        this.orderPlaceDate = orderPlaceDate;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }
}
