package com.flinkmart.mahi.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flinkmart.mahi.adapter.FragmentCategoryListAdapter;
import com.flinkmart.mahi.databinding.FragmentCategoryBinding;
import com.flinkmart.mahi.model.Catlist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    FragmentCategoryListAdapter catlistAdapter;
    List<Catlist>catlists= new ArrayList<> ();
    FragmentCategoryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setUpRecyclerView ();
        initCategory ();
        return  view;
    }

    private void initCategory() {
        FirebaseFirestore.getInstance ( )
                .collection ("subcategory")
                .whereEqualTo ("show",true)
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            Catlist catlist = ds.toObject (Catlist.class);
                            catlistAdapter.addProduct(catlist);
                        }

                    }
                });

    }
    private void setUpRecyclerView() {
        //category
        binding.category.setLayoutManager (new GridLayoutManager (getActivity (),3));
        catlistAdapter=new FragmentCategoryListAdapter (getContext (),catlists);
        binding.category.setAdapter (catlistAdapter);

    }
}