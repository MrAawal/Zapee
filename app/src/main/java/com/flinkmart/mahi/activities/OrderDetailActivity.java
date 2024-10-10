package com.flinkmart.mahi.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.flinkmart.mahi.adapter.OrderDetailAdapter;
import com.flinkmart.mahi.adapter.OrdersAdapter;
import com.flinkmart.mahi.databinding.ActivityOrderDetailBinding;
import com.flinkmart.mahi.databinding.ActivityOrdersBinding;
import com.flinkmart.mahi.model.Order;
import com.flinkmart.mahi.model.OrderDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    ActivityOrderDetailBinding binding;
    OrderDetailAdapter productadaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding= ActivityOrderDetailBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

        String orderNumber= getIntent ().getStringExtra ("orderNumber");
        String delivery="30";
        String Totalprice=getIntent ().getStringExtra ("totalPrice");
        String address=getIntent ().getStringExtra ("address");
        String time=getIntent ().getStringExtra ("date");
        String status=getIntent ().getStringExtra ("status");
        String charge= "35";

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getProduct (Integer.parseInt (orderNumber));
        binding.total.setText (Totalprice);
        binding.address.setText (address);
        binding.date.setText (time);
        binding.delivery.setText (delivery);
        binding.orderid.setText ("#"+orderNumber);
        binding.packege.setText ("5");
        binding.status.setText (status);
    }
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getProduct(int orderNumber){
        getAllProduct (orderNumber);
        productadaper=new OrderDetailAdapter (this)  ;
        binding.orderDetail.setAdapter (productadaper);
        binding.orderDetail.setLayoutManager (new GridLayoutManager (this,1));

    }
    private void getAllProduct(int orderNumber){
        FirebaseFirestore.getInstance ()
                .collection ("OrderProduct")
               .whereEqualTo ("orderid",orderNumber)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            OrderDetails product=ds.toObject (OrderDetails.class);
                            productadaper.addProduct(product);
                        }


                    }
                });
    }
}