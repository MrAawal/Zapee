package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.flinkmart.mahi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;


public class PhoneLoginActivity extends AppCompatActivity {
        CountryCodePicker ccp;
        EditText t1;
        Button b1,EmailLogin;
        FirebaseAuth auth;
    FirebaseUser user;






        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_phone_login);

            t1=(EditText)findViewById(R.id.code);
            ccp=(CountryCodePicker)findViewById(R.id.ccp);
            ccp.registerCarrierNumberEditText(t1);
            b1=(Button)findViewById(R.id.send);
            EmailLogin=(Button) findViewById (R.id.EmailLogin);

            auth=FirebaseAuth.getInstance ();
            user=auth.getCurrentUser();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if(user!=null) {
                Intent i = new Intent (getApplicationContext ( ), ProfileActivity.class);
                startActivity (i);
                finish ( );
            }

            EmailLogin.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(PhoneLoginActivity.this,LoginActivity.class);
                    intent.putExtra("mobile",ccp.getFullNumberWithPlus().replace(" ",""));
                    startActivity(intent);

                }
            });


            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(PhoneLoginActivity.this,ManageOtpActivity.class);
                    intent.putExtra("mobile",ccp.getFullNumberWithPlus().replace(" ",""));
                    startActivity(intent);
                }
            });

        }
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    }