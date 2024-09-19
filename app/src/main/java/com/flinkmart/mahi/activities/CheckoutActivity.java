package com.flinkmart.mahi.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.CartAdapter;
import com.flinkmart.mahi.databinding.ActivityCheckoutBinding;
import com.flinkmart.mahi.model.Product;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    ActivityCheckoutBinding binding;

    CartAdapter adapter;
    ArrayList<Product> products;
    double totalPrice = 0;
    final int del = 30;
    final int gst = 5;
    private  String uid;
    ProgressDialog progressDialog;
    Cart cart;
    TextView profileName, profileEmail, profileContact;

    EditText nameInput;
    EditText addressInput;
    EditText phoneNumber;

    FirebaseAuth auth;
    FirebaseUser user;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Auto fill---------------------------
        profileName = findViewById(R.id.Name);
        profileEmail = findViewById(R.id.Email);
        profileContact = findViewById(R.id.Contact);

        //Field input----------------------------
        nameInput = findViewById(R.id.nameBox);
        addressInput=findViewById (R.id.addressBox);
        phoneNumber=findViewById (R.id.phoneBox);


        auth=FirebaseAuth.getInstance ();
        user=auth.getCurrentUser();




        if(user==null){
            Intent i=new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
            finish ();
        }else {
            FirebaseUtil.currentUserDetails ().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful ())  {
                        userModel=  task.getResult ().toObject (UserModel.class);
                        if(userModel!=null){
                            //for manual input---------------------------
                            nameInput.setText (userModel.getUsername ());
                            phoneNumber.setText (userModel.getPhone ());
                            addressInput.setText (userModel.getAddress ());

                            //for profile Auto fill----------------------
                            profileName.setText (userModel.getUsername ());
                            profileContact.setText (userModel.getPhone ());
                            profileEmail.setText (user.getEmail());
                        }

                    }
                }
            });
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        products = new ArrayList<>();

        cart = TinyCartHelper.getCart();

        for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            Product product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);
            products.add(product);
        }

        adapter = new CartAdapter(this, products, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subtotal.setText(String.format("₹ %.2f",cart.getTotalPrice()));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setAdapter(adapter);

        binding.subtotal.setText(String.format("₹ %.2f",cart.getTotalPrice()));
        totalPrice = (cart.getTotalPrice().doubleValue() *gst/100+del) + cart.getTotalPrice().doubleValue();
        uid=FirebaseAuth.getInstance().getUid();
        binding.total.setText("₹ " + totalPrice);

        binding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processOrder();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void processOrder() {
        progressDialog.show();
                RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject productOrder = new JSONObject();
        JSONObject dataObject = new JSONObject();



        try {
            productOrder.put("address",binding.addressBox.getText().toString());
            productOrder.put("buyer",binding.nameBox.getText().toString());
            productOrder.put("serial", binding.phoneBox.getText ().toString ());

            productOrder.put("email",FirebaseAuth.getInstance ().getCurrentUser ().getEmail ());
            productOrder.put("phone",userModel.getPhone());
            productOrder.put("comment", FirebaseAuth.getInstance ().getCurrentUser ().getUid ());
            productOrder.put("status", "WAITING");

            productOrder.put("created_at",Calendar.getInstance().getTimeInMillis());
            productOrder.put("last_update",Calendar.getInstance().getTimeInMillis());
            productOrder.put("date_ship", Calendar.getInstance().getTimeInMillis());

            productOrder.put("tax",gst);
            productOrder.put("total_fees",totalPrice);



            for(Map.Entry<Item,Integer> items : cart.getAllItemsWithQty().entrySet()) {
                Product products = (Product) items.getKey();
                int quantity = items.getValue();
                products.setQuantity(quantity);
                JSONObject productObj = new JSONObject();

                productOrder.put ("shipping", products.getName());
                productOrder.put ("shipping_location",products.getName ());
                productOrder.put ("shipping_rate",products.getId());

            }

            JSONArray product_order_detail = new JSONArray();
            for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
                Product product = (Product) item.getKey();
                int quantity = item.getValue();
                product.setQuantity(quantity);
                JSONObject productObj = new JSONObject();

                productObj.put("amount", quantity);
                productObj.put("price_item", product.getPrice());
                productObj.put("product_id", product.getId());
                productObj.put("product_name", product.getName());
                product_order_detail.put(productObj);
            }

            dataObject.put("product_order",productOrder);
            dataObject.put("product_order_detail",product_order_detail);

            Log.e("err", dataObject.toString());

        } catch (JSONException e) {}

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.POST_ORDER_URL, dataObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("success")) {
                        Toast.makeText(CheckoutActivity.this, "Success order.", Toast.LENGTH_SHORT).show();
                        String orderNumber = response.getJSONObject("data").getString("code");
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Successful")
                                .setCancelable(false)
                                .setMessage("Your order number is: " + orderNumber)
                                .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(CheckoutActivity.this, PaymentActivity.class);
                                        intent.putExtra("orderCode", orderNumber);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton ("Cash On Delivery", new DialogInterface.OnClickListener ( ) {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(CheckoutActivity.this, OrdersActivity.class);
                                        startActivity(intent);
                                    }
                                })




                                .show();
                    } else {
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Failed")
                                .setMessage("Something went wrong, please try again.")
                                .setCancelable(false)
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                        Toast.makeText(CheckoutActivity.this, "Failed order.", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                    Log.e("res", response.toString());
                } catch (Exception e) {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Security","secure_code");
                return headers;
            }
        } ;

        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}