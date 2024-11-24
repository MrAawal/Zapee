package com.flinkmart.mahi.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.activities.ProfileActivity;
import com.flinkmart.mahi.activities.SearchActivity;
import com.flinkmart.mahi.adapter.FragmentCategoryListAdapter;
import com.flinkmart.mahi.databinding.FragmentCategoryBinding;
import com.flinkmart.mahi.model.Catlist;
import com.flinkmart.mahi.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    FragmentCategoryListAdapter catlistAdapter;
    List<Catlist>catlists= new ArrayList<> ();
    FragmentCategoryBinding binding;

    UserModel userModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();



        binding.shimmer.startShimmer ();
        binding.shimmer.setVisibility (View.VISIBLE);
        binding.home.setVisibility (View.INVISIBLE);

        binding.searchBar.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (getActivity (), SearchActivity.class));
            }
        });
        binding.imageView4.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (getActivity (), ProfileActivity.class));
            }
        });

        Handler handler=new Handler (  );
        handler.postDelayed (()->{



        },2500);

        getUsername ();
        setUpRecyclerView ();
        initCategory ();


        return  view;
    }

    private void initCategory() {
        FirebaseFirestore.getInstance ( )
                .collection ("subcategory")
                .whereEqualTo ("show",true)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {

                            binding.shimmer.stopShimmer ();
                            binding.home.setVisibility (View.VISIBLE);
                            binding.shimmer.setVisibility (View.INVISIBLE);

                            Catlist catlist = ds.toObject (Catlist.class);
                            catlistAdapter.addProduct(catlist);
                        }

                    }
                });

    }

    void  getUsername(){
        FirebaseUtil.currentUserDetails ().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful ())  {

                    userModel=  task.getResult ().toObject (UserModel.class);
                    if(userModel!=null){
                        binding.textView4.setText ("Hi "+userModel.getUsername ());
                        binding.textView11.setText (userModel.getAddress ());

                    }
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