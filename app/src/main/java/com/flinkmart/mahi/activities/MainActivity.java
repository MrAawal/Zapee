package com.flinkmart.mahi.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.CartActivity;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.myadapter;
import com.flinkmart.mahi.roomdatabase.myadapter2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

   ActivityMainBinding binding;
   private int cartQantity=0;

    UserModel userModel;
    FirebaseAuth auth;
    FirebaseUser user;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

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
                    loadFragment(new OrdersFragment ());
                    return true;

                }else if (id==R.id.category) {
                    loadFragment(new CategoryFragment());
                    return true;
                }
                return false;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem menuItem=menu.findItem (R.id.cart);
        View actionView=menuItem.getActionView ();

        TextView quantity=actionView.findViewById (R.id.cart_badge_textview);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        List<Product> products=productDao.getallproduct ();

        myadapter2 adapter=new myadapter2 (products,quantity,quantity,quantity,quantity);

        if(products.size ()==0){
            quantity.setVisibility (View.GONE);
        }
        int qty = 0,i;
        for (i = 0; i < products.size ( ); i++)
            qty = qty + (products.get (i).getQnt ( ));

        quantity.setText (""+qty+" Items");



        actionView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected (menuItem);
            }
        });



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.profile) {
                profileSheet ();
        } else if (item.getItemId() == R.id.fav) {
            startActivity(new Intent (this,LikeActivity.class));
        }
        else if (item.getItemId() == R.id.cart){
            startActivity(new Intent (this, CartActivity.class));

//            bottomSheet ();
        }
        else if (item.getItemId() == R.id.searchicon) {
            startActivity(new Intent (this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }



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

}