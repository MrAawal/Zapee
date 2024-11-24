package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.flinkmart.mahi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    public static int SPLASH_TIMER=3000;
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        new Handler ().postDelayed(new Runnable() {
            @Override
            public void run() {
                auth= FirebaseAuth.getInstance ();
                user=auth.getCurrentUser();
                if(user!=null) {
                    Intent i = new Intent (getApplicationContext ( ), MainActivity.class);
                    startActivity (i);
                    finish ( );
                }else {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                }
            }
        }, SPLASH_TIMER); // Delay in milliseconds
    }
}