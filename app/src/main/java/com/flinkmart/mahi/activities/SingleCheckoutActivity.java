package com.flinkmart.mahi.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.BranchAdapter;
import com.flinkmart.mahi.adapter.CheckItemAdapter;
import com.flinkmart.mahi.databinding.ActivityNewCheckoutBinding;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.OrderPlaceModel;
import com.flinkmart.mahi.model.UserModel1;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.mycheckadapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Random;

public class SingleCheckoutActivity extends AppCompatActivity {

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
    int maintotal = 0;
    int more = 0;
    int Total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityNewCheckoutBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));
        auth = FirebaseAuth.getInstance ( );
        user = auth.getCurrentUser ( );


        String name = getIntent ( ).getStringExtra ("name");
        String discription = getIntent ( ).getStringExtra ("description");
        String discount = getIntent ( ).getStringExtra ("discount");
        String image = getIntent ( ).getStringExtra ("image");
        String id = getIntent ( ).getStringExtra ("id");
        String price = getIntent ( ).getStringExtra ("price");
        String quantity = getIntent ( ).getStringExtra ("quantity");


        int pric = Integer.parseInt (price);
        int quan = Integer.parseInt (quantity);
        int total = pric * quan;
        maintotal += total;
        Total = maintotal + del + gst;
        more = 500 - maintotal;

        if (maintotal > 500) {
            Total = maintotal;
            binding.MaxTotal.setText (String.valueOf ("₹" + maintotal));
            binding.Del.setText ("FREE");
            binding.tax.setText ("FREE");
            binding.tax.setTextColor (getColor (R.color.teal_700));
            binding.Del.setTextColor (getColor (R.color.teal_700));
            binding.view.setText ("Congragulation You Got Free Delivery & Free Bag");
            binding.view.setTextColor (getColor (R.color.purple_500));
        } else {
            binding.MaxTotal.setText (String.valueOf ("₹" + Total));
            binding.view.setText ("Add More ₹" + more + " For Get Free Delivery & Bag");
            binding.view.setTextColor (getColor (R.color.red));
        }

        binding.subtotal.setText (String.valueOf ("₹" + maintotal));


        progressDialog = new ProgressDialog (this);
        progressDialog.setCancelable (false);
        progressDialog.setMessage ("Order Processing...");


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
                            binding.Email.setText (userModel.getPin ( ));
                            binding.pinnumber.setText (userModel.getAddress ( ));
                        } else {
                            Intent intent = new Intent (SingleCheckoutActivity.this, CompleteProfileActivity.class);
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
                    if (branch != null) {
                        binding.address.setText ("Order Pick-up From " + branch.getStorename ( ));
                    } else {

                        Intent intent = new Intent (SingleCheckoutActivity.this, CompleteProfileActivity.class);
                        startActivity (intent);
                    }
                }
            }
        });


        progressDialog = new ProgressDialog (this);
        progressDialog.setCancelable (false);
        progressDialog.setMessage ("Processing...");


        uid = FirebaseAuth.getInstance ( ).getUid ( );
        TextView rateview = findViewById (R.id.subtotal);
        binding.checkoutBtn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                String orderNumber = String.valueOf (getRandomNumber (11111, 99999));
                processOrder (orderNumber);
            }
        });


        binding.imageButton.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                bottomsheet ( );
            }
        });

        getSupportActionBar ( ).setDisplayHomeAsUpEnabled (true);

    }


    void processOrder(String orderNumber) {

        OrderPlaceModel orderPlaceModel = new OrderPlaceModel (orderNumber, uid, userModel.getUsername ( ), userModel.getPhone ( ), userModel.getAddress ( ), branch.getStorename ( ), String.valueOf (Total), String.valueOf (del), Timestamp.now ( ), null, null, "Pending", "cod","pending");
        FirebaseFirestore.getInstance ( )
                .collection ("orders")
                .document (orderNumber)
                .set (orderPlaceModel);

        String name = getIntent ( ).getStringExtra ("name");

        String image = getIntent ( ).getStringExtra ("image");
        String price = getIntent ( ).getStringExtra ("price");
        String quantity = getIntent ( ).getStringExtra ("quantity");

        int pric = Integer.parseInt (price);
        int quan = Integer.parseInt (quantity);
        int total = pric * quan;

        maintotal += total;
        Total = maintotal + del + gst;
        more = 500 - maintotal;


        Product cart = new Product (Integer.parseInt (orderNumber), name, image, maintotal, Integer.parseInt (quantity),"","");
        FirebaseFirestore.getInstance ( )
                .collection ("OrderProduct")
                .document ( )
                .set (cart);



    }






    @Override
    public boolean onSupportNavigateUp() {
        finish ( );
        return super.onSupportNavigateUp ( );
    }


    private void bottomsheet() {

        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog ( this);
        View view= LayoutInflater.from (SingleCheckoutActivity.this).inflate (R.layout.branchsheet,(LinearLayout)findViewById (R.id.cartbtn),false);
        bottomSheetDialog.setContentView (view);
        bottomSheetDialog.show ();

        BranchAdapter branchAdapter;
        String pin = userModel.getPin ();

        RecyclerView branchList=view.findViewById (R.id.branchList);
        Button setButton=view.findViewById (R.id.button);


        branchAdapter=new BranchAdapter (this)  ;
        branchList.setAdapter (branchAdapter);
        branchList.setLayoutManager (new LinearLayoutManager (this));

        getStore (pin,branchAdapter);

        setButton.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
                setStore (branchAdapter);
                bottomSheetDialog.cancel();
                Intent intent = new Intent (SingleCheckoutActivity.this, SingleCheckoutActivity.class);
                startActivity (intent);
                finish ();
            }
        });





    }

    void  getStore(String pin, BranchAdapter branchAdapter){

        FirebaseFirestore.getInstance ()
                .collection ("branch")
                .whereEqualTo ("pincode",pin)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            Branch loanModel=ds.toObject (Branch.class);
                            branchAdapter.addProduct (loanModel);
                        }

                    }
                });
    }


    public static int getRandomNumber(int min, int max) {
        return (new Random ( )).nextInt ((max - min) + 1) + min;
    }

    public void setStore(BranchAdapter branchAdapter){
        String uid=FirebaseAuth.getInstance ( ).getUid ( );
        List<Branch>itemList=branchAdapter.getSelectedItem();
        for (int i=0;i<itemList.size ();i++){
            Branch branch1 = itemList.get (i);
            branch1.setUid (FirebaseAuth.getInstance ( ).getUid ( ));
            FirebaseFirestore.getInstance ( )
                    .collection ("userstore")
                    .document (uid)
                    .set (branch1);
            onStateNotSaved ();
        }

    }
}