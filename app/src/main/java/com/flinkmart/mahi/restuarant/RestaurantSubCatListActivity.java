package com.flinkmart.mahi.restuarant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.activities.NewCheckoutActivity;
import com.flinkmart.mahi.activities.SearchActivity;
import com.flinkmart.mahi.adapter.CatListAdapter;
import com.flinkmart.mahi.adapter.FilterAdapter2;
import com.flinkmart.mahi.databinding.ActivitySubCatListBinding;
import com.flinkmart.mahi.dynamicrv.DynamicRvAdapter;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Catlist;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.CartActivity;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.myadapter;
import com.flinkmart.mahi.roomdatabase.myadapter2;
import com.flinkmart.mahi.scrab.FavouriteActivity;
import com.flinkmart.mahi.scrapadaper.CartAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSubCatListActivity extends AppCompatActivity {
    ActivitySubCatListBinding binding;

    RecyclerView recyclerView;
    int total=0;

    CartAdapter cartAdapter;
    List<CartModel>cartList=new ArrayList<> ();

    FilterAdapter2 filterAdapter;
    ArrayList<Item>itemList;
    ArrayList<Catlist> categoryItems;

    DynamicRvAdapter dynamicRvAdapter;

    CatListAdapter categoryListAdapter;





    String uid= FirebaseAuth.getInstance ( ).getUid ( );


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate (savedInstanceState);
            binding = ActivitySubCatListBinding.inflate (getLayoutInflater ( ));
            setContentView (binding.getRoot ( ));

            binding.progressBar5.setVisibility (View.VISIBLE);

            TextView quantity=findViewById (R.id.cartQnt);
            TextView Subtotal=findViewById (R.id.Subtotal);
            TextView text=findViewById (R.id.text);
            TextView banner=findViewById (R.id.congrage);
            CardView layout=findViewById (R.id.cartlayout);


            String categoryId=getIntent().getStringExtra("category");
            String categoryName=getIntent().getStringExtra("categoryName");
            recyclerView=findViewById (R.id.productList);




            initProduct (categoryId,quantity,layout);
            initCat(categoryId);

            getSupportActionBar().setTitle(categoryName.toUpperCase (  ));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "cart_db").allowMainThreadQueries().build();
            ProductDao productDao = db.ProductDao();

            List<Product> products=productDao.getallproduct ();

            binding.catList.setLayoutManager (new LinearLayoutManager (this));
            myadapter2 adapter=new myadapter2(products,quantity,Subtotal, text, banner);
            binding.cartList.setAdapter(adapter);


            int sum=0,i;
            for(i=0;i< products.size();i++)
                sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());

            Subtotal.setText("₹"+sum);

            int qty = 0;
            for (i = 0; i < products.size ( ); i++)
                qty = qty + (products.get (i).getQnt ( ));


            if (qty>1){
                quantity.setText (""+qty+" ITEMS IN CART |"+"₹"+sum);
            }else {
                quantity.setText (""+qty+" ITEM IN CART |"+"₹"+sum);
            }
            if(products.size ()==0){
                binding.cartlayout.setVisibility (View.GONE);
            }
            binding.continues.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent (getApplicationContext (), RestaurantCartActivity.class));

//                    bottomSheet ();
                }
            });

            binding.imageButton3.setVisibility (View.GONE);
            binding.continues.setText ("Go to cart");

        }

    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    void initProduct(String category,TextView quantity, CardView layout){
          getProduct (category);
          itemList = new ArrayList<> ();

        LinearLayoutManager layoutManager = new GridLayoutManager (this, 2);
        binding.productList.setLayoutManager (layoutManager);
        filterAdapter = new FilterAdapter2 (this,itemList,quantity,layout);
        binding.productList.setAdapter(filterAdapter);
    }
    void getProduct(String category){
        ProgressBar progressBar=new ProgressBar (this);
        progressBar.setVisibility (View.VISIBLE);

        FirebaseFirestore.getInstance ()
                .collection ("product")
                .whereEqualTo ("show",true)
                .whereEqualTo ("category", category)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            binding.progressBar13.setVisibility (View.INVISIBLE);
                            Item product=ds.toObject (Item.class);
                            filterAdapter.addProduct(product);
                        }


                    }
                });

    }
    void initCat(String categoryId){
        getCat (categoryId);
        categoryItems = new ArrayList<> ();
        itemList=new ArrayList<> ();

        TextView quantity=findViewById (R.id.cartQnt);
        RecyclerView recyclerView1=findViewById (R.id.productList);
        CardView layout=findViewById (R.id.cartlayout);
        ProgressBar progressBar=new ProgressBar (this);

        LinearLayoutManager layoutManager = new GridLayoutManager (this, 2);
        binding.catList.setLayoutManager (layoutManager);
        categoryListAdapter = new CatListAdapter (this,categoryItems,recyclerView1,filterAdapter,itemList,quantity,layout);
        binding.catList.setAdapter(categoryListAdapter);
    }
    void getCat(String categoryId){
        ProgressBar progressBar=new ProgressBar (this);
        progressBar.setVisibility (View.VISIBLE);
        FirebaseFirestore.getInstance ()
                .collection ("subcategory")
                .whereEqualTo ("catname", categoryId)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            binding.progressBar13.setVisibility (View.INVISIBLE);
                            binding.progressBar5.setVisibility (View.INVISIBLE);
                            Catlist product=ds.toObject (Catlist.class);
                            categoryListAdapter.addProduct(product);

                        }


                    }
                });

    }
    protected void onResume() {
        super.onResume();

    }




    }
