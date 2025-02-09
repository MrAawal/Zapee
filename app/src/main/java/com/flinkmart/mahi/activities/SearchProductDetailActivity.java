package com.flinkmart.mahi.activities;

import static com.flinkmart.mahi.activities.NewCheckoutActivity.getRandomNumber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.ColorAdapter;
import com.flinkmart.mahi.adapter.SizeAdapter;
import com.flinkmart.mahi.adapter.SizeAdapterProductDetail;
import com.flinkmart.mahi.adapter.SubCategoryItemAdapter;
import com.flinkmart.mahi.databinding.ActivityNewProductDetailBinding;
import com.flinkmart.mahi.homeadapter.LatestProductAdapter;
import com.flinkmart.mahi.model.Favourite;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.model.ProductTiny;
import com.flinkmart.mahi.model.Size;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.CartActivity;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.scrab.FavouriteActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchProductDetailActivity extends AppCompatActivity {

    ActivityNewProductDetailBinding binding;
    ProductTiny currentProduct;
    LatestProductAdapter newProductAdapter;
    Favourite favorite;

    SizeAdapterProductDetail sizeAdapter;
    ColorAdapter colorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String name = getIntent().getStringExtra("name");
        String discription = getIntent().getStringExtra("description");
        String discount = getIntent().getStringExtra("discount");
        String image = getIntent().getStringExtra("image");
        String id = getIntent().getStringExtra ("id");
        String price =  getIntent().getStringExtra ("price");
        String subcat =  getIntent().getStringExtra ("subcategory");
        String cat =  getIntent().getStringExtra ("category");

        final List<SlideModel>imageList=new ArrayList<> ();
        FirebaseDatabase.getInstance ().getReference ().child (id).addListenerForSingleValueEvent (new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren ( )) {
                    imageList.add (new SlideModel (ds.child ("link").getValue (  ).toString (), "", ScaleTypes.FIT));
                    binding.carousel.setImageList (imageList,ScaleTypes.FIT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getProductDetails(id);

        getSupportActionBar().setTitle(name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding.view.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                bottomsheet(discription);
            }
        });
        binding.price.setText ("₹"+price);
        binding.name.setText(name);

        binding.cartbtn2.setVisibility (View.VISIBLE);
        binding.remove.setVisibility (View.INVISIBLE);
        binding.cartbtn2.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                AppDatabase db= Room.databaseBuilder(getApplicationContext (),AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();

                int ide= Integer.parseInt (id);

                SharedPreferences sp3=getSharedPreferences("ProductQnty"+id,MODE_PRIVATE);


                String quantit=sp3.getString ("qnt","1");

                Boolean check=productDao.is_exist(ide);

                if(check==false){
                    SharedPreferences sp=getSharedPreferences("ProductSize",MODE_PRIVATE);
                    SharedPreferences sp2=getSharedPreferences("ProductColor",MODE_PRIVATE);

                    String size = sp.getString ("size", "")+"-"+sp2.getString ("color", "");

                    productDao.insertrecord (new Product (ide,name+size,image,Integer.parseInt (price),1,discount,discription));
                    binding.remove.setVisibility (View.VISIBLE);
                    binding.cartbtn2.setVisibility (View.INVISIBLE);

                    SharedPreferences.Editor editor=sp.edit ();
                    SharedPreferences.Editor editor1=sp2.edit ();
                    SharedPreferences.Editor editor2=sp3.edit ();

                    editor.clear ().commit ();
                    editor.apply();

                    editor2.clear ().commit ();
                    editor.apply();

                    editor1.clear ().commit ();
                    editor.apply();

                    editor1.remove ("color");
                    editor1.apply ();

                    editor2.remove ("qnt");
                    editor2.apply ();


                }

            }
        });

        RecyclerView colorList=binding.colorList;
        TextView textView=findViewById (R.id.cartbtn2);
        sizeAdapter=new SizeAdapterProductDetail (this, colorAdapter, colorList,id,textView)  ;
        LinearLayoutManager layoutManager = new GridLayoutManager (this, 4);
        binding.sizeList.setLayoutManager (layoutManager);
        binding.sizeList.setAdapter (sizeAdapter);

        colorAdapter=new ColorAdapter (this)  ;
        LinearLayoutManager layoutManager1 = new GridLayoutManager (this, 4);
        binding.colorList.setLayoutManager (layoutManager1);
        binding.colorList.setAdapter (colorAdapter);

        sizeList(id);

        initOffer1(subcat);
    }

    private void sizeList(String id) {
        FirebaseFirestore.getInstance ()
                .collection ("productSize")
                .whereEqualTo ("id",id)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            binding.cartbtn2.setEnabled (false);
                            binding.cartbtn2.setTextColor (getResources ().getColor (R.color.grey));
                            Size product=ds.toObject (Size.class);
                            sizeAdapter.addProduct(product);
                        }


                    }
                });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.cart) {
            startActivity(new Intent (this, CartActivity.class));
        } else if (item.getItemId() == R.id.fav) {
            startActivity(new Intent (this, FavouriteActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    void getProductDetails(String id) {
        String name = getIntent().getStringExtra("name");
        String image = getIntent().getStringExtra("image");
        String discount = getIntent().getStringExtra("discount");
        int price = Integer.parseInt (getIntent().getStringExtra ("price"));

        currentProduct = new ProductTiny (name,image,"",price,Integer.parseInt (discount),10,Integer.parseInt (id));

    }


    void initOffer1(String subcat){
        getOffer1(subcat);
        List<Item>itemList=new ArrayList<> ();
        newProductAdapter=new LatestProductAdapter (this,itemList)  ;
        LinearLayoutManager layoutManager = new GridLayoutManager (this, 3);
        binding.productList.setLayoutManager (layoutManager);
        binding.productList.setAdapter (newProductAdapter);
    }
    void getOffer1(String subcat){
        FirebaseFirestore.getInstance ()
                .collection ("product")
                .whereEqualTo ("subcategory",subcat)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            binding.progressBar6.setVisibility (View.INVISIBLE);
                            Item product=ds.toObject (Item.class);
                            newProductAdapter.addProduct(product);
                        }


                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent (SearchProductDetailActivity.this, SearchActivity.class);
        startActivity (intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        return super.onSupportNavigateUp();
    }

    private void bottomsheet(String discription) {

        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog ( this);
        View view= LayoutInflater.from (SearchProductDetailActivity.this).inflate (R.layout.description,(LinearLayout)findViewById (R.id.mainlayout),false);
        bottomSheetDialog.setContentView (view);
        bottomSheetDialog.show ();

        TextView Discription=view.findViewById (R.id.dis);
        Discription.setText (discription);





    }
    public void onBackPressed() {
        Intent intent = new Intent (SearchProductDetailActivity.this, SearchActivity.class);
        startActivity (intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish ();
    }
}