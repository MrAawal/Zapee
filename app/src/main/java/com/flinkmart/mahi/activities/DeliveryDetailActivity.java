package com.flinkmart.mahi.activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.ImageAdapter;
import com.flinkmart.mahi.adapter.OrderDetailAdapter;
import com.flinkmart.mahi.databinding.ActivityDeliveryDetailBinding;
import com.flinkmart.mahi.map.LocationModel;
import com.flinkmart.mahi.map.MapActivity;
import com.flinkmart.mahi.model.ImageModel;
import com.flinkmart.mahi.model.OrderDetails;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

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


        int orderNumber= Integer.parseInt (getIntent ().getStringExtra ("orderNumber"));
        String Totalprice=getIntent ().getStringExtra ("totalPrice");
        String address=getIntent ().getStringExtra ("address");
        String name=getIntent ().getStringExtra ("name");
        String storeLat=getIntent ().getStringExtra ("storeLat");
        String storeLon=getIntent ().getStringExtra ("storeLon");
        getProduct(orderNumber);
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
        binding.orderid.setText ("#"+orderNumber);
        binding.total.setText ("₹"+Totalprice);


        binding.button2.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (DeliveryDetailActivity.this, MainActivity.class);
                startActivity (intent);
                finish();

            }
        });

    }
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent (DeliveryDetailActivity.this, MainActivity.class);
        startActivity (intent);
        finish();
        return super.onSupportNavigateUp();
    }

    void getProduct(int orderNumber){
        getAllProduct (orderNumber);
        productadaper=new ImageAdapter (this)  ;
        binding.orderDetail.setAdapter (productadaper);
        binding.orderDetail.setLayoutManager (new LinearLayoutManager (this));

    }
    private void getAllProduct(int orderNumber){
        FirebaseFirestore.getInstance ()
                .collection ("OrderProduct")
               .whereEqualTo ("pid",orderNumber)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            binding.progressBar9.setVisibility (View.GONE);
                            ImageModel product=ds.toObject (ImageModel.class);
                            productadaper.addProduct(product);
                        }


                    }
                });
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

                        LatLng latLng2=new LatLng(location.getLatitude(),location.getLongitude());
                        MarkerOptions markerOptions2=new MarkerOptions().position(latLng2).title("Address");




                        if(googleMap!=null){
                            googleMap.clear ();
                        }

                        googleMap.addMarker (markerOptions2);

                        databaseReference= FirebaseDatabase.getInstance ().getReference ("partner/"+orderNumber);

                        databaseReference.addValueEventListener (new ValueEventListener ( ) {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String value1=snapshot.child ("partnerLat").getValue (String.class);
                                String value2=snapshot.child ("partnerLon").getValue (String.class);

                                LatLng latLng3=new LatLng(Double.parseDouble (value1), Double.parseDouble (value2));
                                MarkerOptions markerOptions3=new MarkerOptions().position(latLng3).title("Partner");

                                if(googleMap!=null){
                                    googleMap.clear ();
                                }
                                googleMap.addMarker (markerOptions3);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));

                        SharedPreferences sp=getSharedPreferences("location",MODE_PRIVATE);
                        if(sp.contains("latitude")) {
//                            Toast.makeText (DeliveryDetailActivity.this, ""+(sp.getString ("latitude","")), Toast.LENGTH_SHORT).show ( );
                        }




                    }
                });
            }
        });
    }
    public void onBackPressed() {
        super.onBackPressed ( );
        Intent intent = new Intent (DeliveryDetailActivity.this, MainActivity.class);
        startActivity (intent);

    }
}