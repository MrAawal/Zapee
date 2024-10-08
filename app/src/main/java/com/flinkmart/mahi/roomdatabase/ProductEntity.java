package com.flinkmart.mahi.roomdatabase;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProductEntity
{
    @PrimaryKey(autoGenerate = true)
    public int pid;

    @ColumnInfo(name = "pname")
    public String pname;

    @ColumnInfo(name = "price")
    public int price;

    @ColumnInfo(name = "qnt")
    public int qnt;
    @ColumnInfo(name = "uid")
    public String uid;


    public ProductEntity(int pid, String pname, int price, int qnt,String uid) {
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.qnt = qnt;
        this.uid = uid;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
