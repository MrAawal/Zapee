package com.flinkmart.mahi.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.flinkmart.mahi.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AndroidUtil {


    public static  void  showToast(Context context,String message){
        Toast.makeText (context, message,Toast.LENGTH_SHORT).show ( );

    }

    public static  void passUserModelAsIntent(Intent intent,UserModel model){
        intent.putExtra ("username",model.getUsername ());
        intent.putExtra ("phone",model.getPhone ());
    }
    public  static  UserModel getUserModelFromIntent(Intent intent){

        UserModel userModel =new UserModel (  );
        userModel.setUsername (intent.getStringExtra ("username"));
        userModel.setPhone(intent.getStringExtra ("phone"));
        return userModel;
    }

}