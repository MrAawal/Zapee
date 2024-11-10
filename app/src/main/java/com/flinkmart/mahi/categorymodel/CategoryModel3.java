package com.flinkmart.mahi.categorymodel;

public class CategoryModel3 {
    public String tittle;
    public String image;
    public boolean is_selected;
    public CategoryModel3() {
    }

    public CategoryModel3(String tittle, String image) {
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

