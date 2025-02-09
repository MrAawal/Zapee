package com.flinkmart.mahi.cartfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.scrab.CheckoutActivity;
import com.flinkmart.mahi.scrab.NewCartActivity;
import com.flinkmart.mahi.databinding.ActivityFavouriteBinding;
import com.flinkmart.mahi.model.Favourite;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FavouriteActivity extends AppCompatActivity {
    ActivityFavouriteBinding binding;
    com.flinkmart.mahi.cartfirebase.FavouriteAdapter productadaper;
    public static List<Favourite>cartItemList;
    TextView Subtotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding= ActivityFavouriteBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

        Subtotal=findViewById (R.id.subtotal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getProduct ();

        cartItemList=productadaper.getSelectedItems();
        binding.subtotal.setText (String.valueOf (""+cartItemList.size ()));

        binding.check.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
                cartItemList=productadaper.getSelectedItems();
                Toast.makeText (FavouriteActivity.this, cartItemList.size ()+"Items Selected", Toast.LENGTH_SHORT).show ( );
                startActivity (new Intent ( FavouriteActivity.this, CheckoutActivity.class));
            }
        });
    }

    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
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
        productadaper=new FavouriteAdapter (this,Subtotal)  ;
        binding.productList.setAdapter (productadaper);
        binding.productList.setLayoutManager (new GridLayoutManager (this,1));
//        binding.subtotal.setText  (cartItemList.size ()+"ITEMS SELECTED");
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
                            productadaper.addProduct(product);
                        }


                    }
                });
    }

}