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
    TextView profileName, profileAddress, profileContact;
    TextView titleName;
    FirebaseAuth auth;
    Button create;
    FirebaseUser user;
    Button logout;
    UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_profile);

        titleName = findViewById (R.id.titleName);
        profileName = findViewById(R.id.Name);
        profileAddress = findViewById(R.id.Addres);
        profileContact = findViewById(R.id.Contact);
        create = findViewById(R.id.complete);
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
                        titleName.setText (userModel.getUsername ());
                        profileName.setText(user.getEmail());
                        profileContact.setText (userModel.getPhone ());
                        profileAddress.setText (userModel.getAddress ());
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