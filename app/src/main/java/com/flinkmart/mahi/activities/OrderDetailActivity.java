package com.flinkmart.mahi.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flinkmart.mahi.adapter.ImageAdapter;
import com.flinkmart.mahi.adapter.OrderDetailAdapter;
import com.flinkmart.mahi.databinding.ActivityOrderDetailBinding;
import com.flinkmart.mahi.model.ImageModel;
import com.flinkmart.mahi.model.OrderDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    ActivityOrderDetailBinding binding;
    ImageAdapter productadaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding= ActivityOrderDetailBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

        String orderNumber= getIntent ().getStringExtra ("orderNumber");
        String delivery="₹30";
        String Totalprice=getIntent ().getStringExtra ("totalPrice");
        String address=getIntent ().getStringExtra ("address");
        String time=getIntent ().getStringExtra ("date");
        String status=getIntent ().getStringExtra ("status");
        String charge= "35";

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getProduct (orderNumber);
        binding.total.setText ("₹"+Totalprice);
        binding.address.setText (address);
        binding.date.setText (time);
        binding.orderid.setText ("#"+orderNumber);
        binding.status.setText (status);
    }
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getProduct(String orderNumber){
        getAllProduct (orderNumber);
        productadaper=new ImageAdapter (this)  ;
        binding.orderDetail.setAdapter (productadaper);
        binding.orderDetail.setLayoutManager (new LinearLayoutManager (getApplicationContext (),LinearLayoutManager.HORIZONTAL,false));
    }
    private void getAllProduct(String orderNumber){
        FirebaseFirestore.getInstance ()
                .collection ("OrderProduct")
                .whereEqualTo ("orderid",orderNumber)
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
}