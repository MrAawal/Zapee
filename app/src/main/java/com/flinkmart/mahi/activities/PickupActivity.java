package com.flinkmart.mahi.activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.flinkmart.mahi.activities.NewCheckoutActivity.getRandomNumber;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activitylogin.LoginActivity;
import com.flinkmart.mahi.databinding.ActivityPickupBinding;
import com.flinkmart.mahi.model.Courier;
import com.flinkmart.mahi.model.UserModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class PickupActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button create;

    Button CreateProfil;
    FirebaseUser user;

    UserModel userModel;

    ActivityPickupBinding binding;
    EditText daddress,dname,dpin,dcon;

    SupportMapFragment smf;
    FusedLocationProviderClient client;

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding= ActivityPickupBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

        auth= FirebaseAuth.getInstance ();
        user=auth.getCurrentUser();

        if(user==null) {
            Intent i = new Intent (getApplicationContext ( ), LoginActivity.class);
            startActivity (i);
            finish ( );
        }else{
            getUsername();
        }



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

        getmylocation();


        binding.button7.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                proccesorder ( );
            }
        });


    }

    private void proccesorder() {

        ProgressDialog progressBar=new ProgressDialog (this);
        progressBar.setMessage ("Order processing... ");
        progressBar.show ();

        dname = findViewById (R.id.dropName);
        dpin = findViewById (R.id.dropPin);
        dcon = findViewById (R.id.dropContact);
        daddress = findViewById (R.id.dropAddress);

        String orderNumber = String.valueOf (getRandomNumber (11111, 99999));
        String uid = FirebaseAuth.getInstance ( ).getUid ( );
        String dropAdd = daddress.getText ( ).toString ( );
        String dropPin = dpin.getText ( ).toString ( );
        String dropContact = dcon.getText ( ).toString ( );
        String dropName = dname.getText ( ).toString ( );


        if (dropAdd.isEmpty ( )) {
            daddress.setError ("Name should not empty");
            progressBar.cancel ();
            return;
        }
        if (dropName.isEmpty ( )) {
            dname.setError ("Name should not empty");
            progressBar.cancel ();
            return;
        }
        if (dropContact.isEmpty ( ) || dropContact.length ( ) < 10) {
            dcon.setError ("Contact should be 10 digit");
            progressBar.cancel ();
            return;
        }
        if (dropPin.isEmpty ( )|| dropPin.length ( ) < 6){
            dpin.setError ("Pin should be 6 digit");
            progressBar.cancel ();
            return;
        }


        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    userModel = task.getResult ( ).toObject (UserModel.class);

                    if (userModel!=null){
                        SharedPreferences sp = getSharedPreferences ("location", MODE_PRIVATE);
                        if (sp.contains ("latitude")) {

                            String lat = sp.getString ("latitude", "");
                            String lon = sp.getString ("longitude", "");

                            Courier orderPlaceModel = new Courier (uid,orderNumber, lon, lat, userModel.address, dropAdd,dropName, userModel.getPhone ( ), dropContact,userModel.getPin ( ),dropPin);
                            FirebaseFirestore.getInstance ( )
                                    .collection ("courier")
                                    .document (orderNumber)
                                    .set (orderPlaceModel)
                                    .addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressBar.cancel ();
                                            Toast.makeText (PickupActivity.this, "Your Order is succesfully place", Toast.LENGTH_SHORT).show ( );
                                            Intent intent = new Intent (PickupActivity.this, DeliveryDetailActivity.class);
                                            intent.putExtra ("orderNumber", orderNumber);
                                            intent.putExtra ("totalPrice", " Pending");
                                            intent.putExtra ("address", userModel.getAddress ( ));
                                            intent.putExtra ("name", userModel.getUsername ( ));
                                            intent.putExtra ("storeLat", lat);
                                            intent.putExtra ("storeLon", lon);
                                            intent.putExtra ("payment", "Cash on delivery");
                                            intent.putExtra ("date",Timestamp.now ( ));
                                            startActivity (intent);
                                        }
                                    });

                            databaseReference= FirebaseDatabase.getInstance ().getReference ("partner/"+orderNumber);
                            databaseReference.child ("partnerName").setValue ("We will assign a partner soon ! ");
                            databaseReference.child ("partnerContact").setValue (" Please be patient !");
                            databaseReference.child ("partnerId").setValue ("pending");

                            databaseReference.child ("partnerLat").setValue(lat);
                            databaseReference.child ("partnerLon").setValue(lon);
                            databaseReference.child ("storeLat").setValue (lat);
                            databaseReference.child ("storeLon").setValue (lon);

                        }
                    }else {
                        Toast.makeText (PickupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show ( );
                    }
                }

            }

        });


    }

    void  getUsername(){
        FirebaseUtil.currentUserDetails ().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful ())  {
                    userModel=  task.getResult ().toObject (UserModel.class);
                    if(userModel!=null){
                        binding.contact.setText (userModel.getPhone ());
                        binding.name.setText (userModel.getUsername ());
                        binding.address.setText (userModel.getAddress ());
                        binding.email.setText (userModel.getPin ());
                    }
                }
            }
        });
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


                    }
                });
            }
        });
    }


}