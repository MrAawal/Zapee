package com.flinkmart.mahi.model;

public class Size {
    public  String size;

    public  boolean is_selected;

    public Size() {
    }

    public Size(String size, boolean is_selected) {
        this.size = size;
        this.is_selected = is_selected;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isIs_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }
}
