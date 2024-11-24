package com.flinkmart.mahi.model;

public class ImageModel {
    private String id ;
    private String orderid;
    private String uid;
    private String pname;
    private String image;
    private String  discount;
    private String  stock;
    private String description;
    private String  category;
    private String subcategory;
    private  String branch;
    public int qnt, price;
    public boolean is_selected;

    public ImageModel() {
    }

    public ImageModel(String id, String orderid, String uid, String pname, String image, String discount, String stock, String description, String category, String subcategory, String branch, int qnt, int price, boolean is_selected) {
        this.id = id;
        this.orderid = orderid;
        this.uid = uid;
        this.pname = pname;
        this.image = image;
        this.discount = discount;
        this.stock = stock;
        this.description = description;
        this.category = category;
        this.subcategory = subcategory;
        this.branch = branch;
        this.qnt = qnt;
        this.price = price;
        this.is_selected = is_selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public int getQnt() {
        return qnt;
    }

    public void setQnt(int qnt) {
        this.qnt = qnt;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isIs_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }
}