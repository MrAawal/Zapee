package com.flinkmart.mahi.branchAdapter;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.activities.ProfileActivity;
import com.flinkmart.mahi.databinding.ActivityBranchBinding;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.Fashion;
import com.flinkmart.mahi.model.Resturant;
import com.flinkmart.mahi.model.UserModel1;
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

public class BranchGrocceryActivity extends AppCompatActivity {
    ActivityBranchBinding binding;
    BranchAdapter branchAdapter;

    FashionAdapter fashionAdapter;
    RestuarantAdapter restuarantAdapter;
    private static  List<Branch> branchemodel;
    private static  List<Fashion> fashions;
    private static  List<Resturant> resturants;

    UserModel1 userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding=ActivityBranchBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

        TextView warning=findViewById (R.id.textView29);


        binding.linearLayout13.setVisibility (View.INVISIBLE);

        getUsername ();


        banner();

        binding.searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener ( ) {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                branchAdapter.getFilter ().filter (newText);
                return true;
            }
        });

        binding.imageView4.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (getApplicationContext (), ProfileActivity.class));
            }
        });

        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    userModel = task.getResult ( ).toObject (UserModel1.class);
                    if (userModel != null) {
//                        String storecate=getIntent ().getStringExtra ("storecate");
                        String pincode=userModel.getPin ();
                        getStore (pincode);
                    }

                }
            }
        });


//        SharedPreferences sp=getSharedPreferences("store",MODE_PRIVATE);
//        if(sp.contains("StoreId"))
//        {
//            Intent intent = new Intent (getApplicationContext (), MainActivity.class);
//            startActivity (intent);
//        }

        binding.textView29.setVisibility (View.INVISIBLE);
        Handler handler=new Handler (  );
        handler.postDelayed (()->{
           binding.textView29.setVisibility (View.VISIBLE);
           binding.progressBar8.setVisibility (View.INVISIBLE);
        },4000);

        branchAdapter=new BranchAdapter (this,warning) ;
        binding.branchList.setAdapter (branchAdapter);
        binding.branchList.setLayoutManager (new LinearLayoutManager (this));

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
                RequestQueue queue = Volley.newRequestQueue (getApplicationContext ());
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


    void  getStore( String pincode){
                FirebaseFirestore.getInstance ()
                .collection ("branch")
                .whereEqualTo ("pincode",pincode)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            binding.linearLayout13.setVisibility (View.VISIBLE);
                            binding.progressBar8.setVisibility (View.INVISIBLE);
                            Branch resturants=ds.toObject (Branch.class);
                            branchAdapter.addProduct (resturants);
                        }

                    }
                });
    }






    void  getUsername(){
        FirebaseUtil.currentUserDetails ().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful ())  {

                    userModel=  task.getResult ().toObject (UserModel1.class);
                    if(userModel!=null){
                        binding.textView4.setText ("Hi "+userModel.getUsername ());
                        binding.textView11.setText (userModel.getAddress ());

                    }
                }
            }
        });
    }


}