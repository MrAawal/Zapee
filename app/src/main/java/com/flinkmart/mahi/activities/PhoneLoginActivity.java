package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flinkmart.mahi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;


public class PhoneLoginActivity extends AppCompatActivity {
        CountryCodePicker ccp;
        EditText t1;
        Button b1;

        TextView EmailLogin;
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
            EmailLogin=(TextView) findViewById (R.id.EmailLogin);




            auth=FirebaseAuth.getInstance ();
            user=auth.getCurrentUser();

            if(user!=null){
                Intent i = new Intent (getApplicationContext ( ), MainActivity.class);
                startActivity (i);
                finish ( );
            }

            EmailLogin.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(PhoneLoginActivity.this,LoginActivity.class);
                    startActivity(intent);

                }
            });


            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone=t1.getText ().toString ();

                    if(phone.isEmpty ()||phone.length ()<10||phone.length ()>10){
                        t1.setError ("Phone number should be 10 digit");
                        return;
                    }else {
                        Intent intent=new Intent(PhoneLoginActivity.this,ManageOtpActivity.class);
                        intent.putExtra("mobile",ccp.getFullNumberWithPlus().replace(" ",""));
                        startActivity(intent);
                    }

                }
            });

        }
    }