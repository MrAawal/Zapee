package com.flinkmart.mahi.activities;

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
import com.flinkmart.mahi.adapter.CartAdapter;
import com.flinkmart.mahi.databinding.ActivityNewCartBinding;
import com.flinkmart.mahi.model.CartModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class NewCartActivity extends AppCompatActivity {
    ActivityNewCartBinding binding;

    CartAdapter productadaper;
    public static List<CartModel>cartList;
    TextView Subtotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding= ActivityNewCartBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

        Subtotal=findViewById (R.id.subtotal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getProduct ();

        cartList=productadaper.getSelectedItems();

        binding.subtotal.setText (String.valueOf (""+cartList.size ()));


        binding.continueBtn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
                cartList=productadaper.getSelectedItems();
                Toast.makeText (NewCartActivity.this, cartList.size ()+"Items Selected", Toast.LENGTH_SHORT).show ( );
                startActivity (new Intent ( NewCartActivity.this,NewCheckoutActivity.class));
            }
        });
    }

    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.cart, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId() == R.id.cart) {
//            startActivity(new Intent (this, NewCartActivity.class));
//        } else if (item.getItemId() == R.id.fav) {
//            startActivity(new Intent (this, FavouriteActivity.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }


    void getProduct(){
        getAllProduct ();
        productadaper=new CartAdapter (this,Subtotal)  ;
        binding.cartList.setAdapter (productadaper);
        binding.cartList.setLayoutManager (new GridLayoutManager (this,1));
//        binding.subtotal.setText  (cartItemList.size ()+"ITEMS SELECTED");
    }
    private void getAllProduct(){
        String uid= FirebaseAuth.getInstance( ).getUid( );
        FirebaseFirestore.getInstance ()
                .collection ("cart")
                .whereEqualTo ("uid",uid)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            CartModel product=ds.toObject (CartModel.class);
                            productadaper.addProduct(product);
                        }


                    }
                });
    }

}