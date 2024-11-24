package com.flinkmart.mahi.model;

import com.google.firebase.Timestamp;

public class Order {
    private String orderNumber,payment,status,totalPrice,customerAddress,customerName,customerNumber,customerStore,partner;
    private Timestamp orderPlaceDate;

    public Order() {
    }

    public Order(String orderNumber, String payment, String status, String totalPrice, String customerAddress, String customerName, String customerNumber, String customerStore, Timestamp orderPlaceDat,String partner) {
        this.orderNumber = orderNumber;
        this.payment = payment;
        this.status = status;
        this.totalPrice = totalPrice;
        this.customerAddress = customerAddress;
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.customerStore = customerStore;
        this.orderPlaceDate = orderPlaceDate;
        this.partner=partner;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
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

    public String getCustomerStore() {
        return customerStore;
    }

    public void setCustomerStore(String customerStore) {
        this.customerStore = customerStore;
    }

    public Timestamp getOrderPlaceDate() {
        return orderPlaceDate;
    }

    public void setOrderPlaceDate(Timestamp orderPlaceDate) {
        this.orderPlaceDate = orderPlaceDate;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }
}