package com.flinkmart.mahi.restuarant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.activities.ProfileActivity;
import com.flinkmart.mahi.adapter.FilterAdapter2;
import com.flinkmart.mahi.branchAdapter.BranchAdapter;
import com.flinkmart.mahi.branchAdapter.FashionAdapter;
import com.flinkmart.mahi.branchAdapter.RestuarantAdapter;
import com.flinkmart.mahi.databinding.ActivityRestuarantBinding;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.Catlist;
import com.flinkmart.mahi.model.Fashion;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.model.Resturant;
import com.flinkmart.mahi.model.UserModel1;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.myadapter2;
import com.flinkmart.mahi.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RestuarantActivity extends AppCompatActivity {
    ActivityRestuarantBinding binding;
    BranchAdapter branchAdapter;
    FilterAdapter2 filterAdapter2;
    RestaurantCategoryAdapter catlistAdapter;

    RestaurantSubCategoryAdapter subcatList;
    List<Catlist> catlists = new ArrayList<> ();

    List<SubCatlistModel> subCatlistModels = new ArrayList<> ();
    List<Item> item1s = new ArrayList<> ();
    FashionAdapter fashionAdapter;
    RestuarantAdapter restuarantAdapter;
    private static List<Branch> branchemodel;
    private static List<Fashion> fashions;
    private static List<Resturant> resturants;

    DatabaseReference databaseReference;


    UserModel1 userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityRestuarantBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));

        TextView warning = findViewById (R.id.textView29);
        binding.scroll.setVisibility (View.INVISIBLE);
        getUsername ( );
        upadecartLayout();

        String pin = getIntent ( ).getStringExtra ("restId");
        String name = getIntent ( ).getStringExtra ("restName");

        binding.textView27.setText (name);
        binding.about.setText ("About "+name);


        databaseReference= FirebaseDatabase.getInstance ().getReference ("restaurantDetail");

        databaseReference.addValueEventListener (new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value=snapshot.child (pin).getValue (String.class);
                binding.information.setText (value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.imageView5.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                startActivity (new Intent (getApplicationContext ( ), RestaurantCartActivity.class));
            }
        });

        banner ( pin);
        getRestuarentCategory (pin);
        if(binding.searchView==null){
            binding.banner.setVisibility (View.VISIBLE);
        }


        TextView quantity=findViewById (R.id.cartQnt);
        CardView layout=findViewById (R.id.cartlayout);

        initTrendingProduct (pin,quantity,layout);

        binding.searchView.setBackgroundResource (R.drawable.product);
        binding.searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener ( ) {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAdapter2.getFilter( ).filter (newText);
                binding.banner.setVisibility (View.GONE);
                return true;
            }
        });

        binding.imageView4.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                startActivity (new Intent (getApplicationContext ( ), ProfileActivity.class));
            }
        });

        ProgressBar progressBar=binding.progressBar16;
        progressBar.setVisibility (View.INVISIBLE);
        catlistAdapter = new RestaurantCategoryAdapter (this, catlists,binding.subcat,subcatList, subCatlistModels,progressBar);
        binding.branchList.setAdapter (catlistAdapter);
        binding.branchList.setLayoutManager (new LinearLayoutManager (this, LinearLayoutManager.HORIZONTAL, false));


        subcatList = new RestaurantSubCategoryAdapter (this);
        binding.subcat.setAdapter (subcatList);
        binding.subcat.setLayoutManager (new GridLayoutManager (this,5));

        filterAdapter2 = new FilterAdapter2 (this, item1s,quantity,layout);
        binding.DealsList.setAdapter (filterAdapter2);
        binding.DealsList.setLayoutManager (new GridLayoutManager (this, 2));

        fashionAdapter = new FashionAdapter (this, warning);
        binding.fashionList.setAdapter (fashionAdapter);
        binding.fashionList.setLayoutManager (new LinearLayoutManager (this));

    }



    private void banner(String pin) {
        final List<SlideModel> imageList = new ArrayList<> ( );
        FirebaseDatabase.getInstance ( ).getReference ( "restaurantbanner").child (pin).addListenerForSingleValueEvent (new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren ( )) {
                    imageList.add (new SlideModel (ds.child ("image").getValue ( ).toString ( ), ds.child ("tittle").getValue ( ).toString ( ), ScaleTypes.FIT));
                    binding.carousel.setImageList (imageList, ScaleTypes.FIT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                RequestQueue queue = Volley.newRequestQueue (getApplicationContext ( ));
                StringRequest request = new StringRequest (Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
                    try {
                        JSONObject object = new JSONObject (response);
                        if (object.getString ("status").equals ("success")) {
                            JSONArray offerArray = object.getJSONArray ("news_infos");
                            for (int i = 0; i < offerArray.length ( ); i++) {
                                JSONObject childObj = offerArray.getJSONObject (i);
                                imageList.add (new SlideModel (Constants.NEWS_IMAGE_URL + childObj.getString ("image"), "", ScaleTypes.FIT));
                                binding.carousel.setImageList (imageList, ScaleTypes.FIT);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace ( );
                    }
                }, (error1) -> {
                });
                queue.add (request);
            }
        });


    }
    void getRestuarentCategory(String pin) {
        FirebaseFirestore.getInstance ( )
                .collection ("category")
                .whereEqualTo ("branch", pin)
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
    private void initTrendingProduct(String pin, TextView quantity, CardView layout) {
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .whereEqualTo ("show", true)
                .whereEqualTo ("branch", pin)
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            binding.scroll.setVisibility (View.VISIBLE);
                            binding.progressBar8.setVisibility (View.INVISIBLE);
                            Item productList = ds.toObject (Item.class);
                            filterAdapter2.addProduct (productList);
                        }

                    }
                });
    }
    void getUsername(){
        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {

                    userModel = task.getResult ( ).toObject (UserModel1.class);
                    if (userModel != null) {
                        binding.textView4.setText ("Hi " + userModel.getUsername( ));
                        binding.textView11.setText (userModel.getAddress ( ));

                    }
                }
            }
        });
    }
    private void upadecartLayout() {

        TextView quantity=findViewById (R.id.cartQnt);
        TextView Subtotal=findViewById (R.id.Subtotal);
        TextView text=findViewById (R.id.text);
        TextView banner=findViewById (R.id.congrage);
        CardView layout=findViewById (R.id.cartlayout);
        RecyclerView recyclerView=findViewById (R.id.productList);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        List<Product> products=productDao.getallproduct ();

        binding.cartList.setLayoutManager (new LinearLayoutManager (this));
        myadapter2 adapter=new myadapter2(products,quantity,Subtotal, text, banner);
        binding.cartList.setAdapter(adapter);


        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());

        Subtotal.setText("₹"+sum);

        int qty = 0;
        for (i = 0; i < products.size ( ); i++)
            qty = qty + (products.get (i).getQnt ( ));


        if (qty>1){
            quantity.setText (""+qty+" ITEMS IN CART |"+"₹"+sum);
            layout.setVisibility (View.VISIBLE);
        }else {
            quantity.setText (""+qty+" ITEM IN CART |"+"₹"+sum);
        }
        if(products.size ()==0){
            binding.cartlayout.setVisibility (View.GONE);
        }
        binding.continues.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                startActivity(new Intent (getApplicationContext (), RestaurantCartActivity.class));

            }
        });

        binding.imageButton3.setVisibility (View.GONE);
        binding.continues.setText ("Go to cart");

    }
    @Override
    public void onBackPressed(){
        AppDatabase db = Room.databaseBuilder (getApplicationContext ( ),
                AppDatabase.class, "cart_db").allowMainThreadQueries ( ).build ( );
        ProductDao productDao = db.ProductDao ( );

        List<Product> products = productDao.getallproduct ( );
        String uid=FirebaseAuth.getInstance ().getUid ();

        if (products.size ( )==0){
            startActivity (new Intent (getApplicationContext ( ), MainActivity.class));
            FirebaseFirestore.getInstance ()
                    .collection ("userRestaurant")
                    .document (uid)
                    .delete ();
            finish ();
        } else {
            new AlertDialog.Builder (RestuarantActivity.this)
                    .setTitle ("Clear cart!")
                    .setCancelable (false)
                    .setMessage ("Clear cart for go back")
                    .setPositiveButton ("Clear now", new DialogInterface.OnClickListener ( ) {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity (new Intent (getApplicationContext ( ), MainActivity.class));
                            finish ();

                            startActivity (new Intent (getApplicationContext ( ), MainActivity.class));
                            FirebaseFirestore.getInstance ()
                                    .collection ("userRestaurant")
                                    .document (uid)
                                    .delete ();
                            db.clearAllTables ( );
                        }

                    }).setNegativeButton ("No", new DialogInterface.OnClickListener ( ) {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel ( );
                        }
                    }).show ( );


        }


    }

}