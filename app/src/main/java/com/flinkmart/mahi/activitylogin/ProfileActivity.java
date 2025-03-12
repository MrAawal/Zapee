package com.flinkmart.mahi.activitylogin;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.flinkmart.mahi.activities.DeleteAccountActivity;
import com.flinkmart.mahi.activities.DeliveryDetailActivity;
import com.flinkmart.mahi.activities.OrdersActivity;
import com.flinkmart.mahi.activities.PrivacyActivity;
import com.flinkmart.mahi.adapter.OrdersProfileAdapter;
import com.flinkmart.mahi.databinding.ActivityProfileBinding;
import com.flinkmart.mahi.model.Order;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.storeactivity.RestaurantCheckoutActivity;
import com.google.android.datatransport.backend.cct.BuildConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import okhttp3.internal.Version;

public class ProfileActivity extends AppCompatActivity {
       TextView titleName,profileEmail, profileContact,profileAddress;
       FirebaseAuth auth;
       Button create;
       FirebaseUser user;
       Button logout;
       UserModel userModel;
       OrdersProfileAdapter OrdersAdapter;

       ActivityProfileBinding binding;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding=ActivityProfileBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

            binding.textView47.setText ("Version 5.1");

            binding.textView21.setVisibility (View.GONE);

            Handler handler=new Handler (  );
            handler.postDelayed (()->{
                binding.textView21.setVisibility (View.VISIBLE);
                binding.progressBar.setVisibility (View.GONE);
            },2000);


        titleName = findViewById (R.id.name);
        profileContact = findViewById(R.id.contact);
        profileEmail=findViewById(R.id.email);
        profileAddress=findViewById (R.id.address);

        binding.cont.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                contactSheet();
            }
        });

        if(profileEmail==null){
            profileEmail.setVisibility (View.GONE);
        }
        TextView text=findViewById (R.id.textView21);
            String uid=FirebaseAuth.getInstance ( ).getUid ( );

        logout = findViewById (R.id.logout);

        auth=FirebaseAuth.getInstance ();
        user=auth.getCurrentUser();


        if(user==null) {
            Intent i = new Intent (getApplicationContext ( ), LoginActivity.class);
            startActivity (i);
            finish ( );
        }else{

            getUsername();
            getProduct (uid,text);

        }
        binding.textView25.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), PrivacyActivity.class);
                startActivity(i);
            }
        });
        binding.textView24.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), DeleteAccountActivity.class);
                startActivity(i);
            }
        });
        binding.textView26.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent=new Intent ( Intent.ACTION_SEND );
                    intent.setType ("text/plain");
                    intent.putExtra (Intent.EXTRA_SUBJECT,"Zapee");
                    String shareMassage="https://play.google.com/store/apps/details?id=com.flinkmart.mahi";
                    intent.putExtra (Intent.EXTRA_TEXT,shareMassage);
                    startActivity (Intent.createChooser (intent,"share by"));
                }catch (Exception e){
                    Toast.makeText (ProfileActivity.this, ""+e, Toast.LENGTH_SHORT).show ( );
                }
            }
        });
        binding.button8.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), OrdersActivity.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view){
          new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("logout !")
                        .setCancelable(false)
                        .setMessage("Do you want to logout ?")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                             dialogInterface.dismiss ();
                            }
                        }).setNeutralButton ("Yes", new DialogInterface.OnClickListener ( ) {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance ().signOut ();
                                Intent intent=new Intent(getApplicationContext(),PhoneLoginActivity.class);
                                startActivity(intent);
                                finish ();
                            }
                        }).show();

            }
        });
        binding.imageView2.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getApplicationContext ( ), CompleteProfileActivity.class);
                startActivity (i);
//                bottomSheet();3e4gf
            }
        });


//        create.setOnClickListener (new View.OnClickListener ( ) {
//       @Override
//       public void onClick(View view) {
//        Intent intent = new Intent(ProfileActivity.this, CompleteProfileActivity.class);
//
//        intent.putExtra("email", user.getEmail ());
//        intent.putExtra("contact", userModel.getPhone ());
//        intent.putExtra("name", userModel.getUsername ());
//        intent.putExtra("name", userModel.getAddress ());
//
//        startActivity(intent);
//        }
//      });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      }

    private void contactSheet() {
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog ( this);
        View view= LayoutInflater.from (ProfileActivity.this).inflate (R.layout.address,(LinearLayout)findViewById (R.id.mainLayout),false);
        bottomSheetDialog.setContentView (view);
        EditText address;
        TextView congrag;
        congrag = view.findViewById (R.id.congrage);
        address = view.findViewById (R.id.address);
        Button update=view.findViewById (R.id.button10);

        DatabaseReference databaseReference= FirebaseDatabase.getInstance ().getReference ("address");
        databaseReference.addValueEventListener (new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value1=snapshot.child ("zapeeaddress").getValue (String.class);

                congrag.setText ("Email:info@zapee.in    "+value1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        address.setVisibility (View.GONE);

        update.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel ();
            }
        });
        bottomSheetDialog.show ();

    }

    private void bottomSheet() {

        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog ( this);
        View view= LayoutInflater.from (ProfileActivity.this).inflate (R.layout.profile,(LinearLayout)findViewById (R.id.mainLayout),false);
        bottomSheetDialog.setContentView (view);

        EditText address;

        address = view.findViewById (R.id.address);


        Button update=view.findViewById (R.id.button10);
        update.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                String addresss = address.getText ( ).toString ( );

                FirebaseFirestore.getInstance ()
                        .collection ("users")
                        .document ( auth.getUid ())
                        .update("address",addresss)
                        .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful ()){
                                    Toast.makeText (ProfileActivity.this, "Update will reflect soon", Toast.LENGTH_SHORT).show ( );
                                    bottomSheetDialog.cancel ();
                                }
                            }
                        })
                        ;

            }
        });




        bottomSheetDialog.show ();


    }



    void  getUsername(){
        FirebaseUtil.currentUserDetails ().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful ())  {

                    userModel=  task.getResult ().toObject (UserModel.class);
                    if(userModel!=null){
                        binding.contact.setText (userModel.getPhone ());
                        binding.name.setText (userModel.getUsername ());
                        binding.address.setText (userModel.getAddress ()+"-"+userModel.getPin ());
                        String emailid=FirebaseAuth.getInstance ().getCurrentUser ().getEmail ();
                        if(emailid!=null){
                            binding.email.setText (emailid);
                        }



                    }
                }
            }
        });
    }

    void getProduct(String uid, TextView text){
        getAllProduct (uid);
        OrdersAdapter=new OrdersProfileAdapter(this,text);
        binding.orderList.setAdapter (OrdersAdapter);
        binding.orderList.setLayoutManager (new GridLayoutManager (this,1));
    }
    private void getAllProduct(String uid){
        FirebaseFirestore.getInstance ()
                .collection ("orders")
                .whereEqualTo ("uid",uid)
                .limit (1)
                .orderBy ("orderPlaceDate", Query.Direction.DESCENDING)
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            binding.progressBar.setVisibility (View.GONE);
                            Order product=ds.toObject (Order.class);
                                OrdersAdapter.addProduct(product);

                        }


                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}