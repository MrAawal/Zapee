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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.CategoryAdapter;
import com.flinkmart.mahi.adapter.ProductAdapter;
import com.flinkmart.mahi.adapter.ProductAdapter1;
import com.flinkmart.mahi.adapter.ProductAdapter2;
import com.flinkmart.mahi.adapter.ProductAdapter3;
import com.flinkmart.mahi.adapter.ProductAdapter4;
import com.flinkmart.mahi.databinding.ActivityMainBinding;
import com.flinkmart.mahi.model.Category;
import com.flinkmart.mahi.model.Product;
import com.flinkmart.mahi.model.Product1;
import com.flinkmart.mahi.model.Product2;
import com.flinkmart.mahi.model.Product3;
import com.flinkmart.mahi.model.Product4;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MaterialSearchBar.OnSearchActionListener {
    MaterialSearchBar searchBar;
    private DrawerLayout drawer;
    FirebaseAuth auth;
    FirebaseUser user;
    Context context;
    TextView HeaderAddress;
    TextView Add,HeaderName;
    ActivityMainBinding binding;
    UserModel userModel;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categories;
    ProductAdapter product;
    ProductAdapter1 product1;
    ProductAdapter2 product2;
    ProductAdapter3 product3;
    ProductAdapter4 product4;

    ArrayList<Product> products;
    ArrayList<Product1> products1;
    ArrayList<Product2> products2;
    ArrayList<Product3> products3;
    ArrayList<Product4> products4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityMainBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));
        auth = FirebaseAuth.getInstance ();
        user = auth.getCurrentUser ();

        if(user==null){
            Toast.makeText (this, "Hi User", Toast.LENGTH_SHORT).show ( );
        }else{
            getAddress ();
        }

       ///internet Connection-----------------------------------------------------------------------
        if (!isConnected ()){
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
        }else {
            Toast.makeText (MainActivity.this, "Wellcome To ZAPEE", Toast.LENGTH_SHORT).show ( );
        }
        ///internet Connection----------------------------------------------------------------------

        HeaderAddress = findViewById (R.id.headerAddress);
        HeaderName = findViewById (R.id.headerAddress);
        Add = findViewById (R.id.add);

        initCategories ( );
        initSlider ( );
        initFashion ( );
        initPhone ();
        initBeauty ();
        initGrocery ();
        initallProduct();


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


void getAddress(){

        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    userModel = task.getResult ( ).toObject (UserModel.class);
                    if (userModel != null) {
                        Add.setText ("Delivery Address : " + userModel.getAddress ( ));
                    }
                }
            }
        });

}
    void initCategories() {
        categories = new ArrayList<> ( );
        categoryAdapter = new CategoryAdapter (this, categories);

        getCategories ( );


        LinearLayoutManager layoutManager = new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL,false);
        binding.categoriesList.setLayoutManager (layoutManager);
        binding.categoriesList.setAdapter (categoryAdapter);
    }

    void getCategories() {
        RequestQueue queue = Volley.newRequestQueue (this);

        StringRequest request = new StringRequest (Request.Method.GET, Constants.GET_CATEGORIES_URL, new Response.Listener<String> ( ) {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e ("err", response);
                    JSONObject mainObj = new JSONObject (response);
                    if (mainObj.getString ("status").equals ("success")) {
                        JSONArray categoriesArray = mainObj.getJSONArray ("categories");
                        for (int i = 0; i < categoriesArray.length ( ); i++) {
                            JSONObject object = categoriesArray.getJSONObject (i);
                            Category category = new Category (
                                    object.getString ("name"),
                                    Constants.CATEGORIES_IMAGE_URL + object.getString ("icon"),
                                    object.getString ("color"),
                                    object.getString ("brief"),
                                    object.getInt ("id")
                            );
                            categories.add (category);
                        }
                        categoryAdapter.notifyDataSetChanged ( );
                    } else {
                        // DO nothing
                    }
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        }, new Response.ErrorListener ( ) {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add (request);
    }



    private void initSlider() {
        getRecentOffers ( );
    }


    void initFashion() {
        products = new ArrayList<> ( );
        product = new ProductAdapter (this, products);

        getFootwear ( );
        getFashion ();

        LinearLayoutManager layoutManager = new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL,false);
        binding.productList.setLayoutManager (layoutManager);
        binding.productList.setAdapter (product);
    }



    void getFootwear() {
        RequestQueue queue = Volley.newRequestQueue (this);
        String url = Constants.GET_PRODUCTS_URL +"?category_id=2";
        StringRequest request = new StringRequest (Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray productsArray = object.getJSONArray ("products");
                    for (int i = 0; i < productsArray.length ( ); i++) {
                        JSONObject childObj = productsArray.getJSONObject (i);
                        Product product = new Product (
                                childObj.getString ("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString ("image"),
                                childObj.getString ("status"),
                                childObj.getDouble ("price"),
                                childObj.getDouble ("price_discount"),
                                childObj.getInt ("stock"),
                                childObj.getInt ("id")

                        );
                        products.add (product);
                    }
                    product.notifyDataSetChanged ( );
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });

        queue.add (request);
    }

    void getFashion(){
        RequestQueue queue = Volley.newRequestQueue (this);
        String url = Constants.GET_PRODUCTS_URL +"?category_id=15";
        StringRequest request = new StringRequest (Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray productsArray = object.getJSONArray ("products");
                    for (int i = 0; i < productsArray.length ( ); i++) {
                        JSONObject childObj = productsArray.getJSONObject (i);
                        Product product = new Product (
                                childObj.getString ("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString ("image"),
                                childObj.getString ("status"),
                                childObj.getDouble ("price"),
                                childObj.getDouble ("price_discount"),
                                childObj.getInt ("stock"),
                                childObj.getInt ("id")

                        );
                        products.add (product);
                    }
                    product.notifyDataSetChanged ( );
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });

        queue.add (request);
    }

    void initPhone() {
        products1 = new ArrayList<Product1> (  );
        product1 = new ProductAdapter1 (this, products1);

        getPhone ();

        LinearLayoutManager layoutManager = new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL,false);
        binding.productList1.setLayoutManager (layoutManager);
        binding.productList1.setAdapter (product1);

    }

    void getPhone() {
        RequestQueue queue = Volley.newRequestQueue (this);
        String url = Constants.GET_PRODUCTS_URL +"?category_id=4" ;
        StringRequest request = new StringRequest (Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray productsArray = object.getJSONArray ("products");
                    for (int i = 0; i < productsArray.length ( ); i++) {
                        JSONObject childObj = productsArray.getJSONObject (i);
                        Product1 product1 = new Product1 (
                                childObj.getString ("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString ("image"),
                                childObj.getString ("status"),
                                childObj.getDouble ("price"),
                                childObj.getDouble ("price_discount"),
                                childObj.getInt ("stock"),
                                childObj.getInt ("id")
                        );
                        products1.add (product1);
                    }
                    product1.notifyDataSetChanged ( );
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });

        queue.add (request);
    }


    void initBeauty() {
        products2 = new ArrayList<> ( );
        product2 = new ProductAdapter2 (this, products2);

       getBeauty ();

        LinearLayoutManager layoutManager = new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL,false);
        binding.beautyList.setLayoutManager (layoutManager);
        binding.beautyList.setAdapter (product2);
    }

    void getBeauty() {
        RequestQueue queue = Volley.newRequestQueue (this);
        String url = Constants.GET_PRODUCTS_URL +"?category_id=1";
        StringRequest request = new StringRequest (Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray productsArray = object.getJSONArray ("products");
                    for (int i = 0; i < productsArray.length ( ); i++) {
                        JSONObject childObj = productsArray.getJSONObject (i);
                        Product2 product = new Product2 (
                                childObj.getString ("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString ("image"),
                                childObj.getString ("status"),
                                childObj.getDouble ("price"),
                                childObj.getDouble ("price_discount"),
                                childObj.getInt ("stock"),
                                childObj.getInt ("id")

                        );
                        products2.add (product);
                    }
                    product.notifyDataSetChanged ( );
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });

        queue.add (request);
    }


    void initGrocery() {
        products3 = new ArrayList<> ( );
        product3 = new ProductAdapter3 (this, products3);

        getGroccery ();
        geFfruit ();
        geSnacks ();
        getOil ();

        LinearLayoutManager layoutManager = new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL,false);
        binding.groceryList.setLayoutManager (layoutManager);
        binding.groceryList.setAdapter (product3);
    }

    void getGroccery() {
        RequestQueue queue = Volley.newRequestQueue (this);
        String url = Constants.GET_PRODUCTS_URL +"?category_id=12";
        StringRequest request = new StringRequest (Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray productsArray = object.getJSONArray ("products");
                    for (int i = 0; i < productsArray.length ( ); i++) {
                        JSONObject childObj = productsArray.getJSONObject (i);
                        Product3 product = new Product3 (
                                childObj.getString ("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString ("image"),
                                childObj.getString ("status"),
                                childObj.getDouble ("price"),
                                childObj.getDouble ("price_discount"),
                                childObj.getInt ("stock"),
                                childObj.getInt ("id")

                        );
                        products3.add (product);
                    }
                    product.notifyDataSetChanged ( );
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });

        queue.add (request);
    }
    void geFfruit() {
        RequestQueue queue = Volley.newRequestQueue (this);
        String url = Constants.GET_PRODUCTS_URL +"?category_id=10";
        StringRequest request = new StringRequest (Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray productsArray = object.getJSONArray ("products");
                    for (int i = 0; i < productsArray.length ( ); i++) {
                        JSONObject childObj = productsArray.getJSONObject (i);
                        Product3 product = new Product3 (
                                childObj.getString ("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString ("image"),
                                childObj.getString ("status"),
                                childObj.getDouble ("price"),
                                childObj.getDouble ("price_discount"),
                                childObj.getInt ("stock"),
                                childObj.getInt ("id")

                        );
                        products3.add (product);
                    }
                    product.notifyDataSetChanged ( );
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });

        queue.add (request);
    }
    void geSnacks() {
        RequestQueue queue = Volley.newRequestQueue (this);
        String url = Constants.GET_PRODUCTS_URL +"?category_id=13";
        StringRequest request = new StringRequest (Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray productsArray = object.getJSONArray ("products");
                    for (int i = 0; i < productsArray.length ( ); i++) {
                        JSONObject childObj = productsArray.getJSONObject (i);
                        Product3 product = new Product3 (
                                childObj.getString ("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString ("image"),
                                childObj.getString ("status"),
                                childObj.getDouble ("price"),
                                childObj.getDouble ("price_discount"),
                                childObj.getInt ("stock"),
                                childObj.getInt ("id")

                        );
                        products3.add (product);
                    }
                    product.notifyDataSetChanged ( );
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });

        queue.add (request);
    }
    void getOil() {
        RequestQueue queue = Volley.newRequestQueue (this);
        String url = Constants.GET_PRODUCTS_URL +"?category_id=11";
        StringRequest request = new StringRequest (Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray productsArray = object.getJSONArray ("products");
                    for (int i = 0; i < productsArray.length ( ); i++) {
                        JSONObject childObj = productsArray.getJSONObject (i);
                        Product3 product = new Product3 (
                                childObj.getString ("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString ("image"),
                                childObj.getString ("status"),
                                childObj.getDouble ("price"),
                                childObj.getDouble ("price_discount"),
                                childObj.getInt ("stock"),
                                childObj.getInt ("id")

                        );
                        products3.add (product);
                    }
                    product.notifyDataSetChanged ( );
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });

        queue.add (request);
    }

    void initallProduct(){
        products4 = new ArrayList<> ( );
        product4 = new ProductAdapter4 (this, products4);
        getAllproduct();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        binding.allproductList.setLayoutManager (layoutManager);
        binding.allproductList.setAdapter (product4);
    }
    void getAllproduct(){
        RequestQueue queue = Volley.newRequestQueue (this);
        String url = Constants.GET_PRODUCTS_URL +"?count=100";
        StringRequest request = new StringRequest (Request.Method.GET, url, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray productsArray = object.getJSONArray ("products");
                    for (int i = 0; i < productsArray.length ( ); i++) {
                        JSONObject childObj = productsArray.getJSONObject (i);
                        Product4 product = new Product4 (
                                childObj.getString ("name"),
                                Constants.PRODUCTS_IMAGE_URL + childObj.getString ("image"),
                                childObj.getString ("status"),
                                childObj.getDouble ("price"),
                                childObj.getDouble ("price_discount"),
                                childObj.getInt ("stock"),
                                childObj.getInt ("id")

                        );
                        products4.add (product);
                    }
                    product.notifyDataSetChanged ( );
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });

        queue.add (request);
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
            Intent intent = new Intent (getApplicationContext ( ), CartActivity.class);
            startActivity (intent);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent (getApplicationContext ( ), ChatActivity.class);
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