package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.flinkmart.mahi.adapter.FilterAdapter;
import com.flinkmart.mahi.adapter.FilterAdapter2;
import com.flinkmart.mahi.databinding.ActivityAllProductBinding;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.CartActivity;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.myadapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllProductActivity extends AppCompatActivity {
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


                startActivity(new Intent (getApplicationContext (), CartActivity.class));
//                    bottomSheet ();
            }
        });

        binding.imageButton3.setVisibility (View.GONE);
        binding.continues.setText ("Checkout");

        TextView Subtotal=findViewById (R.id.Subtotal);
        TextView text=findViewById (R.id.text);
        TextView banner=findViewById (R.id.congrage);


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

        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());

        int qty = 0;
        for (i = 0; i < products.size ( ); i++)
            qty = qty + (products.get (i).getQnt ( ));

        if (qty>1){
            quantity.setText (""+qty+" ITEMS | "+"₹"+sum);
        }else {
            quantity.setText (""+qty+" ITEM | "+"₹"+sum);
        }
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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fav) {
            startActivity(new Intent (this, FavouriteActivity.class));
        }
        else if (item.getItemId() == R.id.cart) {
            startActivity(new Intent (this, CartActivity.class));        }
        else if (item.getItemId() == R.id.searchicon) {
            startActivity(new Intent (this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void bottomSheet() {
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog ( this);
        View view= LayoutInflater.from (AllProductActivity.this).inflate (R.layout.bottomsheet,(LinearLayout)findViewById (R.id.mainlayout),false);
        bottomSheetDialog.setContentView (view);
        bottomSheetDialog.show ();

        RecyclerView cart=view.findViewById (R.id.cartList);

        Button check=view.findViewById (R.id.continueBtn);
        CardView cardView=view.findViewById (R.id.bottomPrice);
        TextView Subtotal=view.findViewById (R.id.subtotal);
        TextView quantity=view.findViewById (R.id.qty);
        TextView banner=view.findViewById (R.id.congrage);
        TextView text=view.findViewById (R.id.empty);
        Button shop=view.findViewById (R.id.shop);


        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        List<Product> products=productDao.getallproduct ();


        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());
        total=500-sum;

        if(sum<500){
            banner.setText("Add More ₹" +total+ " For Get Free Delivery & Free Bag");
            banner.setTextColor (getColor (R.color.red));
        }else {
            banner.setText("Congragulation You Got Free Delivery & Free Bag");
            banner.setTextColor (getColor (R.color.purple_500));
        }



        if(products.size ()==0){
            text.setVisibility (View.VISIBLE);
            check.setVisibility (View.GONE);
            cardView.setVisibility (View.GONE);
            shop.setVisibility (View.VISIBLE);
            banner.setVisibility (View.GONE);

            shop.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getApplicationContext (), MainActivity.class);
                    startActivity(i);
                }
            });

        }else {

        }

        check.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext (), NewCheckoutActivity.class);
                startActivity(i);
            }
        });



        getAllProduct(Subtotal,quantity,cart,text,banner,cardView);
    }

    private void getAllProduct(TextView Subtotal, TextView quantity, RecyclerView cart, TextView text, TextView banner,CardView cardView){
        onResume ();
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        cart.setLayoutManager(new LinearLayoutManager (this));
        List<Product> products=productDao.getallproduct();
        myadapter adapter=new myadapter(products, Subtotal,quantity, text,banner,cardView);
        cart.setAdapter(adapter);


        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());


        Subtotal.setText("₹"+sum);
        int qty = 0;
        for (i = 0; i < products.size ( ); i++)
            qty = qty + (products.get (i).getQnt ( ));
        quantity.setText (""+qty+" Items");

    }


}

