package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flinkmart.mahi.adapter.ImageAdapter;
import com.flinkmart.mahi.adapter.OrderDetailAdapter;
import com.flinkmart.mahi.databinding.ActivityDeliveryDetailBinding;
import com.flinkmart.mahi.model.ImageModel;
import com.flinkmart.mahi.model.OrderDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class DeliveryDetailActivity extends AppCompatActivity {

    ActivityDeliveryDetailBinding binding;
    ImageAdapter productadaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding= ActivityDeliveryDetailBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

        int orderNumber= Integer.parseInt (getIntent ().getStringExtra ("orderNumber"));
        String delivery="₹30";
        String Totalprice=getIntent ().getStringExtra ("totalPrice");
        String address=getIntent ().getStringExtra ("address");
        String time=getIntent ().getStringExtra ("date");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getProduct (orderNumber);
        binding.total.setText ("₹"+Totalprice);
        binding.address.setText (address);
        binding.delivery.setText (delivery);
        binding.orderid.setText ("#"+orderNumber);
        binding.packege.setText ("₹5");
        binding.button2.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (DeliveryDetailActivity.this, MainActivity.class);
                startActivity (intent);
                finish();
            }
        });

    }
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent (DeliveryDetailActivity.this, MainActivity.class);
        startActivity (intent);
        finish();
        return super.onSupportNavigateUp();
    }

    void getProduct(int orderNumber){
        getAllProduct (orderNumber);
        productadaper=new ImageAdapter (this)  ;
        binding.orderDetail.setAdapter (productadaper);
        binding.orderDetail.setLayoutManager (new LinearLayoutManager (getApplicationContext (),LinearLayoutManager.HORIZONTAL,false));

    }
    private void getAllProduct(int orderNumber){
        FirebaseFirestore.getInstance ()
                .collection ("OrderProduct")
               .whereEqualTo ("pid",orderNumber)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            ImageModel product=ds.toObject (ImageModel.class);
                            productadaper.addProduct(product);
                        }


                    }
                });
    }
    public void onBackPressed() {
        Intent intent = new Intent (DeliveryDetailActivity.this, MainActivity.class);
        startActivity (intent);

    }
}