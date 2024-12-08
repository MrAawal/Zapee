package com.flinkmart.mahi.activities;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flinkmart.mahi.databinding.ActivityProfileBinding;
import com.flinkmart.mahi.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {
       TextView titleName,profileEmail, profileContact,profileAddress;
       FirebaseAuth auth;
       Button create;
       FirebaseUser user;
       Button logout;
       UserModel userModel;
       ActivityProfileBinding binding;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding=ActivityProfileBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());



        titleName = findViewById (R.id.name);
        profileContact = findViewById(R.id.contact);
        profileEmail=findViewById(R.id.email);
        profileAddress=findViewById (R.id.address);

        logout = findViewById (R.id.logout);

        auth=FirebaseAuth.getInstance ();
        user=auth.getCurrentUser();


        if(user==null) {
            Intent i = new Intent (getApplicationContext ( ), LoginActivity.class);
            startActivity (i);
            finish ( );
        }else{


            getUsername();

        }
        logout.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance ().signOut ();
                Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
                finish ();
            }
        });
        create.setOnClickListener (new View.OnClickListener ( ) {
       @Override
       public void onClick(View view) {
        Intent intent = new Intent(ProfileActivity.this, CompleteProfileActivity.class);

        intent.putExtra("email", user.getEmail ());
        intent.putExtra("contact", userModel.getPhone ());
        intent.putExtra("name", userModel.getUsername ());
        intent.putExtra("name", userModel.getAddress ());

        startActivity(intent);
        }
      });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                        binding.address.setText (userModel.getAddress ());
                    }
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