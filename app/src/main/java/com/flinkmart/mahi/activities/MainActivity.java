package com.flinkmart.mahi.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.CatlistAdapter;
import com.flinkmart.mahi.adapter.HorizonProductAdapter;
import com.flinkmart.mahi.adapter.NewProductAdapter;
import com.flinkmart.mahi.databinding.ActivityMainBinding;
import com.flinkmart.mahi.model.Category;
import com.flinkmart.mahi.model.Catlist;
import com.flinkmart.mahi.model.HorizonProductModel;
import com.flinkmart.mahi.model.NewProductModel;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MaterialSearchBar.OnSearchActionListener {
    MaterialSearchBar searchBar;
    private DrawerLayout drawer;
    FirebaseAuth auth;
    FirebaseUser user;
    Context context;
    TextView HeaderAddress;
    TextView Add, HeaderName;
    ActivityMainBinding binding;
    NewProductAdapter newProductAdapter;
    HorizonProductAdapter horizonProductAdapter;
    UserModel userModel;
    CatlistAdapter catlistAdapter;
    ArrayList<Category> categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityMainBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));
        auth = FirebaseAuth.getInstance ( );
        user = auth.getCurrentUser ( );

        if (user == null) {
            //do nothing
        } else {
            getAddress ( );
        }

        ///internet Connection-----------------------------------------------------------------------
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
        ///internet Connection----------------------------------------------------------------------

        HeaderAddress = findViewById (R.id.headerAddress);
        HeaderName = findViewById (R.id.headerAddress);
        Add = findViewById (R.id.add);
        initOffer( );
        initOffer1( );
        initCategories ( );
        initSlider ( );

        auth = FirebaseAuth.getInstance ( );
        user = auth.getCurrentUser ( );


        drawer = findViewById (R.id.drawer_layout);
        NavigationView navigationView = findViewById (R.id.nav_view);
        navigationView.setNavigationItemSelectedListener (this);
        searchBar = findViewById (R.id.searchBar);
        searchBar.setOnSearchActionListener (this);


        Log.d ("LOG_TAG", getClass ( ).getSimpleName ( ) + ": text " + searchBar.getText ( ));
        searchBar.setCardViewElevation (10);
        searchBar.addTextChangeListener (new TextWatcher ( ) {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d ("LOG_TAG", getClass ( ).getSimpleName ( ) + " text changed " + searchBar.getText ( ));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });


    }

void initOffer(){
    getOffer();
    horizonProductAdapter=new HorizonProductAdapter (this)  ;
    LinearLayoutManager layoutManager = new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL,false);
    binding.offerList.setLayoutManager (layoutManager);
    binding.offerList.setAdapter (horizonProductAdapter);

}
void getOffer(){
    FirebaseFirestore.getInstance ()
            .collection ("product")
          .whereEqualTo ("stock","10")
            .get ()
            .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                    for (DocumentSnapshot ds:dsList){
                        HorizonProductModel product=ds.toObject (HorizonProductModel.class);
                        horizonProductAdapter.addProduct(product);
                    }


                }
            });
}

    void initOffer1(){
        getOffer1();
        newProductAdapter=new NewProductAdapter(this)  ;
        LinearLayoutManager layoutManager = new GridLayoutManager (this, 2);
        binding.offerList2.setLayoutManager (layoutManager);
        binding.offerList2.setAdapter (newProductAdapter);
    }
    void getOffer1(){
        FirebaseFirestore.getInstance ()
                .collection ("product")
                .whereEqualTo ("stock","100")
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            NewProductModel product=ds.toObject (NewProductModel.class);
                            newProductAdapter.addProduct(product);
                        }


                    }
                });
    }





    void getAddress() {

        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    userModel = task.getResult ( ).toObject (UserModel.class);
                    if (userModel != null) {
                        Add.setText ( userModel.getAddress ( ));
                    }
                }
            }
        });

    }



    void initCategories() {
        categories = new ArrayList<> ( );
        getCategories ( );

        catlistAdapter = new CatlistAdapter (this);
        binding.categoriesList.setAdapter (catlistAdapter);
        binding.categoriesList.setLayoutManager (new GridLayoutManager (this, 3));
    }

    void getCategories() {
        FirebaseFirestore.getInstance ( )
                .collection ("category")
//                .whereEqualTo ("pincode",pin)
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            Catlist catlist = ds.toObject (Catlist.class);
                            catlistAdapter.addProduct (catlist);
                        }

                    }
                });

    }

    private void initSlider() {
        getRecentOffers ( );
    }

    void getRecentOffers() {
        RequestQueue queue = Volley.newRequestQueue (this);

        StringRequest request = new StringRequest (Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray offerArray = object.getJSONArray ("news_infos");
                    for (int i = 0; i < offerArray.length ( ); i++) {
                        JSONObject childObj = offerArray.getJSONObject (i);
                        binding.carousel.addData (
                                new CarouselItem (
                                        Constants.NEWS_IMAGE_URL + childObj.getString ("image"),
                                        childObj.getString ("title")
                                )
                        );
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });
        queue.add (request);
    }




     @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById (R.id.drawer_layout);
        if (drawer.isDrawerOpen (GravityCompat.START)) {
            drawer.closeDrawer (GravityCompat.START);
        } else {
            super.onBackPressed ( );
        }
    }
//Drawer Layout Close Alert---------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //Drawer Menu ----------------------------------------------------------------------------------

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId ( );

        if (id == R.id.nav_camera) {
            Intent i = new Intent (getApplicationContext ( ), ProfileActivity.class);
            startActivity (i);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent (getApplicationContext ( ), OrdersActivity.class);
            startActivity (intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent (getApplicationContext ( ), NewCartActivity.class);
            startActivity (intent);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent (getApplicationContext ( ), PrivacyActivity.class);
            startActivity (intent);
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent (getApplicationContext ( ), DeleteAccountActivity.class);
            startActivity (intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById (R.id.drawer_layout);
        drawer.closeDrawer (GravityCompat.START);
        return true;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Intent intent = new Intent (MainActivity.this, SearchActivity.class);
        intent.putExtra ("query", text.toString ( ));
        startActivity (intent);
    }

    //Drawer Menu ----------------------------------------------------------------------------------

    //Search And Menu button -----------------------------------------------------------------------

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                drawer.openDrawer (GravityCompat.START);
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                break;
            case MaterialSearchBar.BUTTON_BACK:
                searchBar.closeSearch ( );
                break;
        }
    }



    //Search And Menu button -----------------------------------------------------------------------


    //Internet Connection---------------------------------------------------------------------------
    private boolean isConnected(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getApplicationContext ().getSystemService (context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo ()!=null && connectivityManager.getActiveNetworkInfo ().isConnectedOrConnecting ();
    }
    //Internet Connection---------------------------------------------------------------------------
}