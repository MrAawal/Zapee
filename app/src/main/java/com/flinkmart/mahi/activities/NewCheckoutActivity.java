package com.flinkmart.mahi.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.Update;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.BranchAdapter;
import com.flinkmart.mahi.adapter.CheckItemAdapter;
import com.flinkmart.mahi.databinding.ActivityNewCheckoutBinding;
import com.flinkmart.mahi.map.MapActivity;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.Coupon;
import com.flinkmart.mahi.model.OrderPlaceModel;
import com.flinkmart.mahi.model.UserModel1;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.mycheckadapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NewCheckoutActivity extends AppCompatActivity {

    ActivityNewCheckoutBinding binding;
    final int del = 30;
    final int gst = 5;

    int Discount=0;
    int DiscountSavePrice=0;
    private String uid;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    TextView couponInput;
    int couponTarget=0;
    FirebaseUser user;
    UserModel1 userModel;
    Branch branch;
    CheckItemAdapter checkItemAdapter;
    int maintotal=0;
    int more=0;
    int Total;
    int DiscountTotal=0;

    Coupon coupon;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityNewCheckoutBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));
        auth = FirebaseAuth.getInstance ( );
        user = auth.getCurrentUser ( );

        progressDialog = new ProgressDialog (this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Order Processing...");

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();


        List<Product> products=productDao.getallproduct();

        if(products.size()==0){
                   binding.mainlayout.setVisibility (View.GONE);
                   binding.button3.setVisibility (View.VISIBLE);
                   binding.textView.setVisibility (View.VISIBLE);
                   binding.emty.setVisibility (View.VISIBLE);
                   binding.linearLayout7.setVisibility (View.GONE);
                   binding.button3.setOnClickListener (new View.OnClickListener ( ) {
                       @Override
                       public void onClick(View v) {
                           Intent i = new Intent (getApplicationContext ( ), MainActivity.class);
                           startActivity (i);
                       }
                   });
        }

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
//                            binding.nameBox.setText (userModel.getUsername ( ));
                            binding.phoneBox.setText (userModel.getPhone ( ));
                            binding.addressBox.setText (userModel.getAddress ( ));
                            binding.Name.setText (userModel.getUsername ( ));
                            binding.Contact.setText (userModel.getPhone ( ));
                            binding.Email.setText (userModel.getPin ( ));
                            binding.pinnumber.setText (userModel.getAddress ( ));
                        } else {
                            Intent intent = new Intent (NewCheckoutActivity.this, CompleteProfileActivity.class);
                            startActivity (intent);
                            finish ();
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
                        binding.address.setText ("Order Pick-up From " + branch.getStorename ( ));
                    } else {
                        Intent intent = new Intent (NewCheckoutActivity.this,CompleteProfileActivity.class);
                        startActivity (intent);
                    }
                }
            }
        });

        SharedPreferences sp=getSharedPreferences("location",MODE_PRIVATE);
        if(sp.contains("latitude"))
        {
//            Toast.makeText (this, "Sharing Current Location"+(sp.getString ("latitude","")), Toast.LENGTH_SHORT).show ( );
        } else {
            startActivity(new Intent (getApplicationContext (), MapActivity.class));
        }

        progressDialog = new ProgressDialog (this);
        progressDialog.setCancelable (false);
        progressDialog.setMessage ("Processing...");


        uid = FirebaseAuth.getInstance ( ).getUid ( );
        TextView rateview=findViewById (R.id.subtotal);
        getProduct (rateview);
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
                bottomsheet ();
            }
        });

        getSupportActionBar ( ).setDisplayHomeAsUpEnabled (true);

    }

    protected void onStart() {







                    AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        List<Product> products=productDao.getallproduct();
        super.onStart ( );
        for (int i=0;i<products.size ();i++){
            Product favourite=products.get(i);

            int pric=favourite.getPrice ();
            int quan=favourite.getQnt ();
            int total=pric*quan;
            int discount = Integer.parseInt (favourite.getDiscount ( ))*quan;
            maintotal+=total;
            Total=maintotal+del+gst;

            more=500-maintotal;

            DiscountTotal+=discount;
            Discount=DiscountTotal-maintotal;

        }
        binding.mrptotal.setText ("₹"+DiscountTotal);
        binding.mrptotal.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        binding.subtotal.setText (String.valueOf ("₹"+maintotal));
        binding.saveMrp.setText ("₹"+Discount);



        if(maintotal>500){
            Total=maintotal;
            binding.MaxTotal.setText(String.valueOf ("₹"+maintotal));
            binding.Del.setText ("FREE");
            binding.tax.setText ("FREE");
            binding.tax.setTextColor (getColor (R.color.teal_700));
            binding.Del.setTextColor (getColor (R.color.teal_700));
            binding.view.setText ("Congragulation You Got Free Delivery & Free Bag");
            binding.view.setTextColor (getColor (R.color.purple_500));
        }else {
            binding.total.setText ("₹"+Total);
            binding.MaxTotal.setText (String.valueOf ("₹"+Total));
            binding.view.setText("Add More ₹" +more+ " For Get Free Delivery & Bag");
            binding.view.setTextColor (getColor (R.color.red));
        }


        FirebaseUtil.coupon ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    coupon = task.getResult ( ).toObject (Coupon.class);
                    if (coupon!= null) {


                        couponInput = findViewById (R.id.nameBox);
                        String match = couponInput.getText ( ).toString ( );

                        String coupouns= coupon.getCode ().toUpperCase ();
                        couponInput.setText (coupouns);

                              binding.button5.setOnClickListener (new View.OnClickListener ( ) {
                                  @Override
                                  public void onClick(View v) {
                                      couponTarget=coupon.getTarget ();
                                      if(maintotal>couponTarget) {
                                          int couponDiscount = 0;
                                          couponDiscount = coupon.getValue ( );
                                          binding.button5.setText ("Applied");
                                          binding.nameBox.setTextColor (getColor (R.color.teal_700));
                                          Total = Total - couponDiscount;
                                          binding.MaxTotal.setText ("₹" + Total);
                                          binding.warn.setVisibility (View.GONE);
                                          binding.button5.setEnabled (false);
                                      }else {
                                          binding.textView3.setText ("Please add morethan ₹" +couponTarget+" of Order value");
                                          binding.warn.setVisibility (View.VISIBLE);
                                      }
                                  }
                              });


                    }
                }
            }
        });
    }



    void getProduct(TextView rateview){
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        binding.cartList.setLayoutManager(new LinearLayoutManager (this));

        List<Product> products=productDao.getallproduct();

        TextView quantity = null;

        mycheckadapter adapter=new mycheckadapter (products, rateview,quantity);
        binding.cartList.setAdapter(adapter);

        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());



    }
    void processOrder(String orderNumber){

        ProgressDialog progressBar=new ProgressDialog (this);
        progressBar.setMessage ("Order processing... ");
        progressBar.show ();

//        Handler handler=new Handler (  );
//        handler.postDelayed (()->{
//
//        },500);

        SharedPreferences sp=getSharedPreferences("location",MODE_PRIVATE);
        if(sp.contains("latitude")) {
           String lat=sp.getString ("latitude","");
           String lon=sp.getString ("longitude","");

           OrderPlaceModel orderPlaceModel = new OrderPlaceModel (orderNumber, uid, userModel.getUsername ( ), userModel.getPhone ( ), userModel.getAddress ( ), branch.getStorename ( ), String.valueOf (Total), String.valueOf (del), Timestamp.now ( ), lat, lon, "Pending", "cod","pending");
           FirebaseFirestore.getInstance ( )
                .collection ("orders")
                .document (orderNumber)
                .set (orderPlaceModel).addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                       @Override
                       public void onSuccess(Void unused) {
                           Toast.makeText (NewCheckoutActivity.this, "Order Succes", Toast.LENGTH_SHORT).show ( );
                       }
                   });

            databaseReference= FirebaseDatabase.getInstance ().getReference ("partner/"+orderNumber);
            databaseReference.child ("partnerName").setValue ("We will assign a partner soon ! ");
            databaseReference.child ("partnerContact").setValue (" Please be patient !");
            databaseReference.child ("partnerId").setValue ("pending");
            databaseReference.child ("partnerLat").setValue(lat);
            databaseReference.child ("partnerLon").setValue(lon);
            databaseReference.child ("storeLat").setValue (branch.getStoreLat ());
            databaseReference.child ("storeLon").setValue (branch.getStoreLon ());

        }








        AppDatabase db = Room.databaseBuilder (getApplicationContext ( ),
                AppDatabase.class, "cart_db").allowMainThreadQueries ( ).build ( );
        ProductDao productDao = db.ProductDao ( );

        List<Product> products = productDao.getallproduct ( );
            for (int i = 0; i < products.size ( ); i++) {
                Product cart = products.get (i);
                cart.setPid (Integer.parseInt (orderNumber));
                FirebaseFirestore.getInstance ( )
                        .collection ("OrderProduct")
                        .document ( )
                        .set (cart)
                        .addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                            @Override
                            public void onSuccess(Void unused) {
                                progressBar.cancel();
                                Intent intent = new Intent (NewCheckoutActivity.this, DeliveryDetailActivity.class);
                                intent.putExtra ("orderNumber", orderNumber);
                                intent.putExtra ("totalPrice", String.valueOf (Total));
                                intent.putExtra ("address", userModel.getAddress ( ));
                                intent.putExtra ("name", userModel.getUsername ( ));
                                intent.putExtra ("storeLat", branch.getStoreLat ());
                                intent.putExtra ("storeLon", branch.getStoreLon ());
                                intent.putExtra ("payment", "Cash on delivery");
                                intent.putExtra ("date", Timestamp.now ( ));
                                startActivity (intent);
                            }
                        }).addOnFailureListener (new OnFailureListener ( ) {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setMessage ("Order failed");
                                progressBar.show ();
                            }
                        });








        };
    }




    public static int getRandomNumber(int min, int max) {
        return (new Random ( )).nextInt ((max - min) + 1) + min;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish ( );
        return super.onSupportNavigateUp ( );
    }


    private void bottomsheet() {

        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog ( this);
        View view= LayoutInflater.from (NewCheckoutActivity.this).inflate (R.layout.branchsheet,(LinearLayout)findViewById (R.id.cartbtn),false);
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
                Intent intent = new Intent (NewCheckoutActivity.this,NewCheckoutActivity.class);
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