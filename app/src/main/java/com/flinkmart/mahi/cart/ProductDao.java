package com.flinkmart.mahi.cart;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.flinkmart.mahi.model.Product;

import java.util.List;

@Dao
public interface ProductDao
{
    @Insert
    void insertrecord(ProductEntity productEntity);


    @Query("SELECT EXISTS(SELECT * FROM ProductEntity WHERE id = :productid)")
    Boolean is_exist(int productid);


    @Query("SELECT * FROM ProductEntity")
    List<ProductEntity> getallproduct();

    @Query("DELETE FROM ProductEntity WHERE id = :id")
    void deleteById(int id);
}
