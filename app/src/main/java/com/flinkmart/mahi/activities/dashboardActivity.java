//package com.flinkmart.mahi.activities;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.bumptech.glide.Glide;
//import com.denzcoskun.imageslider.constants.ScaleTypes;
//import com.denzcoskun.imageslider.models.SlideModel;
//import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
//import com.flinkmart.mahi.activitylogin.ProfileActivity;
//import com.flinkmart.mahi.branchAdapter.BranchGrocceryActivity;
//import com.flinkmart.mahi.databinding.ActivityDashboardBinding;
//import com.flinkmart.mahi.model.UserModel1;
//import com.flinkmart.mahi.utils.Constants;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.firestore.DocumentSnapshot;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class dashboardActivity extends AppCompatActivity {
//
//
//    ActivityDashboardBinding binding;
//
//    UserModel1 userModel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate (savedInstanceState);
//        binding = ActivityDashboardBinding.inflate (getLayoutInflater ( ));
//        setContentView (binding.getRoot ( ));
//
//        delivery();
//        groccery ();
//        restuarent ();
//        fashion ();
//        bookLibrary ();
//        getUsername ();
//        banner ();
//
//        binding.imageView4.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent (getApplicationContext (), ProfileActivity.class));
//            }
//        });
//
//        binding.groccery.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                    Intent i = new Intent (getApplicationContext ( ), BranchGrocceryActivity.class);
//                    i.putExtra ("storecate","groccery");
//                    startActivity (i);
//            }
//        });
//        binding.restuarant.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//
//
//
//
//                                    Intent intent = new Intent (getApplicationContext ( ), BranchActivity.class);
//                                    intent.putExtra ("storecate","restuarant");
//                                    startActivity (intent);
//
//
//
//
//
//
//
//            }
//        });
//
//        binding.fashion.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent (getApplicationContext ( ), BranchActivity.class);
//                i.putExtra ("storecate","fashion");
//                startActivity (i);
//            }
//        });
//
//        binding.bookLibrary.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent (getApplicationContext ( ), BranchActivity.class);
//                i.putExtra ("storecate","bookLibrary");
//                startActivity (i);
//            }
//        });
//
//        binding.searchBar.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent (getApplicationContext (), DashboardSearchActivity.class);
//                startActivity (intent);
//            }
//        });
//
//
//        binding.pick.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent (getApplicationContext (), PickupActivity.class);
//                startActivity (intent);
//            }
//        });
//
//    }
//
//    private  void banner() {
//        final List<SlideModel> imageList=new ArrayList<> ();
//        FirebaseDatabase.getInstance ().getReference ().child ("banner").addListenerForSingleValueEvent (new ValueEventListener ( ) {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                for (DataSnapshot ds : snapshot.getChildren ( )) {
//                    imageList.add (new SlideModel (ds.child ("image").getValue (  ).toString (), ds.child ("tittle").getValue (  ).toString (), ScaleTypes.FIT));
//                    binding.carousel.setImageList (imageList,ScaleTypes.FIT);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                RequestQueue queue = Volley.newRequestQueue (getApplicationContext ());
//                StringRequest request = new StringRequest (Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
//                    try {
//                        JSONObject object = new JSONObject (response);
//                        if (object.getString ("status").equals ("success")) {
//                            JSONArray offerArray = object.getJSONArray ("news_infos");
//                            for (int i = 0; i < offerArray.length ( ); i++) {
//                                JSONObject childObj = offerArray.getJSONObject (i);
//                                imageList.add (new SlideModel (Constants.NEWS_IMAGE_URL + childObj.getString ("image"), "", ScaleTypes.FIT));
//                                binding.carousel.setImageList (imageList,ScaleTypes.FIT);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace ( );
//                    }
//                }, ( error1) -> {
//                });
//                queue.add (request);
//            }
//        });
//
//
//
//
//    }
//    void  getUsername(){
//        FirebaseUtil.currentUserDetails ().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful ())  {
//
//                    userModel=  task.getResult ().toObject (UserModel1.class);
//                    if(userModel!=null){
//                        binding.textView4.setText ("Hi "+userModel.getUsername ());
//                        binding.textView11.setText (userModel.getAddress ());
//
//                    }
//                }
//            }
//        });
//    }
//
//    private void delivery() {
//        DatabaseReference databaseReference;
//        databaseReference= FirebaseDatabase.getInstance ().getReference ("delivery");
//
//        databaseReference.addListenerForSingleValueEvent (new ValueEventListener ( ) {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value=snapshot.child ("image").getValue (String.class);
////                    binding.imageView3.setImageURI (value);
//                Glide.with (getApplicationContext ()).load(value)
//                        .into (binding.imageView3);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//
//    private void restuarent(){
//
//        DatabaseReference databaseReference;
//        databaseReference= FirebaseDatabase.getInstance ().getReference ("dashboard");
//
//        databaseReference.addListenerForSingleValueEvent (new ValueEventListener ( ) {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value=snapshot.child ("food").getValue (String.class);
////                    binding.imageView3.setImageURI (value);
//                Glide.with (getApplicationContext ()).load(value)
//                        .into (binding.restuarant);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    private void groccery(){
//
//        DatabaseReference databaseReference;
//        databaseReference= FirebaseDatabase.getInstance ().getReference ("dashboard");
//
//        databaseReference.addListenerForSingleValueEvent (new ValueEventListener ( ) {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value=snapshot.child ("groccery").getValue (String.class);
////                    binding.imageView3.setImageURI (value);
//                Glide.with (getApplicationContext ()).load(value)
//                        .into (binding.groccery);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    private void fashion(){
//        DatabaseReference databaseReference;
//        databaseReference= FirebaseDatabase.getInstance ().getReference ("dashboard");
//
//        databaseReference.addListenerForSingleValueEvent (new ValueEventListener ( ) {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value=snapshot.child ("fashion").getValue (String.class);
////                    binding.imageView3.setImageURI (value);
//                Glide.with (getApplicationContext ()).load(value)
//                        .into (binding.fashion);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void bookLibrary(){
//        DatabaseReference databaseReference;
//        databaseReference= FirebaseDatabase.getInstance ().getReference ("dashboard");
//
//        databaseReference.addListenerForSingleValueEvent (new ValueEventListener ( ) {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value=snapshot.child (
//                        "bookstall").getValue (String.class);
////                    binding.imageView3.setImageURI (value);
//                Glide.with (getApplicationContext ()).load(value)
//                        .into (binding.bookLibrary);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//    public void onBackPressed() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder (dashboardActivity.this);
//        alertDialog.setTitle ("Exit alert!");
//        alertDialog.setMessage ("Do you want to exit?");
//
//        alertDialog.setPositiveButton ("Yes", new DialogInterface.OnClickListener ( ) {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                finish ();
//            }
//        });
//
//        alertDialog.setNeutralButton ("No", new DialogInterface.OnClickListener ( ) {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss ( );
//            }
//        });
//        alertDialog.show ( );
//    }
//}