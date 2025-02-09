package com.flinkmart.mahi.activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.ImageAdapter;
import com.flinkmart.mahi.databinding.ActivityDeliveryDetailBinding;
import com.flinkmart.mahi.model.ImageModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeliveryDetailActivity extends AppCompatActivity {

    ActivityDeliveryDetailBinding binding;
    ImageAdapter productadaper;

    SupportMapFragment smf;
    FusedLocationProviderClient client;
    GeofencingClient geofencingClient;
    DatabaseReference databaseReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding= ActivityDeliveryDetailBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());


        long duration= TimeUnit.MINUTES.toMillis ( 30);
        new CountDownTimer (duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.textView16.setText (""+millisUntilFinished/60000);
            }

            @Override
            public void onFinish() {
             binding.textView16.setText ("Finished");
            }
        }.start ();



        int orderNumber= Integer.parseInt (getIntent ().getStringExtra ("orderNumber"));
        String Totalprice=getIntent ().getStringExtra ("totalPrice");
        String address=getIntent ().getStringExtra ("address");
        String name=getIntent ().getStringExtra ("name");
        String storeLat=getIntent ().getStringExtra ("storeLat");
        String storeLon=getIntent ().getStringExtra ("storeLon");

        getProduct (orderNumber);

        geofencingClient= LocationServices.getGeofencingClient (this );
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);
        Dexter.withContext(this).withPermission(ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener () {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getmylocation(orderNumber);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        getmylocation(orderNumber);

        databaseReference= FirebaseDatabase.getInstance ().getReference ("partner/"+orderNumber);

        databaseReference.addValueEventListener (new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value=snapshot.child ("partnerName").getValue (String.class)+"-"+snapshot.child ("partnerContact").getValue (String.class);
                binding.partner.setText (value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.address.setText ("Deliver to -"+name+"-"+address);
        binding.orderid.setText ("Order number #"+orderNumber);
        binding.total.setText ("Total payable amount: ₹"+Totalprice);


        binding.button2.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (DeliveryDetailActivity.this, MainActivity.class);
                startActivity (intent);
                finish();

            }
        });

    }

    void getProduct(Integer orderNumber){
        getAllProduct (orderNumber);
        productadaper=new ImageAdapter (this)  ;
        binding.orderList.setAdapter (productadaper);
        binding.orderList.setLayoutManager (new GridLayoutManager (this,3));;
    }
    private void getAllProduct(Integer orderNumber){
        FirebaseFirestore.getInstance ()
                .collection ("OrderProduct")
                .whereEqualTo ("pid",orderNumber)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            ImageModel product=ds.toObject (ImageModel.class);
                            productadaper.addProduct(product);
                        }


                    }
                });
    }
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent (DeliveryDetailActivity.this, MainActivity.class);
        startActivity (intent);
        finish();
        return super.onSupportNavigateUp();
    }


    public void getmylocation(int orderNumber) {


        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                        String storeLat=getIntent ().getStringExtra ("storeLat");
                        String storeLon=getIntent ().getStringExtra ("storeLon");

                        LatLng latLng=new LatLng(Double.parseDouble (storeLat), Double.parseDouble (storeLon));
                        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("Store");

//                        googleMap.addMarker (markerOptions);

                        SharedPreferences sp=getSharedPreferences("location",MODE_PRIVATE);

                        if(sp.contains("latitude")){
                            Double lat= Double.parseDouble (sp.getString ("latitude",""));
                            Double lon=Double.parseDouble (sp.getString ("longitude",""));
                            LatLng latLng2=new LatLng(lat,lon);
                            MarkerOptions markerOptions2=new MarkerOptions().position(latLng2).title("Your Address");
                            googleMap.addMarker (markerOptions2);
                            googleMap.animateCamera (CameraUpdateFactory.newLatLngZoom (latLng2,15));
                        }

//                        deliveryPartner(googleMap,orderNumber);
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));






                    }
                });
            }
        });
    }

    private void deliveryPartner(GoogleMap googleMap, int orderNumber) {
                                databaseReference= FirebaseDatabase.getInstance ().getReference ("partner/"+orderNumber);

                        databaseReference.addValueEventListener (new ValueEventListener ( ) {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String value1=snapshot.child ("partnerLat").getValue (String.class);
                                String value2=snapshot.child ("partnerLon").getValue (String.class);

                                LatLng latLng3=new LatLng(Double.parseDouble (value1), Double.parseDouble (value2));
//                                MarkerOptions markerOptions3=new MarkerOptions().position(latLng3).title("Store");

                                if(googleMap!=null){
                                    googleMap.clear ();
                                }
//                                googleMap.addMarker (markerOptions3);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
    }

    public void onBackPressed() {
        super.onBackPressed ( );
        Intent intent = new Intent (DeliveryDetailActivity.this, MainActivity.class);
        startActivity (intent);

    }
}