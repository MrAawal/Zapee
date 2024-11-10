package com.flinkmart.mahi.model;

public class CheckoutProduct {
    String name;

    public CheckoutProduct() {
    }

    public CheckoutProduct(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
