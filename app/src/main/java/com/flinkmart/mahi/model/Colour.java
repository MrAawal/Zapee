package com.flinkmart.mahi.model;

public class Colour{
    public  String color;
    public  boolean is_selected;

    public Colour(){
    }

    public Colour(String color, boolean is_selected){
        this.color = color;
        this.is_selected = is_selected;
    }

    public String getColor(){
        return color;
    }

    public void setColor(String color){
        this.color = color;
    }

    public boolean isIs_selected(){
        return is_selected;
    }

    public void setIs_selected(boolean is_selected){
        this.is_selected = is_selected;
    }
}
