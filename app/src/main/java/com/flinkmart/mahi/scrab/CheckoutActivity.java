package com.flinkmart.mahi.scrab;

import static com.flinkmart.mahi.scrab.FavouriteActivity.cartItemList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activitylogin.CompleteProfileActivity;
import com.flinkmart.mahi.activities.DeliveryDetailActivity;
import com.flinkmart.mahi.activitylogin.LoginActivity;
import com.flinkmart.mahi.adapter.CheckItemAdapter;
import com.flinkmart.mahi.databinding.ActivityCheckoutBinding;
import com.flinkmart.mahi.model.BranchModel;
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

public class CheckoutActivity extends AppCompatActivity {

    ActivityCheckoutBinding binding;
    final int del = 30;
    final int gst = 5;
    private String uid;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user;
    UserModel1 userModel;
    BranchModel branch;
    CheckItemAdapter checkItemAdapter;
    int maintotal=0;
    int Total;
    private Timestamp Timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityCheckoutBinding.inflate (getLayoutInflater ( ));
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
                            Intent intent = new Intent (CheckoutActivity.this, CompleteProfileActivity.class);
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
                    branch = task.getResult ( ).toObject (BranchModel.class);
                    if (branch != null) {
                        binding.address.setText ("Order Pick-up From " + branch.getStorename ( ));
                    }else{
                        Intent intent = new Intent (CheckoutActivity.this, CompleteProfileActivity.class);
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

            }
        });

        getSupportActionBar ( ).setDisplayHomeAsUpEnabled (true);



    }

    protected void onStart() {
        super.onStart ( );
        for (int i=0;i<cartItemList.size ();i++){
            Favourite favourite=cartItemList.get(i);
            int pric=favourite.getPrice ();
            int quan=favourite.getQty ();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.cart) {
            startActivity(new Intent (this, NewCartActivity.class));
        } else if (item.getItemId() == R.id.fav) {
            startActivity(new Intent (this, FavouriteActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    void getProduct(){
        getAllProduct ();
        checkItemAdapter=new CheckItemAdapter (this)  ;
        binding.cartList.setAdapter (checkItemAdapter);
        binding.cartList.setLayoutManager (new GridLayoutManager (this,1));
    }
    private void getAllProduct(){
        String uid= FirebaseAuth.getInstance( ).getUid( );
        FirebaseFirestore.getInstance ()
                .collection ("favourite")
                .whereEqualTo ("uid",uid)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            Favourite product=ds.toObject (Favourite.class);
                            checkItemAdapter.addProduct(product);
                        }


                    }
                });
    }
    void processOrder(String orderNumber){
        OrderPlaceModel orderPlaceModel = new OrderPlaceModel (orderNumber, uid, userModel.getUsername ( ), userModel.getPhone ( ), userModel.getAddress ( ), branch.getStorename ( ),branch.getStoreuid (), String.valueOf (Total), String.valueOf (del), Timestamp.now ( ), null, null, "Pending", "cod","pending");
        FirebaseFirestore.getInstance ( )
                .collection ("orders")
                .document (orderNumber)
                .set (orderPlaceModel);
    }

    void proccesProduct(String orderNumber){
        binding.progress.setVisibility(View.VISIBLE);
        for (int i = 0; i <cartItemList.size(); i++){
            Favourite favourite = cartItemList.get (i);
            favourite.setOrderid (orderNumber);
            favourite.setBranch (orderNumber);

            FirebaseFirestore.getInstance ( )
                    .collection ("OrderProduct")
                    .document ()
                    .set (favourite);
            Intent intent = new Intent (CheckoutActivity.this, DeliveryDetailActivity.class);
            intent.putExtra("orderNumber", orderNumber);
            intent.putExtra("totalPrice", String.valueOf (Total));
            intent.putExtra("address",userModel.getAddress () );
            intent.putExtra("payment", "Cash on delivery");
            intent.putExtra("date", Timestamp.now ());
            startActivity (intent);
        }
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



