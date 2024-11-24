package com.flinkmart.mahi.activities;

import static com.flinkmart.mahi.activities.NewCheckoutActivity.getRandomNumber;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.SubCategoryItemAdapter;
import com.flinkmart.mahi.databinding.ActivityNewProductDetailBinding;
import com.flinkmart.mahi.model.Favourite;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.model.ProductTiny;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.CartActivity;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.List;

public class SearchProductDetailActivity extends AppCompatActivity {

    ActivityNewProductDetailBinding binding;
    ProductTiny currentProduct;
    SubCategoryItemAdapter newProductAdapter;
    Favourite favorite;

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

        Glide.with(this)
                .load(image)
                .into(binding.productImage);

        getProductDetails(id);

        getSupportActionBar().setTitle(name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Cart cart = TinyCartHelper.getCart();

        binding.view.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                bottomsheet(discription);
            }
        });
        binding.price.setText ("₹"+price);
        binding.name.setText(name);
        binding.cartbtn2.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AppDatabase db= Room.databaseBuilder(getApplicationContext (),AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();
                List<Product> products=productDao.getallproduct ();

                int ide= Integer.parseInt (id);

                Boolean check=productDao.is_exist(ide);
                if(check==false) {
                    productDao.insertrecord (new Product (ide,name,image,Integer.parseInt (price),1,discount,discription));
                    Toast.makeText (SearchProductDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show ( );
                }else {
                    Toast.makeText (getApplicationContext (), "Item Exist", Toast.LENGTH_SHORT).show ( );
                }
//                binding.cartbtn2.setBackgroundColor (getApplication ().getResources ().getColor (R.color.purple_500));

            }
        });
        binding.fav.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                String uid= FirebaseAuth.getInstance ( ).getUid ( );
                if(uid==null){
                    Toast.makeText (SearchProductDetailActivity.this, "Please Login", Toast.LENGTH_SHORT).show ( );
                } else {
                    String orderNumber = String.valueOf (getRandomNumber (11111, 99999));
                    Favourite orderProduct = new Favourite (id,orderNumber,uid,name,image,discount,"",discription,
                            cat,subcat,"",1,Integer.parseInt (price),true);
                    FirebaseFirestore.getInstance ( )
                            .collection ("favourite")
                            .document (id)
                            .set (orderProduct);
                    binding.fav.setEnabled(false);
                    binding.fav.setVisibility (View.INVISIBLE);
                    binding.fav2.setVisibility (View.VISIBLE);
                    Toast.makeText (SearchProductDetailActivity.this, "Added in favourite list", Toast.LENGTH_SHORT).show ( );
                }

            }
        });
        initOffer1(subcat);
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
        newProductAdapter=new SubCategoryItemAdapter (this)  ;
        LinearLayoutManager layoutManager = new GridLayoutManager (this, 2);
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