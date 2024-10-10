package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ActivityCartBinding;
import com.flinkmart.mahi.databinding.ActivityCategoryBinding;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.ProductEntity;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.myadapter;
import com.flinkmart.mahi.roomdatabase.myadapter2;

import java.util.List;

public class CartActivity extends AppCompatActivity {
        ActivityCartBinding binding;
        RecyclerView recview;
        TextView rateview;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding= ActivityCartBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            rateview=findViewById(R.id.subtotal);


            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
            AppDatabase.class, "cart_db").allowMainThreadQueries().build();
            ProductDao productDao = db.ProductDao();


            List<ProductEntity> products=productDao.getallproduct();
            binding.cartList.setLayoutManager(new LinearLayoutManager (this));
            myadapter2 adapter=new myadapter2 (products,rateview);
            binding.cartList.setAdapter(adapter);
            int sum=0,i;
            for(i=0;i< products.size();i++)
                sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());
            binding.subtotal.setText("₹"+sum);
            binding.continueBtn.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (CartActivity.this, CheckoutActivity.class);
                    startActivity (intent);
                }
            });

        }

    }