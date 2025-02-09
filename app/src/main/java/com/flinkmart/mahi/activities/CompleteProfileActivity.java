package com.flinkmart.mahi.activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.branchAdapter.BranchAdapter;
import com.flinkmart.mahi.databinding.ActivityCompleteProfileBinding;
import com.flinkmart.mahi.databinding.StoreDialogBinding;
import com.flinkmart.mahi.map.LocationModel;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.model.UserModel1;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class CompleteProfileActivity extends AppCompatActivity{
    EditText usernameInput;
    EditText address;

    BranchAdapter branchAdapter;
    EditText pincode;
    EditText phoneNumber;
    Button CreateProfil;
    UserModel userModel;

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



    }

    void getUsers(){
        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    userModel = task.getResult ( ).toObject (UserModel.class);
                    if (userModel != null){
                        phoneNumber.setText (userModel.getPhone ( ));
                        usernameInput.setText (userModel.getUsername ( ));
                        address.setText (userModel.getAddress ( ));
                        pincode.setText (userModel.getPin ());
//                        usernameInput.setEnabled (false);
//                        address.setEnabled (false);
//                        pincode.setEnabled (false);
                        phoneNumber.setEnabled (false);

//                        Intent intent = new Intent (CompleteProfileActivity.this, BranchGrocceryActivity.class);
//                        intent.putExtra("pincode", userModel.getPin ());
//                        startActivity (intent);
                    }

                }
            }
        });
    }


    public void getmylocation() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location> () {
            @Override
            public void onSuccess(final Location location) {
                smf.getMapAsync(new OnMapReadyCallback () {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        try{

                            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions markerOptions=new MarkerOptions()
                                    .position(latLng)
                                    .title("You are here...!!")
                                    .draggable (true);


                            if (googleMap != null) {
                                googleMap.clear ();
                            }

                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));



                            googleMap.setOnMarkerDragListener (new GoogleMap.OnMarkerDragListener ( ) {
                                @Override
                                public void onMarkerDrag(@NonNull Marker marker) {


                                }
                                @Override
                                public void onMarkerDragEnd(@NonNull Marker marker) {

                                }

                                @Override
                                public void onMarkerDragStart(@NonNull Marker marker) {

                                }
                            });

                            getAddressFromLatLong(location,googleMap);
                            circle(googleMap,latLng);
                            completeProfile(location);

                        }catch (Exception exception){
                                binding.createProfile.setEnabled (false);
                                binding.createProfile.setText ("GPS NOT FOUND");
                                binding.autoAddress.setText ("Refresh location on google map");
                        }


                    }
                });
            }
        });
    }

    private void completeProfile(Location location) {
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

                String mappin = binding.mapPin.getText ().toString ();
                String mapAddress = binding.autoAddress.getText ().toString ();
                String feature = binding.featuresAddress.getText ().toString ();
                String local = binding.HeaderAddress.getText ().toString ();

                String lat= String.valueOf (location.getLatitude ());
                String lon=String.valueOf (location.getLongitude ());


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
                    userModel.setPhone (phonenumber);
                    userModel.setUsername (username);
                    userModel.setAddress (addresss);
                    userModel.setPin (pin);
                    userModel.setCreatedTimestamp (Timestamp.now ( ));
                } else {

                }
                userModel = new UserModel (phonenumber,username,addresss, pin,mapAddress,mappin,local,feature,lat,lon,Timestamp.now ( ));
                FirebaseUtil.currentUserDetails ( )
                        .set (userModel)
                        .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                            @Override
                            public void onComplete(@NonNull Task<Void> task){
                                if (task.isSuccessful ( )) {
                                    progressBar.setVisibility (View.VISIBLE);
                                    CreateProfil.setVisibility (View.GONE);

                                    UpdateStore (pin);

                                }
                            }
                        });



            }
        });

    }

    private void getAddressFromLatLong(Location location, GoogleMap googleMap) {

        Geocoder geocoder=new Geocoder (CompleteProfileActivity.this, Locale.getDefault ());
        try {
            List<Address> listAddress=geocoder.getFromLocation(location.getLatitude (), location.getLongitude (),4);
            if(listAddress.size ()>0){

                String alladress=listAddress.get (0).getAddressLine (0);
                binding.autoAddress.setText (alladress);
                binding.HeaderAddress.setText (listAddress.get (0).getSubLocality ());
                binding.featuresAddress.setText (listAddress.get (0).getFeatureName ());
                binding.mapPin.setText (listAddress.get (0).getPostalCode ());



            }
        } catch (IOException e) {
            throw new RuntimeException (e);
        }
    }

    private void circle(GoogleMap googleMap, LatLng latLng) {
        CircleOptions circleOptions=new CircleOptions ();
        circleOptions.center (latLng);
        circleOptions.radius (100);
        circleOptions.strokeColor (getColor (R.color.red));
//                        circleOptions.fillColor (trans);
        circleOptions.strokeWidth (2);
        googleMap.addCircle (circleOptions);
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
                            binding.progressBar10.setVisibility (View.GONE);
                            binding.createProfile.setVisibility (View.VISIBLE);
                            Branch resturants=ds.toObject (Branch.class);
                            branchAdapter.addProduct (resturants);
                        }

                    }
                });
        dialog.show ();
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


