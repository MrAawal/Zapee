package com.flinkmart.mahi.model;

public class OrderProduct {
    public String uid;
    public String name;
    public String image;

    public int id;
    public int orderid;
    public int price;
    public int qty;

    public OrderProduct() {
    }

    public OrderProduct(String uid, String name, String image, int id, int orderid, int price, int qty) {
        this.uid = uid;
        this.name = name;
        this.image = image;
        this.id = id;
        this.orderid = orderid;
        this.price = price;
        this.qty = qty;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}