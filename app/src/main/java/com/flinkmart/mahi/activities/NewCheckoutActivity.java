package com.flinkmart.mahi.activities;

import static com.flinkmart.mahi.activities.FavouriteActivity.cartItemList;
import static com.flinkmart.mahi.activities.NewCartActivity.cartList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.CheckItemAdapter;
import com.flinkmart.mahi.databinding.ActivityNewCheckoutBinding;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Favourite;
import com.flinkmart.mahi.model.OrderPlaceModel;
import com.flinkmart.mahi.model.UserModel1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Random;

public class NewCheckoutActivity extends AppCompatActivity {

    ActivityNewCheckoutBinding binding;
    final int del = 30;
    final int gst = 5;
    private String uid;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user;
    UserModel1 userModel;
    Branch branch;
    CheckItemAdapter checkItemAdapter;
    int maintotal=0;
    int Total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityNewCheckoutBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));
        auth = FirebaseAuth.getInstance ( );
        user = auth.getCurrentUser ( );
        if (user == null) {
            Intent i = new Intent (getApplicationContext ( ), LoginActivity.class);
            startActivity (i);
            finish ( );
        } else {
            FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful ( )) {
                        userModel = task.getResult ( ).toObject (UserModel1.class);
                        if (userModel != null) {
                            //for manual input---------------------------
                            binding.nameBox.setText (userModel.getUsername ( ));
                            binding.phoneBox.setText (userModel.getPhone ( ));
                            binding.addressBox.setText (userModel.getAddress ( ));
                            binding.Name.setText (userModel.getUsername ( ));
                            binding.Contact.setText (userModel.getPhone ( ));
                            binding.Email.setText (userModel.getAddress ( ));
                            binding.pinnumber.setText (userModel.getPin ( ));
                        } else {
                            Intent intent = new Intent (NewCheckoutActivity.this, CompleteProfileActivity.class);
                            startActivity (intent);
                        }

                    }
                }
            });
        }
        FirebaseUtil.currentUserStore ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    branch = task.getResult ( ).toObject (Branch.class);
                    if (branch!= null) {
                        binding.address.setText ("Your Selected Store : " + branch.getStorename ( ));
                    } else {

                        Intent intent = new Intent (NewCheckoutActivity.this,CompleteProfileActivity.class);
                        startActivity (intent);
                    }
                }
            }
        });


        progressDialog = new ProgressDialog (this);
        progressDialog.setCancelable (false);
        progressDialog.setMessage ("Processing...");


        uid = FirebaseAuth.getInstance ( ).getUid ( );

        getProduct ();
        binding.checkoutBtn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                String orderNumber = String.valueOf (getRandomNumber (11111, 99999));
                processOrder (orderNumber);
                proccesProduct(orderNumber);
            }
        });


        binding.imageButton.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (NewCheckoutActivity.this, BranchActivity.class);
                intent.putExtra ("pincode", userModel.getPin ( ));
                startActivity (intent);
            }
        });

        getSupportActionBar ( ).setDisplayHomeAsUpEnabled (true);

    }

    protected void onStart() {
        super.onStart ( );
        for (int i=0;i< cartList.size ( );i++){
            CartModel cart=cartList.get (i);
            int pric=cart.getPrice ();
            int quan=cart.getQty ();
            int total=pric*quan;
            maintotal+=total;
            Total=maintotal+del+gst;
        }
        binding.subtotal.setText (String.valueOf ("₹"+maintotal));
        binding.total.setText (String.valueOf (Total));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);

        return super.onCreateOptionsMenu(menu);
    }

    void getProduct(){
        getAllProduct ();
        checkItemAdapter=new CheckItemAdapter (this)  ;
        binding.cartList.setAdapter (checkItemAdapter);
        binding.cartList.setLayoutManager (new GridLayoutManager (this,1));
    }
    private void getAllProduct() {
        String uid = FirebaseAuth.getInstance ( ).getUid ( );
        FirebaseFirestore.getInstance ( )
                .collection ("cart")
                .whereEqualTo ("uid", uid)
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            Favourite product = ds.toObject (Favourite.class);
                            checkItemAdapter.addProduct (product);
                        }


                    }
                }).addOnCompleteListener (new OnCompleteListener<QuerySnapshot> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    }
                });
    }
    void processOrder(String orderNumber){

        OrderPlaceModel orderPlaceModel = new OrderPlaceModel (orderNumber, uid, userModel.getUsername ( ), userModel.getPhone ( ), userModel.getAddress ( ), branch.getStorename ( ), String.valueOf (Total), String.valueOf (del), Timestamp.now ( ), null, null, "Pending", "cod");
        FirebaseFirestore.getInstance ( )
                .collection ("orders")
                .document (orderNumber)
                .set (orderPlaceModel);
    }

    void proccesProduct(String orderNumber){
        binding.progress.setVisibility(View.VISIBLE);
        progressDialog.setMessage ("Proccesing");
        progressDialog.show ( );

        for (int i = 0; i <cartList.size(); i++){
                CartModel cart = cartList.get (i);
                cart.setOrderid (orderNumber);
                cart.setBranch (orderNumber);
                FirebaseFirestore.getInstance ( )
                        .collection ("OrderProduct")
                        .document ()
                        .set (cart);
                Intent intent = new Intent (NewCheckoutActivity.this, DeliveryDetailActivity.class);
                intent.putExtra("orderNumber", orderNumber);
                intent.putExtra("totalPrice", String.valueOf (Total));
                intent.putExtra("address",userModel.getAddress () );
                intent.putExtra("payment", "Cash on delivery");
                intent.putExtra("date", Timestamp.now ());
                startActivity (intent);
            }
            progressDialog.dismiss ();


    }

    public static int getRandomNumber(int min, int max) {
        return (new Random ( )).nextInt ((max - min) + 1) + min;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish ( );
        return super.onSupportNavigateUp ( );
    }
}