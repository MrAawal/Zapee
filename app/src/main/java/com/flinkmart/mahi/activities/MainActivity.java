package com.flinkmart.mahi.activities;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.Fragment.CategoryFragment;
import com.flinkmart.mahi.Fragment.HomeFragment;
import com.flinkmart.mahi.Fragment.OrdersFragment;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.CartAdapter;
import com.flinkmart.mahi.adapter.ItemAdapter;
import com.flinkmart.mahi.databinding.ActivityMainBinding;
import com.flinkmart.mahi.model.CartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

   ActivityMainBinding binding;
   private int cartQantity=0;

    CartAdapter cartAdapter;
    public static List<CartModel>cartList;


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
//            Toast.makeText (MainActivity.this, "Wellcome To ZAPEE", Toast.LENGTH_SHORT).show ( );
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.profile) {
            startActivity(new Intent (this, ProfileActivity.class));
        } else if (item.getItemId() == R.id.fav) {
            startActivity(new Intent (this, FavouriteActivity.class));
        }
        else if (item.getItemId() == R.id.cart) {
            bottomSheet ();
        }
        else if (item.getItemId() == R.id.searchicon) {
            startActivity(new Intent (this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean isConnected(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getApplicationContext ().getSystemService (getApplicationContext ().CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo ()!=null && connectivityManager.getActiveNetworkInfo ().isConnectedOrConnecting ();
    }

    private void bottomSheet() {
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog ( this);
        View view= LayoutInflater.from (MainActivity.this).inflate (R.layout.bottomsheet,(LinearLayout)findViewById (R.id.mainlayout),false);
        bottomSheetDialog.setContentView (view);
        bottomSheetDialog.show ();

        TextView Subtotal=view.findViewById (R.id.subtotal);;
        RecyclerView cart=view.findViewById (R.id.cartList);

        Button check=view.findViewById (R.id.checkout);

        if(cartList==null){
            check.setVisibility (View.INVISIBLE);
        }

        cartAdapter=new CartAdapter (this,Subtotal);


        cart.setAdapter (cartAdapter);

        cart.setLayoutManager (new GridLayoutManager (this,1));
        getAllProduct ();
        cartList=cartAdapter.getSelectedItems();
        check.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                cartList=cartAdapter.getSelectedItems();
                Intent intent = new Intent(getApplication (), NewCartActivity.class);
                startActivity (intent);
                bottomSheetDialog.cancel ();

            }
        });




    }

    private void getAllProduct(){
        String uid= FirebaseAuth.getInstance( ).getUid( );
        FirebaseFirestore.getInstance ()
                .collection ("cart")
                .whereEqualTo ("uid",uid)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            CartModel product=ds.toObject (CartModel.class);
                            cartAdapter.addProduct(product);
                        }


                    }
                });
    }

}