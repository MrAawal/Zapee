package com.flinkmart.mahi.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.flinkmart.mahi.R;
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

        binding.textView.setVisibility (View.INVISIBLE);

//        Handler handler=new Handler (  );
//        handler.postDelayed (()->{
//            binding.textView.setVisibility (View.VISIBLE);
//            binding.progressBar7.setVisibility (View.INVISIBLE);
//        },500);

        String uid=FirebaseAuth.getInstance ( ).getUid ( );
        TextView text=findViewById (R.id.textView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getProduct (uid,text);


    }
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getProduct(String uid, TextView text){
        getAllProduct (uid);
        productadaper=new OrdersAdapter (this,text)  ;
        binding.productList.setAdapter (productadaper);
        binding.productList.setLayoutManager (new GridLayoutManager (this,1));
    }
    private void getAllProduct(String uid){
        FirebaseFirestore.getInstance ()
                .collection ("orders")
                .whereEqualTo ("uid",uid)
                .orderBy ("orderPlaceDate", Query.Direction.DESCENDING)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            binding.progressBar7.setVisibility (View.INVISIBLE);
                            Order product=ds.toObject (Order.class);
                            productadaper.addProduct(product);
                        }


                    }
                });
    }
    }