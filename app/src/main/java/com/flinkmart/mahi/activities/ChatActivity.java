package com.flinkmart.mahi.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.ProductAdapter;
import com.flinkmart.mahi.adapter.ProductAdapter4;
import com.flinkmart.mahi.databinding.ActivityChatBinding;
import com.flinkmart.mahi.model.Product;
import com.flinkmart.mahi.model.Product4;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    ProductAdapter4 product;
    ArrayList<Product4> products;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding=ActivityChatBinding.inflate (getLayoutInflater ());
       setContentView (binding.getRoot ());



        initProducts ( );


       getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
    }


    void getRecentProducts() {
        RequestQueue queue = Volley.newRequestQueue (this);

        String url = Constants.GET_PRODUCTS_URL;
        StringRequest request = new StringRequest (Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray productsArray = object.getJSONArray ("products");
                    for (int i = 0; i < productsArray.length ( ); i++) {
                        JSONObject childObj = productsArray.getJSONObject (i);
                        Product4 product = new Product4 (
                                childObj.getString ("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString ("image"),
                                childObj.getString ("status"),
                                childObj.getDouble ("price"),
                                childObj.getDouble ("price_discount"),
                                childObj.getInt ("stock"),
                                childObj.getInt ("id")

                        );
                        products.add (product);
                    }
                    product.notifyDataSetChanged ( );
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });

        queue.add (request);
    }

    void initProducts() {
        products = new ArrayList<> ( );
        product= new ProductAdapter4 (this, products);

        getRecentProducts ( );

        GridLayoutManager layoutManager = new GridLayoutManager (this, 3);
        binding.product.setLayoutManager (layoutManager);
        binding.product.setAdapter (product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.cart) {
            startActivity(new Intent (this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}
