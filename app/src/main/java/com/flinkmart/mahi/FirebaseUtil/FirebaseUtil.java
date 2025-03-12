package com.flinkmart.mahi.FirebaseUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class FirebaseUtil{
    public static  String currentUserId(){
        return FirebaseAuth.getInstance ().getCurrentUser ().getUid ();
    }


    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance ( ).collection ("users").document (currentUserId ());
    }

    public static DocumentReference currentUserStore(){
        return FirebaseFirestore.getInstance ( ).collection ("userStore").document (currentUserId());
    }
    public static DocumentReference currentUserRestaurant(){
        return FirebaseFirestore.getInstance ( ).collection ("userRestaurant").document (currentUserId());
    }


    public static DocumentReference coupon(){
        return FirebaseFirestore.getInstance ( ).collection("coupon").document ("tEgrv0T0cdcuclZrof9b");
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
    public static CollectionReference orders(String uid) {
        return FirebaseFirestore.getInstance ( ).collection ("orders");

    }
    public static CollectionReference favourite(String uid) {
        return FirebaseFirestore.getInstance ( ).collection ("favourite");

    }



}

