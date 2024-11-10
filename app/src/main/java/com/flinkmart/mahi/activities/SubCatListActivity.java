package com.flinkmart.mahi.activities;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.CartAdapter;
import com.flinkmart.mahi.adapter.FilterAdapter;
import com.flinkmart.mahi.adapter.SublistAdapter;
import com.flinkmart.mahi.databinding.ActivitySubCatListBinding;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Catlist;
import com.flinkmart.mahi.model.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class SubCatListActivity extends AppCompatActivity {
    ActivitySubCatListBinding binding;

    RecyclerView recyclerView;

    CartAdapter cartAdapter;
    public static List<CartModel>cartList;

    SublistAdapter sublistAdapter;
    FilterAdapter filterAdapter;
    List<Item>itemList;


    String uid= FirebaseAuth.getInstance ( ).getUid ( );


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate (savedInstanceState);
            binding = ActivitySubCatListBinding.inflate (getLayoutInflater ( ));
            setContentView (binding.getRoot ( ));

            String category=getIntent().getStringExtra("category");
            recyclerView=findViewById (R.id.list);

            initcategory(category);

            initProduct (category);

            bottomSheet ();

            getSupportActionBar().setTitle(category);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void initcategory(String category){
        getSubCategory (category);
        itemList = new ArrayList<> ();
        sublistAdapter = new SublistAdapter (this,recyclerView);
        LinearLayoutManager layoutManager = new GridLayoutManager (this, 1);
        binding.catList.setAdapter (sublistAdapter);
        binding.catList.setLayoutManager (layoutManager);
    }

    void getSubCategory(String category) {
            FirebaseFirestore.getInstance ( )
                    .collection ("subcategory")
                    .whereEqualTo ("catname",category)
                    .get ( )
                    .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                            for (DocumentSnapshot ds : dsList) {
                                Catlist catlist = ds.toObject (Catlist.class);
                                sublistAdapter.addProduct (catlist);
                            }

                        }
                    }).addOnFailureListener (new OnFailureListener ( ) {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText (SubCatListActivity.this, "No cat Selected", Toast.LENGTH_SHORT).show ( );
                        }
                    });;
        }

    void initProduct(String category){
        getProduct(category);
        itemList = new ArrayList<> ();
        LinearLayoutManager layoutManager = new GridLayoutManager (this, 2);
        binding.productList.setLayoutManager (layoutManager);
        filterAdapter = new FilterAdapter (this ,itemList);
        binding.productList.setAdapter(filterAdapter);
    }
    void getProduct(String category){
        FirebaseFirestore.getInstance ()
                .collection ("product")
                .whereEqualTo ("category", category)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            Item product=ds.toObject (Item.class);
                            filterAdapter.addProduct(product);
                        }


                    }
                });

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category, menu);
        MenuItem searchItem=menu.findItem (R.id.searchicon);
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

    private void bottomSheet() {
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog ( this);
        View view= LayoutInflater.from (SubCatListActivity.this).inflate (R.layout.bottomsheet,(LinearLayout)findViewById (R.id.mainlayout),false);
        bottomSheetDialog.setContentView (view);


        bottomSheetDialog.show ();

        TextView Subtotal=view.findViewById (R.id.subtotal);;
        RecyclerView cart=view.findViewById (R.id.cartList);

        Button check=view.findViewById (R.id.checkout);

        if(cartList==null){
            bottomSheetDialog.cancel ();
            check.setVisibility (View.INVISIBLE);
        }

        cartAdapter=new CartAdapter (this,Subtotal);

        cart.setAdapter (cartAdapter);

        cart.setLayoutManager (new GridLayoutManager (this,1));
        getAllProduct ();
        cartList=cartAdapter.getSelectedItems();
        check.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                cartList=cartAdapter.getSelectedItems();
                Toast.makeText (getApplication (), cartList.size ()+"Items Selected", Toast.LENGTH_SHORT).show ( );
                Intent intent = new Intent(getApplication (), NewCartActivity.class);
                startActivity (intent);
                bottomSheetDialog.cancel ();
            }
        });




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
                            cartAdapter.addProduct(product);
                        }


                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fav) {
            startActivity(new Intent (this, FavouriteActivity.class));
        }
        else if (item.getItemId() == R.id.cart) {
            bottomSheet ();
        }
        else if (item.getItemId() == R.id.searchicon) {
            startActivity(new Intent (this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }





    }
