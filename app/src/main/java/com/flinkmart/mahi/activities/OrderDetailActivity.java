package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flinkmart.mahi.adapter.OrderDetailAdapter;
import com.flinkmart.mahi.databinding.ActivityOrderDetailBinding;
import com.flinkmart.mahi.model.OrderDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderDetailActivity extends AppCompatActivity {

    ActivityOrderDetailBinding binding;
    OrderDetailAdapter product;
    ArrayList<OrderDetail>products;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));

        products = new ArrayList<>();
        product = new OrderDetailAdapter (this, products);


        int OrderId = getIntent().getIntExtra("id", 0);


        auth = FirebaseAuth.getInstance ();
        user = auth.getCurrentUser ();

        if(user==null){
            Intent i = new Intent (getApplicationContext( ),LoginActivity.class);
            startActivity (i);
            finish ( );
        }else{


            getProducts(OrderId);

        }



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        binding.orderDetail.setLayoutManager(layoutManager);
        binding.orderDetail.setAdapter(product);
    }

    void getProducts(int OrderId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://flink-mart.com/android/orderDetails/read.php?order_id="+OrderId;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString("status").equals("success")){
                    JSONArray productsArray = object.getJSONArray("products");
                    for(int i =0; i< productsArray.length(); i++) {
                        JSONObject childObj = productsArray.getJSONObject(i);
                        OrderDetail product = new OrderDetail (

                        childObj.getInt ("order_id"),
                        childObj.getString ("amount"),
                        childObj.getString ("product_name"),
                        childObj.getString ("price_item")
                        );
                        products.add(product);
                    }
                    product.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText (this, "Login First", Toast.LENGTH_SHORT).show ( );
        });

        queue.add(request);
    }

    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
