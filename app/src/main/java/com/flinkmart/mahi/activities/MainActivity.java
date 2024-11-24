package com.flinkmart.mahi.activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.Fragment.CategoryFragment;
import com.flinkmart.mahi.Fragment.HomeFragment;
import com.flinkmart.mahi.Fragment.OrdersFragment;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ActivityMainBinding;
import com.flinkmart.mahi.map.MapActivity;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.model.UserModel1;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.CartActivity;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.myadapter2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class MainActivity extends AppCompatActivity {
   ActivityMainBinding binding;
   private int cartQantity=0;


   String[]permission={"android.permission.POST_NOTIFICATIONS"};

    UserModel userModel;
    FirebaseAuth auth;
    FirebaseUser user;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());



        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermissions (permission,34);
        }

        SharedPreferences sp=getSharedPreferences("location",MODE_PRIVATE);
        if(sp.contains("latitude"))
        {
//            Toast.makeText (this, "Sharing Current Location"+(sp.getString ("latitude","")), Toast.LENGTH_SHORT).show ( );
        } else {
            startActivity(new Intent (getApplicationContext (), MapActivity.class));
        }

        FirebaseMessaging.getInstance().subscribeToTopic("discount")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Done";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                    }
                });

//        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful ( )) {
//                    userModel = task.getResult ( ).toObject (UserModel.class);
//                    if (userModel != null) {
//                        //for manual input---------------------------
////                            binding.nameBox.setText (userModel.getUsername ( ));
//                    } else {
//                        Intent intent = new Intent (getApplicationContext () ,CompleteProfileActivity.class);
//                        startActivity (intent);
//                        finish ();
//                    }
//
//                }
//            }
//        });

        BottomNavigationView bottomNavigationView=findViewById (R.id.bottom_navigation);

        BadgeDrawable badgeDrawable=bottomNavigationView.getOrCreateBadge (R.id.orders);


        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        List<Product>products=productDao.getallproduct();

        if(products.size()==0){
            badgeDrawable.setVisible (false);
        }else{
            int qty = 0,i;
            for (i = 0; i < products.size ( ); i++)
                qty = qty + (products.get (i).getQnt( ));
            badgeDrawable.setVisible (true);
            badgeDrawable.setNumber (qty);


        }




//        binding.flot.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent (getApplicationContext (), CartActivity.class));
//            }
//        });


        if (!isConnected ( )) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder (MainActivity.this);
            alertDialog.setTitle ("No Internet Connected");
            alertDialog.setMessage ("Please Connect to Internet");
            alertDialog.setPositiveButton ("Ok", new DialogInterface.OnClickListener ( ) {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish ( );
                }
            });

            alertDialog.show ( );
        } else {
        }




        loadFragment(new HomeFragment ());

        binding.bottomNavigation.setOnItemSelectedListener (new NavigationBarView.OnItemSelectedListener ( ) {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id=item.getItemId ();

                if (id==R.id.home){
                    loadFragment(new HomeFragment ());
                    return true;
                } else if (id==R.id.orders) {
//                    loadFragment(new OrdersFragment ());
                    startActivity(new Intent (getApplicationContext (), CartActivity.class));
                    return true;

                }else if (id==R.id.category) {
                    loadFragment(new CategoryFragment());
                    return true;
                }
                return true;
            }
        });





    }



    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager ( );
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction ();
        fragmentTransaction.replace (R.id.container,fragment);
        fragmentTransaction.addToBackStack (null);
        fragmentTransaction.commit ();

    }
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.home, menu);
//
//        MenuItem menuItem=menu.findItem (R.id.cart);
//
//        View actionView=menuItem.getActionView ();
//
//        TextView quantity=actionView.findViewById (R.id.cart_badge_textview);
//
//        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
//                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
//        ProductDao productDao = db.ProductDao();
//
//        List<Product>products=productDao.getallproduct();
//
//        myadapter2 adapter=new myadapter2 (products,quantity,quantity,quantity,quantity);
//
//        if(products.size()==0){
//
//            quantity.setVisibility(View.GONE);
//
//        }else{
//            adapter.updateQuantity();
//            int qty = 0,i;
//            for (i = 0; i < products.size ( ); i++)
//                 qty = qty + (products.get (i).getQnt( ));
//
//            quantity.setText(""+qty);
//
//
//        }
//
//
//
//        actionView.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                onOptionsItemSelected (menuItem);
//            }
//        });
//
//
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId() == R.id.profile) {
////                profileSheet ();
//            startActivity(new Intent (this, ProfileActivity.class));
//        } else if (item.getItemId() == R.id.fav) {
//            startActivity(new Intent (this,LikeActivity.class));
//        }
//        else if (item.getItemId() == R.id.cart){
//            startActivity(new Intent (this, CartActivity.class));
//
////            bottomSheet ();
//        }
//        else if (item.getItemId() == R.id.searchicon) {
//            startActivity(new Intent (this, SearchActivity.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }



    private void profileSheet() {

        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog ( this);
        View view= LayoutInflater.from (MainActivity.this).inflate (R.layout.activity_profile,(LinearLayout)findViewById (R.id.mainlayout),false);
        bottomSheetDialog.setContentView (view);
        TextView name =view.findViewById (R.id.name);
        TextView contact = view.findViewById(R.id.contact);
        TextView  address = view.findViewById(R.id.address);
        TextView  email = view.findViewById(R.id.email);
        LinearLayout layout=findViewById (R.id.mainlayout);
        Button Logout=view.findViewById (R.id.logout);




        bottomSheetDialog.show ();


        auth=FirebaseAuth.getInstance ();
        user=auth.getCurrentUser ();
        if(user==null){
            name.setVisibility (View.GONE);
            address.setText ("Please Login!");
            email.setVisibility (View.GONE);
            contact.setVisibility (View.GONE);
            Logout.setText ("Login");
            Logout.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent (getApplication (), LoginActivity.class));
                }
            });

        }else {
              getCurrenuUser(name,address,contact,email);
        }


        Logout.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance ().signOut ();
                Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
                finish ();
            }
        });

    }

    private void getCurrenuUser(TextView name, TextView address, TextView contact, TextView email) {

        FirebaseUtil.currentUserDetails ().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful ())  {
                    userModel=  task.getResult ().toObject (UserModel.class);
                    if(userModel!=null){
                        name.setText (userModel.getUsername ().toUpperCase ());
                        contact.setText (userModel.getPhone ());
                        email.setText (user.getEmail ().toUpperCase ());
                        address.setText (userModel.getAddress ().toUpperCase ());
                    }
                }
            }
        });

    }


    private boolean isConnected(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getApplicationContext ().getSystemService (getApplicationContext ().CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo ()!=null && connectivityManager.getActiveNetworkInfo ().isConnectedOrConnecting ();
    }
    public void onBackPressed(){
        super.onBackPressed ( );
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder (MainActivity.this);
        alertDialog.setTitle ("Exit");
        alertDialog.setMessage ("Do you want to exit");

        alertDialog.setPositiveButton ("No", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish ( );
            }
        });
        alertDialog.setNegativeButton ("Yes", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ( );
                finish ( );
            }
        });

    }



}