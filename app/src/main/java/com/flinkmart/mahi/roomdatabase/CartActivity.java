package com.flinkmart.mahi.roomdatabase;

import static com.flinkmart.mahi.R.color.purple_500;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.NewCheckoutActivity;
import com.flinkmart.mahi.branchAdapter.BranchAdapter;
import com.flinkmart.mahi.databinding.StoreDialogBinding;
import com.flinkmart.mahi.homeadapter.BundleAdapter;
import com.flinkmart.mahi.model.BranchModel;
import com.flinkmart.mahi.model.HorizonProductModel;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.model.UserModel1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    int minAmount=0;

    BranchModel branch;

    BundleAdapter newProductAdapter;

    int total=0;

    UserModel userModel;

    BranchAdapter branchAdapter;

    DatabaseReference databaseReference;
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

        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    userModel =task.getResult ( ).toObject (UserModel.class);
                    if (userModel != null) {
                        FirebaseUtil.currentUserStore( ).get().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful ( )) {
                                    branch = task.getResult ( ).toObject (BranchModel.class);
                                    if (branch!=null){

                                        databaseReference= FirebaseDatabase.getInstance ().getReference ("store/"+branch.getStoreuid ());
                                        databaseReference.addListenerForSingleValueEvent (new ValueEventListener ( ) {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int minAmount=snapshot.child ("minimum").getValue (Integer.class);
                                                getroomdata (minAmount);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });





                                    }else{
                                        String pin= userModel.getPin( );
                                        selectStore(pin);
                                    }
                                }
                            }
                        });
                    }else{
//
                    }

                }
            }
        });


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




    public void getroomdata(int minAmount) {
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
        total= minAmount -sum;

        if(sum<minAmount){
            banner.setText ("Add More ₹"+total+"For Get Free Delivery");
            banner.setTextColor (banner.getContext ().getResources ().getColor (R.color.red));
        }else {
            banner.setText("Congratulations You Got Free Delivery");
            banner.setTextColor (banner.getContext ().getResources ().getColor (purple_500));
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


    public  void selectStore(String pin){
        StoreDialogBinding storeDialogBinding = StoreDialogBinding.inflate(LayoutInflater.from(this));
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(storeDialogBinding.getRoot())
                .setCancelable (false)
                .create();

        RecyclerView recyclerView=storeDialogBinding.storeList;

        TextView warning=storeDialogBinding.productName;
        branchAdapter=new BranchAdapter (this,warning);
        recyclerView.setLayoutManager (new LinearLayoutManager (this));
        recyclerView.setAdapter (branchAdapter);

        FirebaseFirestore.getInstance ()
                .collection ("branch")
                .whereEqualTo ("pincode",pin)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            BranchModel resturants=ds.toObject (BranchModel.class);
                            branchAdapter.addProduct (resturants);
                        }

                    }
                });
        dialog.show ();
    }


}