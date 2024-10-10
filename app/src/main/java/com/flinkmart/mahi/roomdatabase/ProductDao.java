package com.flinkmart.mahi.roomdatabase;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao
{
    @Insert
    void insertrecord(ProductEntity product);

    @Query("SELECT EXISTS(SELECT * FROM ProductEntity WHERE pid = :productid)")
    Boolean is_exist(int productid);


    @Query("SELECT * FROM ProductEntity")
    List<ProductEntity> getallproduct();

    @Query("DELETE FROM ProductEntity WHERE pid = :id")
    void deleteById(int id);
//    @Update
//    void updateProduct(ProductEntity product);

}
