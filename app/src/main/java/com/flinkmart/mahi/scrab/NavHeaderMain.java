package com.flinkmart.mahi.scrab;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activitylogin.LoginActivity;
import com.flinkmart.mahi.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class NavHeaderMain extends AppCompatActivity {

    TextView titleName, profileAddress;
    FirebaseAuth auth;
    FirebaseUser user;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.nav_header_main);

        titleName = findViewById (R.id.headerName);
        profileAddress = findViewById(R.id.headerAddress);

        auth=FirebaseAuth.getInstance ();
        user=auth.getCurrentUser();



        if(user==null) {
            Intent i = new Intent (getApplicationContext ( ), LoginActivity.class);
            startActivity (i);
            finish ( );
        }else{
            getUsername();
        }

    }

    void  getUsername(){
        FirebaseUtil.currentUserDetails ().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful ())  {
                    userModel=  task.getResult ().toObject (UserModel.class);
                    if(userModel!=null){
                        titleName.setText (userModel.getUsername ());
                        profileAddress.setText (userModel.getAddress ());
                    }
                }
            }
        });
    }


}