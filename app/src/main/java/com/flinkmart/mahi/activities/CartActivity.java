package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.CartAdapter;
import com.flinkmart.mahi.adapter.myadapter;
import com.flinkmart.mahi.cart.AppDatabase;
import com.flinkmart.mahi.cart.ProductDao;
import com.flinkmart.mahi.cart.ProductEntity;
import com.flinkmart.mahi.databinding.ActivityCategoryBinding;
import com.flinkmart.mahi.model.Product;
import com.flinkmart.mahi.databinding.ActivityCartBinding;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    CartAdapter adapter;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityCartBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));

        products = new ArrayList<> ( );
        Cart cart = TinyCartHelper.getCart ( );

        for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty ( ).entrySet ( )) {
            Product product = (Product) item.getKey ( );
            int quantity = item.getValue ( );
            product.setQuantity (quantity);

            products.add (product);
        }

        adapter = new CartAdapter (this, products, new CartAdapter.CartListener ( ) {
            @Override
            public void onQuantityChanged() {
                binding.subtotal.setText (String.format ("₹ %.2f", cart.getTotalPrice ( )));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager (this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration (this, layoutManager.getOrientation ( ));
        binding.cartList.setLayoutManager (layoutManager);
        binding.cartList.addItemDecoration (itemDecoration);
        binding.cartList.setAdapter (adapter);

        binding.subtotal.setText (String.format ("₹ %.2f", cart.getTotalPrice ( )));


        binding.continueBtn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                startActivity (new Intent (CartActivity.this, CheckoutActivity.class));
            }
        });

        getSupportActionBar ( ).setDisplayHomeAsUpEnabled (true);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish ( );
        return super.onSupportNavigateUp ( );
    }

}