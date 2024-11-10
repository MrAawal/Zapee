//package com.flinkmart.mahi.activities;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.MenuItem;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//
//import com.flinkmart.mahi.R;
//import com.flinkmart.mahi.adapter.OrdersAdapter;
//import com.flinkmart.mahi.databinding.ActivityOrdersBinding;
//import com.flinkmart.mahi.databinding.ActivityOrdersFragmentBinding;
//import com.flinkmart.mahi.model.Order;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.navigation.NavigationBarView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.List;
//
//public class OrdersFrafmentActivity extends AppCompatActivity {
//    ActivityOrdersFragmentBinding binding;
//    OrdersAdapter productadaper;
//    BottomNavigationView bottomNavigationView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate (savedInstanceState);
//        binding= ActivityOrdersFragmentBinding.inflate (getLayoutInflater ());
//        setContentView (binding.getRoot ());
//
//        bottomNavigationView= findViewById (R.id.navigation_view);
//
//
//        bottomNavigationView.setOnItemSelectedListener (new NavigationBarView.OnItemSelectedListener ( ) {
//            @SuppressLint("NonConstantResourceId")
//            @Override
//            public boolean onNavigationItemSelected( MenuItem item) {
//                int itemId=item.getItemId ();
//                if (itemId==R.id.cart){
//                    Intent i = new Intent (getApplicationContext ( ), NewCartActivity.class);
//                    startActivity (i);
//
//                } else if (itemId==R.id.orders) {
//                    Intent i = new Intent (getApplicationContext ( ), OrdersFrafmentActivity.class);
//                    startActivity (i);
//                }else if (itemId==R.id.home) {
//                    Intent i = new Intent (getApplicationContext ( ), MainActivity.class);
//                    startActivity (i);
//                }
//                return false;
//            }
//        });
//        String uid=FirebaseAuth.getInstance ( ).getUid ( );
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getProduct (uid);
//    }
//    public boolean onSupportNavigateUp() {
//        finish();
//        return super.onSupportNavigateUp();
//    }
//
//    void getProduct(String uid){
//        getAllProduct (uid);
//        productadaper=new OrdersAdapter (this)  ;
//        binding.productList.setAdapter (productadaper);
//        binding.productList.setLayoutManager (new GridLayoutManager (this,1));
//    }
//    private void getAllProduct(String uid){
//        FirebaseFirestore.getInstance ()
//                .collection ("orders")
//                .whereEqualTo ("uid",uid)
////                .orderBy ("status", Query.Direction.ASCENDING)
////                .orderBy ("orderPlaceDate", Query.Direction.ASCENDING)
//                .get ()
//                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
//                        for (DocumentSnapshot ds:dsList){
//                            Order product=ds.toObject (Order.class);
//                            productadaper.addProduct(product);
//                        }
//
//
//                    }
//                });
//    }
//    }