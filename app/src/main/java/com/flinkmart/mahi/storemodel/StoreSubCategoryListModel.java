package com.flinkmart.mahi.storemodel;

public class StoreSubCategoryListModel {
    public String id;
    public String tittle;
    public String image;
    public boolean is_selected;
    public StoreSubCategoryListModel(){
    }

    public StoreSubCategoryListModel(String id, String tittle, String image, boolean is_selected) {
        this.id = id;
        this.tittle = tittle;
        this.image = image;
        this.is_selected = is_selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isIs_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }
}

