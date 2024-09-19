package com.flinkmart.mahi.model;

public class OrderDetail {
    private int  order_id;
    private String product_name,amount,price_item;

    public OrderDetail(int order_id, String product_name, String amount, String price_item) {
        this.order_id = order_id;
        this.product_name = product_name;
        this.amount = amount;
        this.price_item = price_item;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice_item() {
        return price_item;
    }

    public void setPrice_item(String price_item) {
        this.price_item = price_item;
    }
}