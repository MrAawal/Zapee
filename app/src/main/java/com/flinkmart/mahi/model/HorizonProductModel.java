package com.flinkmart.mahi.model;

import com.hishd.tinycart.model.Item;

import java.math.BigDecimal;

public class HorizonProductModel implements Item {
    public String tittle,image,id,price, discount,stock,description;

    public HorizonProductModel() {
    }


    public HorizonProductModel(String tittle, String image, String id, String price, String discount, String stock, String description) {
        this.tittle = tittle;
        this.image = image;
        this.id = id;
        this.price = price;
        this.discount = discount;
        this.stock = stock;
        this.description = description;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public BigDecimal getItemPrice() {
        return null;
    }

    @Override
    public String getItemName() {
        return null;
    }
}