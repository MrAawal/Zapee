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
import com.flinkmart.mahi.adapter.OrderAdapter;
import com.flinkmart.mahi.databinding.ActivityOrdersBinding;
import com.flinkmart.mahi.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {


    ActivityOrdersBinding binding;
    OrderAdapter product;
    ArrayList<Order> products;
    FirebaseAuth auth;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance ();
        user = auth.getCurrentUser ();

        if(user==null){
            Toast.makeText (this, "Please Login First", Toast.LENGTH_SHORT).show ( );
            Intent i = new Intent (OrdersActivity.this, LoginActivity.class);
            startActivity (i);
            finish ( );
        }else{
            products = new ArrayList<>();
            product = new OrderAdapter (this, products);

            String uid=user.getUid();

            getRecentProducts (uid);

            GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
            binding.orderList.setLayoutManager(layoutManager);
            binding.orderList.setAdapter(product);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

    }

    void getRecentProducts(String uid) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://flink-mart.com/android/restapi/read.php?comment="+uid;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getString("status").equals("success")){
                    JSONArray productsArray = object.getJSONArray("products");
                    for(int i =0; i< productsArray.length(); i++) {
                        JSONObject childObj = productsArray.getJSONObject(i);
                        Order product = new Order (
                                childObj.getInt("id"),
                                childObj.getString ("buyer"),
                                childObj.getString ("address"),
                                childObj.getString ("status"),
                                childObj.getString ("code"),
                                childObj.getString ("payment_status"),
                                childObj.getString ("total_fees")

                        );
                        products.add(product);
                    }
                    product.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
        });

        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}