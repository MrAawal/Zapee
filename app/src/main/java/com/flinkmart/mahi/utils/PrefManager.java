package com.flinkmart.mahi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.flinkmart.mahi.model.UserModel;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PrefManager {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0 ;

    private static final String PREF_NAME="username";
    private static final String PREF_PHONE="phone";
    private static final String PREF_ADDRESS="address";
    private static final String TAG_USER_PROFILE="username";

     public PrefManager (Context context){
         this._context=context;
         preferences=_context.getSharedPreferences(TAG_USER_PROFILE,PRIVATE_MODE);
         editor=preferences.edit ();
     }

     public static Gson provideGson() {
         GsonBuilder gsonBuilder=new GsonBuilder ();
         return gsonBuilder
                 .setDateFormat ("yyyy-MM-dd'T'HH:mm:ss2")
                 .create ();

     }
     public void save(UserModel user){
         Gson gson=provideGson ();
         String json=gson.toJson (user);
         editor.putString (TAG_USER_PROFILE,json);
         editor.apply ();
     }
     public  UserModel get(Context context){
         Gson gson=provideGson ();
         String json=preferences.getString (TAG_USER_PROFILE,null);
         return gson.fromJson (json,UserModel.class);

     }

}
