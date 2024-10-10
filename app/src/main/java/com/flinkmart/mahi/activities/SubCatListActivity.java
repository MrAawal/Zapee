package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flinkmart.mahi.adapter.CatlistAdapter;
import com.flinkmart.mahi.adapter.SublistAdapter;
import com.flinkmart.mahi.databinding.ActivitySubCatListBinding;
import com.flinkmart.mahi.model.Catlist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class SubCatListActivity extends AppCompatActivity {
        ActivitySubCatListBinding binding;
        SublistAdapter branchAdapter;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate (savedInstanceState);
            binding = ActivitySubCatListBinding.inflate (getLayoutInflater ( ));
            setContentView (binding.getRoot ( ));
            String category=getIntent().getStringExtra("category");

            getSupportActionBar().setTitle(category);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getStore (category);
            branchAdapter = new SublistAdapter (this);
            binding.branchList.setAdapter (branchAdapter);
            binding.branchList.setLayoutManager (new GridLayoutManager (this,3));
        }
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

        void getStore(String category) {
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
                                branchAdapter.addProduct (catlist);
                            }

                        }
                    });
        }


    }
