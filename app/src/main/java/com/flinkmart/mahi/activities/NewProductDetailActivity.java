package com.flinkmart.mahi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.ColorAdapter;
import com.flinkmart.mahi.adapter.SizeAdapterProductDetail;
import com.flinkmart.mahi.databinding.ActivityNewProductDetailBinding;
import com.flinkmart.mahi.homeadapter.LatestProductAdapter;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.model.Size;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.CartActivity;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.scrab.FavouriteActivity;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class


NewProductDetailActivity extends AppCompatActivity {

    ActivityNewProductDetailBinding binding;
    LatestProductAdapter newProductAdapter;

   SizeAdapterProductDetail sizeAdapter;

   ColorAdapter colorAdapter;

    public boolean is_selected;


    String uid= FirebaseAuth.getInstance ( ).getUid ( );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progressBar6.setVisibility (View.VISIBLE);

        String name = getIntent().getStringExtra("name");
        String discription = getIntent().getStringExtra("description");
        String discount = getIntent().getStringExtra("discount");
        String image = getIntent().getStringExtra("image");
        String id = getIntent().getStringExtra ("id");
        String price =  getIntent().getStringExtra ("price");
        String subcat =  getIntent().getStringExtra ("subcategory");
        String cat =  getIntent().getStringExtra ("category");


        final List<SlideModel>imageList=new ArrayList<> ();
        FirebaseDatabase.getInstance ().getReference ("productimages").child (id).addListenerForSingleValueEvent (new ValueEventListener ( ) {
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


        binding.view.setText (discription);
        binding.name.setText (name);
        binding.price.setText ("₹"+price);
        binding.remove.setVisibility (View.INVISIBLE);


        getSupportActionBar().setTitle("Product Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        SharedPreferences sp3=getSharedPreferences("ProductQnty"+id,MODE_PRIVATE);


        String quantit=sp3.getString ("qnt","1");


        binding.cartbtn2.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                AppDatabase db= Room.databaseBuilder(getApplicationContext (),AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();

                int ide= Integer.parseInt (id);

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


        binding.remove.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                int ide= Integer.parseInt (id);
                AppDatabase db = Room.databaseBuilder(getApplicationContext (),
                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();

                productDao.deleteById(ide);

                binding.remove.setVisibility (View.INVISIBLE);
                binding.cartbtn2.setVisibility (View.VISIBLE);
            }
        });

        AppDatabase db= Room.databaseBuilder(getApplicationContext (),AppDatabase.class,"cart_db").allowMainThreadQueries().build();
        ProductDao productDao=db.ProductDao();
        int ide= Integer.parseInt (id);
        Boolean check=productDao.is_exist(ide);

        if(check==true){
            binding.cartbtn2.setVisibility (View.INVISIBLE);
            binding.remove.setVisibility (View.VISIBLE);
        }
         binding.information.setOnClickListener (new View.OnClickListener ( ) {
             @Override
             public void onClick(View v) {
                 int isVisible=binding.view.getVisibility ();

                 if (isVisible==View.VISIBLE){
                     binding.view.setVisibility (View.GONE);
                     binding.information.setText ("See");

                 }else {
                     binding.view.setVisibility (View.VISIBLE);
                     binding.information.setText ("Hide");
                 }



             }
         });



         initOffer1(subcat);

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
        getMenuInflater().inflate(R.menu.category, menu);


        MenuItem cart=menu.findItem (R.id.cart);
        View actionView=cart.getActionView ();

        TextView quantity=actionView.findViewById (R.id.cart_badge_textview);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        List<Product> products=productDao.getallproduct ();



        if(products.size ()==0){
            quantity.setVisibility (View.GONE);
        }

        int qty = 0,i;
        for (i = 0; i < products.size ( ); i++)
            qty = qty + (products.get (i).getQnt ( ));

        quantity.setText (""+qty);
        actionView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected (cart);
            }
        });


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



    void initOffer1(String subcat){
        getOffer1(subcat);
        List<Item> modelList=new ArrayList<> ();
        newProductAdapter=new LatestProductAdapter (this,modelList)  ;
        LinearLayoutManager layoutManager = new GridLayoutManager (this, 3);
        binding.productList.setLayoutManager (layoutManager);
        binding.productList.setAdapter (newProductAdapter);
    }
    void getOffer1(String subcat){
        FirebaseFirestore.getInstance ()
                .collection ("product")
                .whereEqualTo ("show",true)
                .whereEqualTo ("subcategory",subcat)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            binding.progressBar6.setVisibility (View.GONE);
                            Item product=ds.toObject (Item.class);
                            newProductAdapter.addProduct(product);
                        }


                    }
                });
    }





    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}