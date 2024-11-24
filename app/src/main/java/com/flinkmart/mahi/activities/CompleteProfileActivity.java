package com.flinkmart.mahi.activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ActivityCompleteProfileBinding;
import com.flinkmart.mahi.databinding.ActivityNewCheckoutBinding;
import com.flinkmart.mahi.map.LocationModel;
import com.flinkmart.mahi.map.MapActivity;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.model.UserModel1;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class CompleteProfileActivity extends AppCompatActivity {
    EditText usernameInput;
    EditText address;
    EditText pincode;
    EditText phoneNumber;
    Button CreateProfil;
    UserModel1 userModel;

    SupportMapFragment smf;
    FusedLocationProviderClient client;

    ActivityCompleteProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityCompleteProfileBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));

        phoneNumber = (findViewById (R.id.phone));
        usernameInput = findViewById (R.id.username);
        address = findViewById (R.id.address);
        pincode = findViewById (R.id.pin);
        CreateProfil = findViewById (R.id.createProfile);




        binding.textView20.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (CompleteProfileActivity.this, PrivacyActivity.class);
                startActivity (intent);
            }
        });
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

                getUsers();
                getmylocation();



    }

    void getUsers() {
        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    userModel = task.getResult ( ).toObject (UserModel1.class);
                    if (userModel != null){
                        Intent intent = new Intent (CompleteProfileActivity.this, MainActivity.class);
                        startActivity (intent);
                        phoneNumber.setText (userModel.getPhone ( ));
                        usernameInput.setText (userModel.getUsername ( ));
                        address.setText (userModel.getAddress ( ));
                        pincode.setText (userModel.getPin ());
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
                        binding.createProfile.setOnClickListener (new View.OnClickListener ( ) {
                            @Override
                            public void onClick(View v){
                                ProgressBar progressBar=findViewById (R.id.progressBar10);
                                CreateProfil = findViewById (R.id.createProfile);
                                String pin = pincode.getText ( ).toString ( ).trim ();
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


                                            }
                                        });

                                SharedPreferences sp=getSharedPreferences("location",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sp.edit();
                                editor.putString("latitude",latitude);
                                editor.putString("longitude",longitude);
                                editor.apply();

                                String phonenumber = phoneNumber.getText ( ).toString ( ).trim ();
                                String username = usernameInput.getText ( ).toString ( );
                                String addresss = address.getText ( ).toString ( );

                                if (username.isEmpty ( ) || username.length ( ) < 3) {
                                    usernameInput.setError ("Name should be >3 digit");
                                    return;
                                }
                                if (phonenumber.isEmpty ( ) || phonenumber.length ( ) < 10) {
                                    phoneNumber.setError ("Contact should be 10 digit");
                                    return;
                                }
                                if (addresss.isEmpty ( )) {
                                    address.setError ("Address Can't not be Empty");
                                    return;
                                }if (pin.isEmpty ( )||pin.length ( ) < 6) {
                                    pincode.setError ("Pin Can't not be Empty & lenth should 6 digit");
                                    return;
                                }
                                if (userModel != null){
                                    Intent intent = new Intent (CompleteProfileActivity.this, MainActivity.class);
                                    startActivity (intent);
                                    userModel.setPhone (phonenumber);
                                    userModel.setUsername (username);
                                    userModel.setAddress (addresss);
                                    userModel.setPin (pin);
                                    userModel.setCreatedTimestamp (Timestamp.now ( ));
                                } else {
                                    userModel = new UserModel1 (phonenumber,username, addresss, pin,Timestamp.now ( ));
                                }
                                FirebaseUtil.currentUserDetails ( ).set (userModel).addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task){
                                        if (task.isSuccessful ( )) {
                                            progressBar.setVisibility (View.VISIBLE);
                                            CreateProfil.setVisibility (View.GONE);
                                            Intent intent = new Intent (CompleteProfileActivity.this, BranchActivity.class);
                                            intent.putExtra("pincode", pin);
                                            startActivity (intent);
                                            finish();

                                        }
                                    }
                                });



                            }
                        });

                    }
                });
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish ( );
        return super.onSupportNavigateUp ( );
    }

    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder (CompleteProfileActivity.this);
        alertDialog.setTitle ("Data Safety");
        alertDialog.setMessage ("We use your data to deliver order");

        alertDialog.setPositiveButton ("Exit", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish ();
            }
        });

        alertDialog.setNeutralButton ("Understand", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ( );
            }
        });
        alertDialog.show ( );
    }


}


