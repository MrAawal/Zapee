package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.SearchAdapter;
import com.flinkmart.mahi.adapter.SubCategoryItemAdapter;
import com.flinkmart.mahi.databinding.ActivitySearchBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.model.Item;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    SearchAdapter adapter;
    ActivitySearchBinding binding;
    SubCategoryItemAdapter subCategoryListAdapter;
    List<Item>itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySearchBinding.inflate (getLayoutInflater () );
        setContentView (binding.getRoot ());
        itemList=new ArrayList<> (  );
//        initProduct ();
        binding.SearchInput.requestFocus();
       binding.SearchInput.setOnQueryTextListener (new SearchView.OnQueryTextListener ( ) {
           @Override
           public boolean onQueryTextSubmit(String query) {
               String searchTerm = binding.SearchInput.getQuery ().toString();
               if(searchTerm.isEmpty() || searchTerm.length()<1){
               }
               setupSearchRecyclerView(searchTerm);
               return true;
           }

           @Override
           public boolean onQueryTextChange(String text) {
               String searchTerm = binding.SearchInput.getQuery ( ).toString ( );
               if (searchTerm.isEmpty ( ) || searchTerm.length ( ) < 1) {
               }
               setupSearchRecyclerView (searchTerm);
               return true;
           }
       });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    void initProduct(){
        getProduct();
        subCategoryListAdapter=new SubCategoryItemAdapter (this)  ;
        LinearLayoutManager layoutManager = new GridLayoutManager (this, 2);
        binding.SearchList.setLayoutManager (layoutManager);
        binding.SearchList.setAdapter (subCategoryListAdapter);
    }
    void getProduct(){
        FirebaseFirestore.getInstance ()
                .collection ("product")
//                .whereEqualTo ("category",category)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            Item product=ds.toObject (Item.class);
                            subCategoryListAdapter.addProduct(product);
                        }


                    }
                });
    }


    void setupSearchRecyclerView(String searchTerm){

        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("tittle",searchTerm.toLowerCase ())
                .whereLessThanOrEqualTo("tittle",searchTerm.toLowerCase ()+'\uf8ff');
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query,Item.class).build();

        adapter = new SearchAdapter (options,getApplicationContext(),itemList);
        binding.SearchList.setLayoutManager(new GridLayoutManager (this,1));
        binding.SearchList.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent (SearchActivity.this, MainActivity.class);
        startActivity (intent);
        finish();
        return super.onSupportNavigateUp();
    }
    public void onBackPressed() {
        super.onBackPressed ( );
        Intent intent = new Intent (SearchActivity.this, MainActivity.class);
        startActivity (intent);
        finish ( );
    }
}