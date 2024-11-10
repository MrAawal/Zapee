package com.flinkmart.mahi.FirebaseUtil;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.flinkmart.mahi.activities.CheckoutActivity;
import com.flinkmart.mahi.activities.CompleteProfileActivity;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.UserModel1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;



public class FirebaseUtil{
    public static  String currentUserId(){
        return FirebaseAuth.getInstance ().getCurrentUser ().getUid ();
    }
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance ( ).collection ("users").document (currentUserId ());
    }
    public static DocumentReference currentUserStore(){
        return FirebaseFirestore.getInstance ( ).collection ("userstore").document (currentUserId ());
    }
    public static DocumentReference deleteUserDetail(){
        return FirebaseFirestore.getInstance ( ).collection ("Delete User").document (currentUserId ());
    }

    public static DocumentReference cartDetails(String uid){
        return FirebaseFirestore.getInstance ( )
                .collection ("cart")
                .document ( uid );
    }

    public static DocumentReference favdetail(String uid2){
        return FirebaseFirestore.getInstance ( )
                .collection ("favourite")
                .document ( uid2 );
    }

    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance ( ).collection ("product");
    }



}

