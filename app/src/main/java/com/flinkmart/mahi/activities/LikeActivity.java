package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.CartAdapter;
import com.flinkmart.mahi.adapter.FavouriteAdapter;
import com.flinkmart.mahi.databinding.ActivityLikeBinding;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Favourite;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.myadapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LikeActivity extends AppCompatActivity {
    ActivityLikeBinding binding;
   FavouriteAdapter filterAdapter;

    List<Favourite> itemList;

    CartAdapter cartAdapter;


    public static List<CartModel>cartList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityLikeBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));
        String category = FirebaseAuth.getInstance ( ).getUid ( );
        getSupportActionBar ( ).setTitle ("Favourite");
        getSupportActionBar ( ).setDisplayHomeAsUpEnabled (true);
        initProduct (category);

    }




    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    void initProduct(String category){
        getProduct(category);
        itemList = new ArrayList<> ();
        TextView Subtotal=findViewById (R.id.textView18);
        LinearLayoutManager layoutManager = new GridLayoutManager (this, 2);
        binding.productList.setLayoutManager (layoutManager);
        filterAdapter = new FavouriteAdapter (this,Subtotal);
        binding.productList.setAdapter(filterAdapter);
    }
    void getProduct(String category){
        FirebaseFirestore.getInstance ()
                .collection ("favourite")
                .whereEqualTo ("uid", category)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            Favourite product=ds.toObject (Favourite.class);
                            filterAdapter.addProduct(product);
                        }


                    }
                });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category, menu);
//        MenuItem searchItem=menu.findItem (R.id.searchicon);
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

        quantity.setText (""+qty+" Items");
        actionView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected (cart);
            }
        });

//        SearchView searchView= (SearchView) searchItem.getActionView ();
//        searchView.setMaxWidth (Integer.MAX_VALUE);
//        searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener ( ){
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filterAdapter.getFilter().filter(newText);
//                return true;
//            }
//        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fav) {
            startActivity(new Intent (this, FavouriteActivity.class));
        }
        else if (item.getItemId() == R.id.cart) {
            bottomSheet();
        }
        else if (item.getItemId() == R.id.searchicon) {
            startActivity(new Intent (this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void bottomSheet() {
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog ( this);
        View view= LayoutInflater.from (LikeActivity.this).inflate (R.layout.bottomsheet,(LinearLayout)findViewById (R.id.mainlayout),false);
        bottomSheetDialog.setContentView (view);
        bottomSheetDialog.show ();

        TextView Subtotal=view.findViewById (R.id.subtotal);
        TextView quantity=view.findViewById (R.id.qty);
        RecyclerView cart=view.findViewById (R.id.cartList);
        Button check=view.findViewById (R.id.continueBtn);

        check.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext (), NewCheckoutActivity.class);
                startActivity(i);
            }
        });

        getAllProduct(Subtotal,quantity,cart);
    }

    private void getAllProduct(TextView Subtotal, TextView quantity, RecyclerView cart){
        onResume ();
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        cart.setLayoutManager(new LinearLayoutManager (this));

        List<Product> products=productDao.getallproduct();
//        myadapter adapter=new myadapter(products, Subtotal,quantity, text);
//        cart.setAdapter(adapter);
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