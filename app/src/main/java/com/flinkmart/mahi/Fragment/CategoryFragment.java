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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.activities.ProfileActivity;
import com.flinkmart.mahi.activities.SearchActivity;
import com.flinkmart.mahi.scrapadaper.FragmentCategoryListAdapter;
import com.flinkmart.mahi.databinding.FragmentCategoryBinding;
import com.flinkmart.mahi.model.Catlist;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        binding.linearLayout4.setVisibility (View.INVISIBLE);

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
        banner();


        return  view;
    }

    private  void banner() {
        final List<SlideModel>imageList=new ArrayList<> ();
        FirebaseDatabase.getInstance ().getReference ().child ("banner").addListenerForSingleValueEvent (new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren ( )) {
                    imageList.add (new SlideModel (ds.child ("image").getValue (  ).toString (), ds.child ("tittle").getValue (  ).toString (), ScaleTypes.FIT));
                    binding.carousel.setImageList (imageList,ScaleTypes.FIT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                RequestQueue queue = Volley.newRequestQueue (getActivity ());
                StringRequest request = new StringRequest (Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
                    try {
                        JSONObject object = new JSONObject (response);
                        if (object.getString ("status").equals ("success")) {
                            JSONArray offerArray = object.getJSONArray ("news_infos");
                            for (int i = 0; i < offerArray.length ( ); i++) {
                                JSONObject childObj = offerArray.getJSONObject (i);
                                imageList.add (new SlideModel (Constants.NEWS_IMAGE_URL + childObj.getString ("image"), "", ScaleTypes.FIT));
                                binding.carousel.setImageList (imageList,ScaleTypes.FIT);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace ( );
                    }
                }, ( error1) -> {
                });
                queue.add (request);
            }
        });




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
                            binding.linearLayout4.setVisibility (View.VISIBLE);

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