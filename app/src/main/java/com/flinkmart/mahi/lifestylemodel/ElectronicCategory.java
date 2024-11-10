package com.flinkmart.mahi.lifestylemodel;

public class ElectronicCategory {
    public String tittle;
    public String image;
    public boolean is_selected;
    public ElectronicCategory() {
    }

    public ElectronicCategory(String tittle, String image) {
        this.tittle = tittle;
        this.image = image;
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

}

