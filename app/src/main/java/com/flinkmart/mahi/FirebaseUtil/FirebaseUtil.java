package com.flinkmart.mahi.FirebaseUtil;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.flinkmart.mahi.activities.CheckoutActivity;
import com.flinkmart.mahi.activities.CompleteProfileActivity;
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

    public static DocumentReference addToCart(){
        return FirebaseFirestore.getInstance ( ).collection ("user cart").document (currentUserId ());
    }



    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

}

