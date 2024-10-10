package com.flinkmart.mahi.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.flinkmart.mahi.adapter.NewProductAdapter;
import com.flinkmart.mahi.databinding.ActivityAllProductBinding;
import com.flinkmart.mahi.model.NewProductModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.List;

public class AllProductActivity extends AppCompatActivity {
    ActivityAllProductBinding binding;
    NewProductAdapter productadaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding= ActivityAllProductBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());
        String category=getIntent().getStringExtra("category");
        getSupportActionBar().setTitle(category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getProduct (category);


    }
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getProduct(String category){
        getAllProduct (category);
        productadaper=new NewProductAdapter (this)  ;
        binding.productList.setAdapter (productadaper);
        binding.productList.setLayoutManager (new GridLayoutManager (this,2));
    }
    private void getAllProduct(String category){
        FirebaseFirestore.getInstance ()
                .collection ("product")
                .whereEqualTo ("subcategory",category)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            NewProductModel product=ds.toObject (NewProductModel.class);
                            productadaper.addProduct(product);
                        }


                    }
                });
    }

}