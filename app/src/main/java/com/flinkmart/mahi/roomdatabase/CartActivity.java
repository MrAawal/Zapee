package com.flinkmart.mahi.roomdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.activities.NewCheckoutActivity;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    RecyclerView recview;
    TextView rateview,quantity,text,banner;
    Button check,shop;
    CardView layout;

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
                Intent i=new Intent(CartActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        getroomdata();

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

    public void getroomdata()
    {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();



        CardView cardView=findViewById (R.id.bottomPrice);
        recview=findViewById(R.id.cartList);
        recview.setLayoutManager(new LinearLayoutManager (this));
        text=findViewById(R.id.empty);
        banner=findViewById (R.id.congrage);

        List<Product> products=productDao.getallproduct();
        myadapter adapter=new myadapter (products, rateview,quantity,text,banner,cardView);
        recview.setAdapter(adapter);


        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());
        total=500-sum;

        if(sum<500){
            banner.setText("Add More ₹" +total+ " For Get Free Delivery & Free Bag");
            banner.setTextColor (getColor (R.color.red));
        }else {
            banner.setText("Congragulation You Got Free Delivery & Free Bag");
            banner.setTextColor (getColor (R.color.purple_500));
        }

        if(products.size ()==0){
            text.setVisibility (View.VISIBLE);
            check.setVisibility (View.GONE);
            shop.setVisibility (View.VISIBLE);
            banner.setVisibility (View.GONE);
            layout.setVisibility (View.GONE);
            shop.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getApplicationContext (), MainActivity.class);
                    startActivity(i);
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