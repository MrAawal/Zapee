package com.flinkmart.mahi.activities;

import static com.flinkmart.mahi.activities.NewCheckoutActivity.getRandomNumber;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.FilterAdapter;
import com.flinkmart.mahi.databinding.ActivityNewProductDetailBinding;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Favourite;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.CartActivity;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class


NewProductDetailActivity extends AppCompatActivity {

    ActivityNewProductDetailBinding binding;
    FilterAdapter newProductAdapter;

    CartModel cartItem;

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

        Glide.with(this)
                .load(image)
                .into(binding.productImage);

        binding.view.setText (discription);
        binding.name.setText (name);
        binding.price.setText ("₹"+price);


        getSupportActionBar().setTitle("Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
                }else {
                    Toast.makeText (getApplicationContext (), "Item Exist", Toast.LENGTH_SHORT).show ( );
                }
                 binding.cartbtn2.setText ("Added");
            }
        });

        AppDatabase db= Room.databaseBuilder(getApplicationContext (),AppDatabase.class,"cart_db").allowMainThreadQueries().build();
        ProductDao productDao=db.ProductDao();
        List<Product> products=productDao.getallproduct ();
        int ide= Integer.parseInt (id);
        Boolean check=productDao.is_exist(ide);
        if(check==true){
            binding.cartbtn2.setText ("Added");
        }
         binding.information.setOnClickListener (new View.OnClickListener ( ) {
             @Override
             public void onClick(View v) {
                 int isVisible=binding.view.getVisibility ();

                 if (isVisible==View.VISIBLE){
                     binding.view.setVisibility (View.GONE);
                     binding.information.setText ("Product Information            See");

                 }else {
                     binding.view.setVisibility (View.VISIBLE);
                     binding.information.setText ("Product Information           Hide");
                 }



             }
         });


        binding.fav2.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                String uid= FirebaseAuth.getInstance ( ).getUid ( );
                if(uid==null){
                    Toast.makeText (NewProductDetailActivity.this, "Please Login", Toast.LENGTH_SHORT).show ( );
                } else {
                    String orderNumber = String.valueOf (getRandomNumber (11111, 99999));
                    Favourite orderProduct = new Favourite (id,orderNumber,uid,name,image,discount,"",discription,
                            cat,subcat,"",1,Integer.parseInt (price),true);
                    FirebaseFirestore.getInstance ( )
                            .collection ("favourite")
                            .document (id+uid)
                            .set (orderProduct);
                    binding.fav2.setVisibility (View.INVISIBLE);
                    binding.fav.setVisibility (View.VISIBLE);
                    Toast.makeText (NewProductDetailActivity.this, "Added in favourite list", Toast.LENGTH_SHORT).show ( );
                }

            }
        });

       binding.fav.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance ()
                        .collection ("favourite")
                        .document ( id+uid)
                        .delete ()
                        .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful ()){
                                    binding.fav.setVisibility (View.INVISIBLE);
                                    binding.fav2.setVisibility (View.VISIBLE);
                                    Toast.makeText (NewProductDetailActivity.this, "Item Remove", Toast.LENGTH_SHORT).show ( );
                                }
                            }
                        });

            }
        });

        FirebaseUtil.cartDetails (id+uid).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    cartItem = task.getResult ( ).toObject (CartModel.class);
                    if (cartItem!= null){
                        binding.cartbtn.setBackgroundColor(getApplication ().getResources ().getColor (R.color.teal_700));
                    }else{

                    }
                }
            }

        });
        FirebaseUtil.favdetail (id+uid).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    cartItem = task.getResult ( ).toObject (CartModel.class);
                    if (cartItem!= null){
                        binding.fav2.setVisibility (View.INVISIBLE);
                    }else{

                    }
                }
            }

        });

        initOffer1(subcat);
    }

    private void bottomsheet(String discription) {

            BottomSheetDialog bottomSheetDialog=new BottomSheetDialog ( this);
            View view= LayoutInflater.from (NewProductDetailActivity.this).inflate (R.layout.description,(LinearLayout)findViewById (R.id.mainlayout),false);
            bottomSheetDialog.setContentView (view);
            bottomSheetDialog.show ();

            TextView Discription=view.findViewById (R.id.dis);
            Discription.setText (discription);





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

        quantity.setText (""+qty+" Items");
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
        newProductAdapter=new FilterAdapter (this,modelList)  ;
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