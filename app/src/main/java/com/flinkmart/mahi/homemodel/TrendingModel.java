package com.flinkmart.mahi.homemodel;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;

public class TrendingModel  {
    public int qty;

    public int pid;

    public String pname;


    public String image;

    public int price;

    public int qnt;

    public String description;

    public String discount;
    public TrendingModel() {
    }

    public TrendingModel(int qty, int pid, String pname, String image, int price, int qnt, String description, String discount) {
        this.qty = qty;
        this.pid = pid;
        this.pname = pname;
        this.image = image;
        this.price = price;
        this.qnt = qnt;
        this.description = description;
        this.discount = discount;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQnt() {
        return qnt;
    }

    public void setQnt(int qnt) {
        this.qnt = qnt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
