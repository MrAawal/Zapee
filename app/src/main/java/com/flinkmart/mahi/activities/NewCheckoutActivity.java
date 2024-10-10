package com.flinkmart.mahi.activities;

import static com.flinkmart.mahi.activities.CheckoutActivity.getRandomNumber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.CartAdapter;
import com.flinkmart.mahi.adapter.Checkoutadapter;
import com.flinkmart.mahi.adapter.NewCartAdapter;
import com.flinkmart.mahi.databinding.ActivityCheckoutBinding;
import com.flinkmart.mahi.databinding.ActivityNewCheckoutBinding;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.OrderPlaceModel;
import com.flinkmart.mahi.model.Product;
import com.flinkmart.mahi.model.ProductTiny;
import com.flinkmart.mahi.model.UserModel1;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.OrderProduct;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.ProductEntity;
import com.flinkmart.mahi.roomdatabase.myadapter2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NewCheckoutActivity extends AppCompatActivity {

    ActivityNewCheckoutBinding binding;
    double totalPrice = 0;
    final int del = 30;
    final int gst = 5;
    private  String uid;
    private  String uuid;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user;
    UserModel1 userModel;
    Branch branch;
    ArrayList<ProductTiny> products;
    TextView rate;
    TextView rateview;
    Cart cart;
NewCartAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance ();
        user=auth.getCurrentUser();
        if(user==null){
            Intent i=new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
            finish();
        }else{
            FirebaseUtil.currentUserDetails ().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful ())  {
                        userModel=  task.getResult ().toObject (UserModel1.class);
                        if(userModel!=null){
                            //for manual input---------------------------
                            binding.nameBox.setText (userModel.getUsername ());
                            binding.phoneBox.setText (userModel.getPhone ());
                            binding.addressBox.setText (userModel.getAddress ());
                            binding.Name.setText (userModel.getUsername ());
                            binding.Contact.setText (userModel.getPhone ());
                            binding.Email.setText (userModel.getAddress ());
                            binding.pinnumber.setText (userModel.getPin());
                        }else{
                            Intent intent = new Intent (NewCheckoutActivity.this, CompleteProfileActivity.class);
                            startActivity (intent);
                        }

                    }
                }
            });
        }
        FirebaseUtil.currentUserStore().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful ())  {
                    branch=  task.getResult ().toObject (Branch.class);
                    if(branch!=null){
                        binding.address.setText ("Your Selected Store : "+branch.getStorename ());
                    }else{
                        Intent intent = new Intent (NewCheckoutActivity.this, CompleteProfileActivity.class);
                        startActivity (intent);
                    }
                }
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        products = new ArrayList<>();
        cart = TinyCartHelper.getCart();

        products = new ArrayList<>();
        Cart cart = TinyCartHelper.getCart();

        for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            ProductTiny product = (ProductTiny) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);

            products.add(product);
        }

        adapter = new NewCartAdapter(this, products, new NewCartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subtotal.setText(String.format("₹ %.2f",cart.getTotalPrice()));
                totalPrice = (cart.getTotalPrice().doubleValue() + gst) +del;
                binding.total.setText("₹ " + totalPrice);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.cartList.setLayoutManager(layoutManager);
        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setAdapter(adapter);

        binding.subtotal.setText(String.format("₹ %.2f",cart.getTotalPrice()));
        totalPrice = (cart.getTotalPrice().doubleValue() + gst) +del;
        binding.total.setText("₹ " + totalPrice);


        binding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processOrder();

            }
        });


        binding.imageButton.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (NewCheckoutActivity.this, BranchActivity.class);
                intent.putExtra("pincode", userModel.getPin ());
                startActivity (intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    void processOrder() {
        uuid=FirebaseAuth.getInstance ( ).getUid ( );
        String orderNumber = String.valueOf (getRandomNumber (11111, 99999));
        OrderPlaceModel orderPlaceModel = new OrderPlaceModel (orderNumber, uuid, userModel.getUsername ( ), userModel.getPhone ( ), userModel.getAddress ( ), branch.getStorename ( ), String.valueOf (totalPrice), String.valueOf (del), Timestamp.now ( ), null, null, "Pending", "cod");
        FirebaseFirestore.getInstance ( )
                .collection ("orders")
                .document (orderNumber)
                .set (orderPlaceModel);

        for(Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()) {
            ProductTiny product = (ProductTiny) item.getKey ( );
            int quantity = item.getValue ( );
            product.setQuantity (quantity);

            OrderProduct orderProduct = new OrderProduct (uid, product.getName (), product.getId (), Integer.parseInt (orderNumber), (int) product.getPrice(),quantity);
            FirebaseFirestore.getInstance ( )
                    .collection ("OrderProduct")
                    .document ( )
                    .set (orderProduct);

                Intent intent = new Intent (NewCheckoutActivity.this, DeliveryDetailActivity.class);
                intent.putExtra("orderNumber", orderNumber);
                intent.putExtra("totalPrice", String.valueOf (totalPrice));
            intent.putExtra("address",userModel.getAddress () );
            intent.putExtra("payment", "Cash on delivery");
            intent.putExtra("date", Timestamp.now ());
                startActivity (intent);

        }

        }

    public static int getRandomNumber(int min, int max) {
        return (new Random ()).nextInt((max - min) + 1) + min;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}