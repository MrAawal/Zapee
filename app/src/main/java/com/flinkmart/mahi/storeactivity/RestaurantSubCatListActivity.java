package com.flinkmart.mahi.storeactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.flinkmart.mahi.roomdatabase.myadapter2;
import com.flinkmart.mahi.scrab.FavouriteActivity;
import com.flinkmart.mahi.scrapadaper.CartAdapter;
import com.flinkmart.mahi.storeadapter.StoreHomeProductAdapter;
import com.flinkmart.mahi.storeadapter.StoreSubCategoryAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSubCatListActivity extends AppCompatActivity {
    ActivitySubCatListBinding binding;
    RecyclerView recyclerView;
    StoreHomeProductAdapter filterAdapter;
    ArrayList<Item>itemList;
    ArrayList<Catlist> categoryItems;
    StoreSubCategoryAdapter categoryListAdapter;



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


    void initProduct(String category,TextView quantity, CardView layout){
        getProduct (category);
        itemList = new ArrayList<> ();

        LinearLayoutManager layoutManager = new GridLayoutManager (this, 2);
        binding.productList.setLayoutManager (layoutManager);
        filterAdapter = new StoreHomeProductAdapter (this,itemList,quantity,layout);
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
        categoryListAdapter = new StoreSubCategoryAdapter (this,categoryItems,recyclerView1,filterAdapter,itemList,quantity,layout);
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



    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category, menu);
        MenuItem searchItem=menu.findItem (R.id.searchicon);
        MenuItem cart=menu.findItem (R.id.cart);
        View actionView=cart.getActionView ();

        TextView quantity=actionView.findViewById (R.id.cart_badge_textview);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        List<Product> products=productDao.getallproduct ();



        if(products.size ()==0){
            quantity.setVisibility (View.GONE);
        }

        int qty = 0,i;
        for (i = 0; i < products.size ( ); i++)
            qty = qty + (products.get (i).getQnt ( ));
        quantity.setText (""+qty);
        actionView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected (cart);
            }
        });


        SearchView searchView= (SearchView) searchItem.getActionView ();
        searchView.setMaxWidth (Integer.MAX_VALUE);
        searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener ( ){

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }




    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fav){
            startActivity(new Intent (this, FavouriteActivity.class));
        }
        else if (item.getItemId() == R.id.cart) {
            startActivity(new Intent (this, RestaurantCartActivity.class));
        }
        else if (item.getItemId() == R.id.searchicon) {
            startActivity(new Intent (this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}
