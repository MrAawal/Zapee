package com.flinkmart.mahi.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.flinkmart.mahi.adapter.OrdersAdapter;
import com.flinkmart.mahi.databinding.ActivityOrdersBinding;
import com.flinkmart.mahi.model.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    ActivityOrdersBinding binding;
    OrdersAdapter productadaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding= ActivityOrdersBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());
        String uid=FirebaseAuth.getInstance ( ).getUid ( );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getProduct (uid);
    }
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getProduct(String uid){
        getAllProduct (uid);
        productadaper=new OrdersAdapter (this)  ;
        binding.productList.setAdapter (productadaper);
        binding.productList.setLayoutManager (new GridLayoutManager (this,1));
    }
    private void getAllProduct(String uid){
        FirebaseFirestore.getInstance ()
                .collection ("orders")
                .whereEqualTo ("uid",uid)
                .orderBy ("orderPlaceDate", Query.Direction.ASCENDING)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            Order product=ds.toObject (Order.class);
                            productadaper.addProduct(product);
                        }


                    }
                });
    }
    }