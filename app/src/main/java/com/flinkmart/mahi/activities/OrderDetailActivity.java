package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.ImageAdapter;
import com.flinkmart.mahi.adapter.OrderDetailAdapter;
import com.flinkmart.mahi.databinding.ActivityOrderDetailBinding;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.Coupon;
import com.flinkmart.mahi.model.ImageModel;
import com.flinkmart.mahi.model.OrderDetails;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.CartActivity;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    ActivityOrderDetailBinding binding;
    ImageAdapter productadaper;

    Coupon coupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding= ActivityOrderDetailBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

        Integer orderNumber= Integer.valueOf (getIntent ().getStringExtra ("orderNumber"));

        String Totalprice=getIntent ().getStringExtra ("totalPrice");
        String address=getIntent ().getStringExtra ("address");
        String time=getIntent ().getStringExtra ("date");
        String status=getIntent ().getStringExtra ("status");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getProduct (orderNumber);
        binding.total.setText ("₹"+Totalprice);
        binding.address.setText (address);
        binding.date.setText (time);
        binding.orderid.setText ("#"+orderNumber);
        binding.status.setText (status);

        binding.imageButton2.setVisibility (View.GONE);
        if(status=="Pending"){
            binding.imageButton2.setVisibility (View.GONE);
        }
        if(status=="Delivered"){
            binding.imageButton2.setVisibility (View.GONE);
        }
        if(status=="Canceled"){
            binding.imageButton2.setVisibility (View.GONE);
        }
        binding.button6.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                binding.product.setVisibility (View.VISIBLE);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);


        MenuItem cart=menu.findItem (R.id.help);


        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.help) {
            FirebaseUtil.coupon ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful ( )) {
                        coupon = task.getResult ( ).toObject (Coupon.class);

                        String agent=coupon.getContact ();


                        if (coupon!= null) {
                            String url = "https://api.whatsapp.com/send?phone= " + agent;
                            try {
                                PackageManager pm = getApplicationContext().getPackageManager();
                                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            } catch (PackageManager.NameNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            }





                        } else {

                            Toast.makeText (OrderDetailActivity.this, "No Agent Active", Toast.LENGTH_SHORT).show ( );
                        }
                    }
                }
            });




        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getProduct(Integer orderNumber){
        getAllProduct (orderNumber);
        productadaper=new ImageAdapter (this)  ;
        binding.orderDetail.setAdapter (productadaper);
        binding.orderDetail.setLayoutManager (new GridLayoutManager (this,4));
    }
    private void getAllProduct(Integer orderNumber){
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
}