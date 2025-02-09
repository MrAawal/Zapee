package com.flinkmart.mahi.activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.branchAdapter.BranchAdapter;
import com.flinkmart.mahi.adapter.CheckItemAdapter;
import com.flinkmart.mahi.databinding.ActivityNewCheckoutBinding;
import com.flinkmart.mahi.databinding.OrderProgressBinding;
import com.flinkmart.mahi.databinding.StoreDialogBinding;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.Coupon;
import com.flinkmart.mahi.model.Online;
import com.flinkmart.mahi.model.OrderPlaceModel;
import com.flinkmart.mahi.model.UserModel1;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.mycheckadapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;
import java.util.Random;

public class NewCheckoutActivity extends AppCompatActivity {
    ActivityNewCheckoutBinding binding;
    double del = 0;
    final int gst = 5;
    SupportMapFragment smf;
    FusedLocationProviderClient client;
    int Discount=0;
    int DiscountSavePrice=0;
    private String uid;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    TextView couponInput;
    int couponTarget=0;
    FirebaseUser user;
    UserModel1 userModel;
    Online online;
    Branch branch;
    CheckItemAdapter checkItemAdapter;
    int maintotal=0;
    int more=0;
    double Total;
    int DiscountTotal=0;
    Coupon coupon;
    Double distance;
    DatabaseReference databaseReference;
    BranchAdapter branchAdapter;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityNewCheckoutBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));
        auth = FirebaseAuth.getInstance ( );
        user = auth.getCurrentUser ( );

        progressBar=new ProgressDialog (this);
        progressBar.setMessage ("Calculating...");
        progressBar.setCancelable (false);
        progressBar.create ();
        progressBar.show ();


        OrderProgressBinding orderProgress = OrderProgressBinding.inflate (LayoutInflater.from (this));
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder (this)
                .setView (orderProgress.getRoot ( ))
                .setCancelable (false)
                .create ( );





        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);

        Dexter.withContext(this).withPermission(ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener () {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getmylocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        progressDialog = new ProgressDialog (this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Order Processing...");

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();


        List<Product> products=productDao.getallproduct();



        if(products.size()==0){
                   binding.mainlayout.setVisibility (View.GONE);
                   binding.button3.setVisibility (View.VISIBLE);
                   binding.textView.setVisibility (View.VISIBLE);
                   binding.emty.setVisibility (View.VISIBLE);
                   binding.linearLayout7.setVisibility (View.GONE);
                   binding.button3.setOnClickListener (new View.OnClickListener ( ) {
                       @Override
                       public void onClick(View v) {
                           Intent i = new Intent (getApplicationContext ( ), MainActivity.class);
                           startActivity (i);
                       }
                   });
        }

        if (user == null) {
            Intent i = new Intent (getApplicationContext ( ),LoginActivity.class);
            startActivity (i);
            finish ( );
        } else {
            binding.checkoutBtn.setEnabled (false);
            FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful ( )) {
                        binding.checkoutBtn.setEnabled (true);
                        userModel = task.getResult ( ).toObject (UserModel1.class);
                        if (userModel != null) {
                            binding.phoneBox.setText (userModel.getPhone ( ));
                            binding.addressBox.setText (userModel.getAddress ( ));
                            binding.Name.setText (userModel.getUsername ( ));
                            binding.Contact.setText (userModel.getPhone ( ));
                            binding.Email.setText (userModel.getPin ( ));
                            binding.pinnumber.setText (userModel.getAddress ( ));
//
                            FirebaseUtil.currentUserStore ( ).get( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful ( )) {
                                        branch = task.getResult ( ).toObject (Branch.class);
                                        if (branch!=null){
                                            getStoreStatus(userModel.getPin ());
                                            binding.address.setText ("Zone:" + branch.getPincode ());

                                        }else{
                                            binding.checkoutBtn.setEnabled (false);
                                            String pin= userModel.getPin ( );
                                            selectStore (pin);
                                        }
                                    }
                                }
                            });
                        }else{
//                            completeProfile();
                        }

                    }
                }
            });
        }

        progressDialog = new ProgressDialog (this);
        progressDialog.setCancelable (false);
        progressDialog.setMessage ("Processing...");


        uid = FirebaseAuth.getInstance ( ).getUid ( );
        TextView rateview=findViewById (R.id.subtotal);
        getProduct (rateview);
        binding.checkoutBtn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                String orderNumber = String.valueOf (getRandomNumber (11111, 99999));
                processOrder (orderNumber,dialog);
                dialog.show ();
            }
        });

        binding.imageButton.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                String pin =userModel.getPin ();
                selectStore (pin);
            }
        });

        getSupportActionBar ( ).setDisplayHomeAsUpEnabled (true);

    }

    private void getStoreStatus(String pin) {

        databaseReference= FirebaseDatabase.getInstance ().getReference ("store/"+pin);
        databaseReference.addListenerForSingleValueEvent (new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean value=snapshot.child ("online").getValue (boolean.class);
                if(value!=true){

                    binding.view.setText ("We are unavailable right now");
                    binding.checkoutBtn.setEnabled (false);
                    binding.checkoutBtn.setText ("Store closed");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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
                            binding.checkoutBtn.setEnabled (true);
                            Branch resturants=ds.toObject (Branch.class);
                            branchAdapter.addProduct (resturants);
                        }

                    }
                });
        dialog.show ();
    }
    public  void UpdateStore(String pin){
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
                            binding.checkoutBtn.setEnabled (true);
                            Branch resturants=ds.toObject (Branch.class);
                            branchAdapter.addProduct (resturants);
                        }

                    }
                });
        dialog.show ();
    }

    public void getmylocation() {

        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    binding.checkoutBtn.setEnabled (true);
                    userModel = task.getResult ( ).toObject (UserModel1.class);
                    if (userModel != null) {
                        FirebaseUtil.currentUserStore ( ).get( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                if (task2.isSuccessful ( )) {
                                    branch = task2.getResult ( ).toObject (Branch.class);
                                    if (branch!=null){
                                        databaseReference= FirebaseDatabase.getInstance ().getReference ("deliverycharge/");
                                        databaseReference.addListenerForSingleValueEvent (new ValueEventListener ( ) {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int value=snapshot.child (branch.getStorename ()).getValue (Integer.class);


                                        double delivery=value;
                                        double lat=Double.parseDouble (branch.getStoreLat ());
                                        double lon=Double.parseDouble (branch.getStoreLon ());


                                        if (ActivityCompat.checkSelfPermission(getApplicationContext (), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext (), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    ActivityCompat#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for ActivityCompat#requestPermissions for more details.
                                            return;
                                        }

                                        Task<Location> task = client.getLastLocation();
                                        task.addOnSuccessListener(new OnSuccessListener<Location> () {
                                            @Override
                                            public void onSuccess(final Location location) {
                                                smf.getMapAsync(new OnMapReadyCallback () {
                                                    @Override
                                                    public void onMapReady(GoogleMap googleMap) {

                                                        progressBar.cancel ();
                                                        LatLng userCurentLocation=new LatLng(location.getLatitude(),location.getLongitude());
                                                        MarkerOptions markerOptions=new MarkerOptions().position(userCurentLocation).title("You are here...!!");
//                                                        googleMap.addMarker(markerOptions);





                                                        SharedPreferences sp=getSharedPreferences("location",MODE_PRIVATE);
                                                        if(sp.contains("latitude")){

                                                           String userlat = sp.getString ("latitude", "");
                                                           String userlon = sp.getString ("longitude", "");

                                                            LatLng userAddress=new LatLng(Double.parseDouble (userlat),Double.parseDouble (userlon));
                                                            MarkerOptions markerOptions3=new MarkerOptions().position(userAddress).title("Delivery Address");
                                                            googleMap.addMarker(markerOptions3);

                                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userAddress,14));

                                                            LatLng store=new LatLng(Double.parseDouble (branch.getStoreLat ()),Double.parseDouble (branch.getStoreLon ()));
                                                            MarkerOptions markerOptions2=new MarkerOptions().position(store).title("Pickup point");
                                                            googleMap.addMarker(markerOptions2);

                                                            distance = SphericalUtil.computeDistanceBetween (userAddress, store);
                                                            int deliverytime;
                                                            deliverytime= (int) (distance/1000*2+20);
                                                            binding.textView32.setText ("Estimate delivery time : "+deliverytime);

                                                            calculation(distance,delivery);

                                                        }

                                                    }
                                                });
                                            }
                                        });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });



    }
    void calculation(Double distance, Double delivery) {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        List<Product> products=productDao.getallproduct();
        super.onStart ( );
        for (int i=0;i<products.size ();i++){
            Product favourite=products.get(i);

            int pric=favourite.getPrice ();
            int quan=favourite.getQnt ();
            int total=pric*quan;
            int discount = Integer.parseInt (favourite.getDiscount ( ))*quan;
            maintotal+=total;
            del=20+distance/1000*delivery;
            Total=maintotal+del+gst;

            more=1000-maintotal;

            DiscountTotal+=discount;
            Discount=DiscountTotal-maintotal;
        }
        binding.mrptotal.setText ("Mrp:₹"+DiscountTotal);
//        binding.mrptotal.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        binding.subtotal.setText (String.valueOf ("Sub total:₹"+maintotal));

        binding.saveMrp.setText ("Save:₹"+Discount);
        binding.Del.setText ("₹"+del);
        if(distance/1000>7){
            binding.checkoutBtn.setEnabled (false);
            binding.checkoutBtn.setText("Out of zone");
            MaterialAlertDialogBuilder alertDialog=new MaterialAlertDialogBuilder (this);
              alertDialog.setMessage ("Your location is out of your selected zone.We deliver 7km radius from store of your selected zone.We are sorry for the inconvenience caused to you.");
              alertDialog.setCancelable (false);
              alertDialog.create ();

              alertDialog.setPositiveButton ("Update zone", new DialogInterface.OnClickListener ( ) {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      UpdateStore(userModel.pin);
                  }
              });
              alertDialog.show ();

        }

        if(maintotal>1000){
            Total=maintotal;
            binding.MaxTotal.setText(String.valueOf ("₹"+maintotal));
            binding.Del.setText ("FREE");
            binding.tax.setText ("FREE");
            binding.tax.setTextColor (getColor (R.color.teal_700));
            binding.Del.setTextColor (getColor (R.color.teal_700));
            binding.view.setText ("Congratulations You Got Free Delivery");
            binding.view.setTextColor (getColor (R.color.purple_500));
            binding.couponLayout.setVisibility (View.GONE);
        }else {
            binding.total.setText ("₹"+Total);
            binding.MaxTotal.setText (String.valueOf ("₹"+Total));
            binding.view.setText("Add More ₹" +more+ " For Get Free Delivery");
            binding.view.setTextColor (getColor (R.color.red));
        }

        FirebaseUtil
                .coupon ( )
                .get ( )
                .addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful ( )) {
                            coupon = task.getResult ( ).toObject (Coupon.class);
                            if (coupon!= null) {
                                couponInput = findViewById (R.id.nameBox);
                                String match = couponInput.getText ( ).toString ( );
                                String coupouns= coupon.getCode ().toUpperCase ();
                                couponInput.setText (coupouns);

                                binding.button5.setOnClickListener (new View.OnClickListener ( ) {
                                    @Override
                                    public void onClick(View v) {
                                        couponTarget=coupon.getTarget ();
                                        if(maintotal>couponTarget) {
                                            int couponDiscount = 0;
                                            couponDiscount = coupon.getValue ( );
                                            binding.button5.setText ("Applied");
                                            binding.nameBox.setTextColor (getColor (R.color.teal_700));
                                            Total = Total - couponDiscount;
                                            binding.MaxTotal.setText ("₹" + Total);
                                            binding.warn.setVisibility (View.GONE);
                                            binding.button5.setEnabled (false);
                                        }else {
                                            binding.textView3.setText ("Please add morethan ₹" +couponTarget+" of Order value");
                                            binding.warn.setVisibility (View.VISIBLE);
                                        }
                                    }
                                });

                            }
                        }
                    }
                });





    }
    void getProduct(TextView rateview){
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        binding.cartList.setLayoutManager(new LinearLayoutManager (this));

        List<Product> products=productDao.getallproduct();

        TextView quantity = null;

        mycheckadapter adapter=new mycheckadapter (products, rateview,quantity);
        binding.cartList.setAdapter(adapter);

        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());



    }
    void processOrder(String orderNumber, androidx.appcompat.app.AlertDialog dialog){

        SharedPreferences sp=getSharedPreferences("location",MODE_PRIVATE);
        if(sp.contains("latitude")) {

            String lat=sp.getString ("latitude","");
            String lon=sp.getString ("longitude","");

            OrderPlaceModel orderPlaceModel = new OrderPlaceModel (orderNumber, uid,userModel.getUsername ( ), userModel.getPhone ( ), userModel.getAddress ( ),branch.getStorename (), String.valueOf (Total), String.valueOf (del), Timestamp.now ( ), lat, lon, "Pending", "cod","pending");
            FirebaseFirestore.getInstance ( )
                    .collection ("orders")
                    .document (orderNumber)
                    .set (orderPlaceModel).addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                        @Override
                        public void onSuccess(Void unused) {


                            new AlertDialog.Builder(NewCheckoutActivity.this)
                                    .setTitle("Order Successful")
                                    .setCancelable(false)
                                    .setMessage("Your order number is: " + orderNumber)
                                    .setPositiveButton("Track Order", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(NewCheckoutActivity.this, DeliveryDetailActivity.class);
                                            intent.putExtra ("orderNumber", orderNumber);
                                            intent.putExtra ("totalPrice", String.valueOf (Total));
                                            intent.putExtra ("address", userModel.getAddress ( ));
                                            intent.putExtra ("name", userModel.getUsername ( ));
                                            intent.putExtra ("storeLat", branch.getStoreLat ());
                                            intent.putExtra ("storeLon", branch.getStoreLon ());
                                            intent.putExtra ("payment", "Cash on delivery");
                                            intent.putExtra ("date", Timestamp.now ( ));
                                            startActivity(intent);
                                        }
                                    }).show();

                        }
                    });
            databaseReference= FirebaseDatabase.getInstance ().getReference ("partner/"+orderNumber);
            databaseReference.child ("partnerName").setValue ("Awaiting for acceptance");
            databaseReference.child ("partnerContact").setValue ("Order will procces soon!");
            databaseReference.child ("partnerId").setValue ("pending");
            databaseReference.child ("partnerLat").setValue(branch.getStoreLat ());
            databaseReference.child ("partnerLon").setValue(branch.getStoreLon ());
            databaseReference.child ("storeLat").setValue (branch.getStoreLat ());
            databaseReference.child ("storeLon").setValue (branch.getStoreLon ());
        }


        AppDatabase db = Room.databaseBuilder (getApplicationContext ( ),
                AppDatabase.class, "cart_db").allowMainThreadQueries ( ).build ( );
        ProductDao productDao = db.ProductDao ( );

        List<Product> products = productDao.getallproduct ( );
        for (int i = 0; i < products.size ( ); i++) {
            Product cart = products.get (i);

            cart.setPid (Integer.parseInt (orderNumber));
            FirebaseFirestore.getInstance ( )
                    .collection ("OrderProduct")
                    .document ( )
                    .set (cart)
                    .addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                        @Override
                        public void onSuccess(Void unused) {
                           dialog.cancel ();
                           db.clearAllTables ();


                        }
                    }).addOnFailureListener (new OnFailureListener ( ) {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setMessage ("Order failed");
                            progressBar.show ();
                        }
                    });








        };
    }


    public static int getRandomNumber(int min, int max) {
        return (new Random ( )).nextInt ((max - min) + 1) + min;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish ( );
        return super.onSupportNavigateUp ( );
    }




}