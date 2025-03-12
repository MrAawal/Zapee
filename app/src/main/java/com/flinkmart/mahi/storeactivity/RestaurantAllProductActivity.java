package com.flinkmart.mahi.storeactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.FilterAdapter2;
import com.flinkmart.mahi.databinding.ActivityAllProductBinding;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAllProductActivity extends AppCompatActivity {
    ActivityAllProductBinding binding;
    FilterAdapter2 filterAdapter;
    List<Item>itemList;
    int total=0;

    public static List<CartModel>cartList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityAllProductBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));
        binding.progressBar3.setVisibility (View.VISIBLE);

        String CategoryId = getIntent().getStringExtra("catId");
        String CategoryName = getIntent().getStringExtra("catName");


        getSupportActionBar().setTitle(CategoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        TextView quantity=findViewById (R.id.cartQnt);

        List<Product> products=productDao.getallproduct ();



        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());



        int qty = 0;
        for (i = 0; i < products.size ( ); i++)
            qty = qty + (products.get (i).getQnt ( ));


        if (qty>1){
            quantity.setText (""+qty+" ITEMS |"+"₹"+sum);
        }else {
            quantity.setText (""+qty+" ITEM |"+"₹"+sum);
        }
        if(products.size ()==0){
            binding.cartlayout.setVisibility (View.GONE);
        }


        binding.continues.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (getApplicationContext (), RestaurantCartActivity.class));
            }
        });

        binding.imageButton3.setVisibility (View.GONE);
        binding.continues.setText ("Checkout");



        CardView layout=findViewById (R.id.cartlayout);
        initProduct (CategoryId,quantity,layout);
    }




    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    void initProduct(String CategoryId,TextView quantity, CardView layout){
        getProduct(CategoryId);
        itemList = new ArrayList<> ();
        LinearLayoutManager layoutManager = new GridLayoutManager (this, 2);
        binding.productList.setLayoutManager (layoutManager);
        filterAdapter = new FilterAdapter2 (this ,itemList,quantity,layout);
        binding.productList.setAdapter(filterAdapter);
    }
    void getProduct(String CategoryId){
        FirebaseFirestore.getInstance ()
                .collection ("product")
                .whereEqualTo ("show",true)
                .whereEqualTo ("subcategory",CategoryId)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            binding.progressBar3.setVisibility (View.GONE);
                            Item product=ds.toObject (Item.class);
                            filterAdapter.addProduct(product);
                        }

                    }
                });
    }



}

