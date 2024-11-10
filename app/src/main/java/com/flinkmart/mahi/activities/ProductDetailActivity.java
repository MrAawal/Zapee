package com.flinkmart.mahi.activities;

import static java.lang.String.valueOf;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ActivityProductDetailBinding;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.ProductEntity;

import java.util.Random;





public class ProductDetailActivity extends AppCompatActivity {

    ActivityProductDetailBinding binding;

    CartModel currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String id = String.valueOf (getRandomNumber (11111,99999));
        String name = getIntent().getStringExtra("name");
        String image = getIntent().getStringExtra("image");
        String rate = getIntent().getStringExtra ("price");
        String qtty = "1";

        String description = getIntent().getStringExtra ("description");

        Glide.with(this)
                .load(image)
                .into(binding.productImage);
        binding.price.setText ("₹"+rate);
        binding.productDescription.setText ("Description : "+description);
        binding.name.setText (name);



        getSupportActionBar().setTitle(name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.cartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase db= Room.databaseBuilder(getApplicationContext(),AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();
                Boolean check=productDao.is_exist(Integer.parseInt(id));
                if(check==false)
                {
                    int pid=Integer.parseInt(id);
                    String pname=name;
                    int price=Integer.parseInt(rate);
                    int qnt=Integer.parseInt(qtty);
                    productDao.insertrecord(new ProductEntity (pid,pname,price,qnt,"1"));
                    binding.cartbtn.setText("You Can  Add Me More ! Come Again");
                }
                else
                {
                    Toast.makeText (ProductDetailActivity.this, "Product Already added", Toast.LENGTH_SHORT).show ( );

                }
            }

        });

    }




    public static int getRandomNumber(int min, int max) {
        return (new Random ()).nextInt((max - min) + 1) + min;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.cart) {
            startActivity(new Intent(this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}