package com.flinkmart.mahi.model;

import java.io.Serializable;

public class SingleSelection implements Serializable {
    public  boolean isChecked=false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
