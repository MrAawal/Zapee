package com.flinkmart.mahi.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ActivityCheckoutBinding;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.OrderPlaceModel;
import com.flinkmart.mahi.model.UserModel1;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.OrderProduct;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.ProductEntity;
import com.flinkmart.mahi.roomdatabase.myadapter;
import com.flinkmart.mahi.roomdatabase.myadapter2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheckoutActivity extends AppCompatActivity {

    ActivityCheckoutBinding binding;
    double totalPrice = 0;
    final int del = 30;
    final int gst = 5;
    private  String uid;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user;
    UserModel1 userModel;
    Branch branch;
    ArrayList<ProductEntity> product;
    TextView rate;
    TextView rateview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
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
                            Intent intent = new Intent (CheckoutActivity.this, CompleteProfileActivity.class);
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
                        Intent intent = new Intent (CheckoutActivity.this, CompleteProfileActivity.class);
                        startActivity (intent);
                    }
                }
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        product = new ArrayList<>();

        rateview=findViewById(R.id.subtotal);
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();


        List<ProductEntity> products=productDao.getallproduct();
        binding.cartList.setLayoutManager(new LinearLayoutManager (this));
        myadapter2 adapter=new myadapter2(products,rateview);
        binding.cartList.setAdapter(adapter);

        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum= (int) (sum+(products.get(i).getPrice()*products.get(i).getQnt()));

        binding.subtotal.setText("₹ "+sum);
        totalPrice = (gst+del+sum);
        uid=FirebaseAuth.getInstance().getUid();
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
                Intent intent = new Intent (CheckoutActivity.this, BranchActivity.class);
                intent.putExtra("pincode", userModel.getPin ());
                startActivity (intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    void processOrder(){
        String orderNumber= String.valueOf (getRandomNumber (11111,99999));
        OrderPlaceModel orderPlaceModel=new OrderPlaceModel(orderNumber,uid,userModel.getUsername (),userModel.getPhone (),userModel.getAddress (),branch.getStorename (),String.valueOf (totalPrice),String.valueOf (del),Timestamp.now(),null,null,"Pending","cod");
        FirebaseFirestore.getInstance ()
                .collection ("orders")
                .document (orderNumber)
                .set (orderPlaceModel);
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();
        List<ProductEntity> products=productDao.getallproduct();

        rate=findViewById (R.id.subtotal);
        binding.cartList.setLayoutManager(new LinearLayoutManager (this));
        myadapter2 adapter=new myadapter2(products, rate);
        binding.cartList.setAdapter(adapter);

        for(int i=0;i<products.size();i++){
            ProductEntity orderProduct = products.get(i);
            orderProduct.setPid (Integer.parseInt (orderNumber));
            orderProduct.setUid (uid);
            FirebaseFirestore.getInstance ( )
                    .collection ("OrderProduct")
                    .document ()
                    .set (orderProduct );
            Toast.makeText (this, "Order Id "+orderNumber, Toast.LENGTH_SHORT).show ( );
            Intent intent = new Intent (CheckoutActivity.this, OrdersActivity.class);
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