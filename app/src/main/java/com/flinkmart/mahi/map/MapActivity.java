package com.flinkmart.mahi.map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.model.OrderPlaceModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.SphericalUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import  static  android.Manifest.permission.*;



public class MapActivity extends AppCompatActivity {
    SupportMapFragment smf;
    FusedLocationProviderClient client;
    Button button;
    Double distance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);

        button=findViewById (R.id.button4);

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

    }


    public void getmylocation() {
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
                        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("You are here...!!");



                        googleMap.addMarker(markerOptions);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

//                        distance = SphericalUtil.computeDistanceBetween (user, store);


                        String latitude= String.valueOf (location.getLatitude ());
                        String longitude=String.valueOf (location.getLongitude ());
                        String uid=FirebaseAuth.getInstance ( ).getUid ( );
                        LocationModel orderPlaceModel = new LocationModel(uid,latitude,longitude,Timestamp.now ());
                        FirebaseFirestore.getInstance ( )
                                .collection ("Location")
                                .document ()
                                .set (orderPlaceModel);
                        SharedPreferences sp=getSharedPreferences("location",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("latitude",latitude);
                        editor.putString("longitude",longitude);
                        editor.apply();
                        startActivity(new Intent (getApplicationContext (), MainActivity.class));
                    }
                });

                SharedPreferences sp=getSharedPreferences("location",MODE_PRIVATE);
                if(sp.contains("latitude"))
                {

//                    Toast.makeText (MapActivity.this, ""+(sp.getString ("latitude","")), Toast.LENGTH_SHORT).show ( );
                }

                String latitude= String.valueOf (location.getLatitude ());
                String longitude=String.valueOf (location.getLongitude ());
                String uid= FirebaseAuth.getInstance ( ).getUid ( );

                LocationModel orderPlaceModel = new LocationModel(uid,latitude,longitude,Timestamp.now ());
                FirebaseFirestore.getInstance ( )
                        .collection ("Location")
                        .document (uid)
                        .set (orderPlaceModel)
                        .addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                            @Override
                            public void onSuccess(Void unused) {
                                startActivity(new Intent (getApplicationContext (), MainActivity.class));
                            }
                        });



            }
        });
    }
}