package com.flinkmart.mahi.roomdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.activities.NewCheckoutActivity;
import com.flinkmart.mahi.adapter.FilterAdapter;
import com.flinkmart.mahi.homeadapter.BundleAdapter;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.HorizonProductModel;
import com.flinkmart.mahi.model.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    RecyclerView recview;
    TextView rateview,quantity,text,banner;
    Button check,shop;
    CardView layout;

    BundleAdapter newProductAdapter;

    int total=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        rateview=findViewById(R.id.subtotal);
        quantity=findViewById(R.id.qty);
        check=findViewById (R.id.continueBtn);
        text=findViewById(R.id.empty);
        layout=findViewById (R.id.bottomPrice);
        shop=findViewById (R.id.shop);


        shop.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                onBackPressed ();
            }
        });
        getroomdata();
//
//        getProduct();

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();


        List<Product> products=productDao.getallproduct();

            check.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(CartActivity.this, NewCheckoutActivity.class);
                    startActivity(i);
                }
            });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

     void getProduct() {
        initProduct ();
        RecyclerView productList=findViewById (R.id.productList);
        List<HorizonProductModel> modelList=new ArrayList<> ();
        newProductAdapter=new BundleAdapter (this,modelList)  ;
        LinearLayoutManager layoutManager = new LinearLayoutManager (getApplicationContext (),LinearLayoutManager.HORIZONTAL,false);
        productList.setLayoutManager (layoutManager);
        productList.setAdapter (newProductAdapter);

    }

    void initProduct(){
        FirebaseFirestore.getInstance ()
                .collection ("product")
//                .whereEqualTo ("show",true)
                .whereEqualTo ("category","8309")
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            HorizonProductModel product=ds.toObject (HorizonProductModel.class);
                            newProductAdapter.addProduct(product);
                        }


                    }
                });
    }



    public void getroomdata()
    {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();



        CardView cardView=findViewById (R.id.bottomPrice);
        ImageView imageView=findViewById (R.id.imageView7);

        recview=findViewById(R.id.cartList);
        recview.setLayoutManager(new LinearLayoutManager (this));
        text=findViewById(R.id.empty);
        banner=findViewById (R.id.congrage);

        List<Product> products=productDao.getallproduct();
        myadapter adapter=new myadapter (products, rateview,quantity,text,banner,cardView,imageView);
        recview.setAdapter(adapter);


        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());
        total=1000-sum;

        if(sum<1000){
            banner.setText("Add More ₹" +total+ " For Get Free Delivery & Free Bag");
            banner.setTextColor (getColor (R.color.red));
        }else {
            banner.setText("Congratulations You Got Free Delivery & Free Bag");
            banner.setTextColor (getColor (R.color.purple_500));
        }

        if(products.size ()==0){
            text.setVisibility (View.VISIBLE);
            check.setVisibility (View.GONE);
            shop.setVisibility (View.VISIBLE);
            text.setText ("Cart is empty");
            banner.setVisibility (View.GONE);
            imageView.setVisibility (View.VISIBLE);

            layout.setVisibility (View.GONE);
            shop.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    onBackPressed ();
                }
            });

        }else {

        }



        int qty = 0, i2;
        for (i2 = 0; i2 < products.size ( ); i2++)
            qty = qty + (products.get (i2).getQnt ( ));


        if (qty>1){
            quantity.setText (""+qty+" ITEMS");
        }else {
            quantity.setText (""+qty+" ITEM ");
        }
        rateview.setText("₹"+sum);


    }

    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }



}