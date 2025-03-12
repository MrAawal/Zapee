package com.flinkmart.mahi.scrab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activitylogin.LoginActivity;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.activitylogin.PhoneLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilePhoneActivity extends AppCompatActivity {
    TextView profileName, profileContact;
    TextView titleName;
    String nameFromDB,addressFromDB,contactFromDB;
    FirebaseAuth auth;
    Button create;
    FirebaseUser user;
    Button logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_profile);
        profileName = findViewById(R.id.name);
        profileContact = findViewById(R.id.contact);
        logout = findViewById (R.id.logout);


        showAllUserData();

        auth=FirebaseAuth.getInstance ();
        user=auth.getCurrentUser();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(user==null){
            Intent i=new Intent(getApplicationContext(), PhoneLoginActivity.class);
            startActivity(i);
            finish ();
        }else {
            profileContact.setText (user.getPhoneNumber ());
        }
        logout.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance ().signOut ();
                Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish ();
            }
        });
    }

    public void showAllUserData(){

        Intent intent = getIntent();
        String nameUser = intent.getStringExtra("name");
        String contactUser = FirebaseAuth.getInstance ().getCurrentUser ().getPhoneNumber ();
        profileName.setText(nameUser);
        profileContact.setText(contactUser);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void onBackPressed() {
        Intent intent = new Intent(ProfilePhoneActivity.this, MainActivity.class);
        intent.putExtra("name", nameFromDB);
        startActivity(intent);
    }


}