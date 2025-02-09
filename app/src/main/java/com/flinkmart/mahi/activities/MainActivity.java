package com.flinkmart.mahi.activities;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.Fragment.HomeFragment;
import com.flinkmart.mahi.Fragment.OrdersFragment;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.branchAdapter.BranchAdapter;
import com.flinkmart.mahi.databinding.ActivityMainBinding;
import com.flinkmart.mahi.databinding.QuantityDialogBinding;
import com.flinkmart.mahi.map.LocationModel;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.CartActivity;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
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
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private int cartQantity = 0;

    SupportMapFragment smf;
    FusedLocationProviderClient client;

    BranchAdapter branchAdapter;


    String[] permission = {"android.permission.POST_NOTIFICATIONS"};

    UserModel userModel;
    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding = ActivityMainBinding.inflate (getLayoutInflater ( ));
        setContentView (binding.getRoot ( ));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions (permission, 34);
        }

        getCurrenuUser ();

        binding.flot.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (getApplicationContext (), CartActivity.class));
            }
        });

        SharedPreferences sp = getSharedPreferences ("location", MODE_PRIVATE);
        if (sp.contains ("latitude")) {
//            Toast.makeText (this, "Sharing Current Location"+(sp.getString ("latitude","")), Toast.LENGTH_SHORT).show ( );
        } else {
//            startActivity (new Intent (getApplicationContext ( ), MapActivity.class));
        }

        FirebaseMessaging.getInstance ( ).subscribeToTopic ("discount")
                .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Done";
                        if (!task.isSuccessful ( )) {
                            msg = "Failed";
                        }
                    }
                });


        BottomNavigationView bottomNavigationView = findViewById (R.id.bottom_navigation);

        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge (R.id.orders);


        AppDatabase db = Room.databaseBuilder (getApplicationContext ( ),
                AppDatabase.class, "cart_db").allowMainThreadQueries ( ).build ( );
        ProductDao productDao = db.ProductDao ( );

        List<Product> products = productDao.getallproduct ( );

        if (products.size ( ) == 0) {
            badgeDrawable.setVisible (false);
        } else {
            int qty = 0, i;
            for (i = 0; i < products.size ( ); i++)
                qty = qty + (products.get (i).getQnt ( ));
            badgeDrawable.setVisible (true);
            badgeDrawable.setNumber (qty);


        }




        if (!isConnected ( )) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder (MainActivity.this);
            alertDialog.setTitle ("No Internet Connected");
            alertDialog.setMessage ("Please Connect to Internet");
            alertDialog.setPositiveButton ("Ok", new DialogInterface.OnClickListener ( ) {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish ( );
                }
            });

            alertDialog.show ( );
        } else {
        }


        loadFragment (new HomeFragment ( ));

        binding.bottomNavigation.setOnItemSelectedListener (new NavigationBarView.OnItemSelectedListener ( ) {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId ( );

                if (id == R.id.home) {
                    loadFragment (new HomeFragment ( ));
                    return true;
                } else if (id == R.id.orders) {

                    startActivity (new Intent (getApplicationContext ( ), CartActivity.class));
                    return true;
                } else if (id == R.id.category) {
                    loadFragment (new OrdersFragment ( ));
//                    loadFragment(new CategoryFragment());
                    return true;
                }
                return true;
            }
        });


    }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem cart=menu.findItem (R.id.home);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile) {
            startActivity(new Intent (this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager ( );
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction ( );
        fragmentTransaction.replace (R.id.container, fragment);
        fragmentTransaction.addToBackStack (null);
        fragmentTransaction.commit ( );

    }

    private void getCurrenuUser() {

        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    userModel = task.getResult ( ).toObject (UserModel.class);
                    if (userModel != null) {

                    } else {
                        completeProfile ( );
                    }
                }
            }
        });

    }


    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext ( ).getSystemService (getApplicationContext ( ).CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo ( ) != null && connectivityManager.getActiveNetworkInfo ( ).isConnectedOrConnecting ( );
    }

    public void onBackPressed() {
        super.onBackPressed ( );
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder (MainActivity.this);
        alertDialog.setTitle ("Exit");
        alertDialog.setMessage ("Do you want to exit");

        alertDialog.setPositiveButton ("No", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish ( );
            }
        });
        alertDialog.setNegativeButton ("Yes", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ( );
                finish ( );
            }
        });

    }

    public void completeProfile() {
        QuantityDialogBinding quantityDialogBinding = QuantityDialogBinding.inflate (LayoutInflater.from (this));

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder (this)
                .setView (quantityDialogBinding.getRoot ( ))
                .setCancelable (false)
                .create ( );

        EditText nameInput = quantityDialogBinding.username;
        EditText phoneInput = quantityDialogBinding.phone;
        EditText addressInput = quantityDialogBinding.address;
        EditText pinInput = quantityDialogBinding.pin;

        TextView lat=quantityDialogBinding.textView34;
        TextView lon=quantityDialogBinding.textView36;


        EditText locality = quantityDialogBinding.HeaderAddress;
        TextView autoaddress = quantityDialogBinding.autoAddress;
        EditText mappin = quantityDialogBinding.mapPin;
        EditText feature = quantityDialogBinding.featuresAddress;

        String uid = FirebaseAuth.getInstance ( ).getUid ( );

        FirebaseFirestore.getInstance ( ).collection ("users")
                .document (uid)
                .get ( )
                .addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful ( )) {
                            userModel = task.getResult ( ).toObject (UserModel.class);
                            if (userModel != null) {

                                phoneInput.setText (userModel.getPhone ( ));
                                nameInput.setText (userModel.getUsername ( ));
                                addressInput.setText (userModel.getAddress ( ));
                                pinInput.setText (userModel.getPin ( ));

//                                nameInput.setEnabled (false);
//                                addressInput.setEnabled (false);
//                                pinInput.setEnabled (false);
//                                phoneInput.setEnabled (false);

                            }

                        }
                    }
                });


        quantityDialogBinding.saveBtn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){

                String uid = FirebaseAuth.getInstance ( ).getUid ( );
                String phone = phoneInput.getText ( ).toString ( );
                String name = nameInput.getText ( ).toString ( );
                String address = addressInput.getText ( ).toString ( );
                String pin = pinInput.getText ( ).toString ( );


                String autoaddresses = autoaddress.getText ( ).toString ( );
                String local = locality.getText ( ).toString ( );
                String features =feature.getText ( ).toString ( );
                String mpin = mappin.getText ( ).toString ( );
                String latitude =lat.getText ( ).toString ( );
                String longitude = lon.getText ( ).toString ( );

                if (name.isEmpty ( ) || name.length ( ) < 3) {
                    nameInput.setError ("Name should be >3 digit");
                    return;
                }
                if (phone.isEmpty ( ) || phone.length ( ) < 10) {
                    phoneInput.setError ("Contact should be 10 digit");
                    return;
                }
                if (address.isEmpty ( )) {
                    addressInput.setError ("Address Can't not be Empty");
                    return;
                }
                if (pin.isEmpty ( ) || pin.length ( ) < 6) {
                    pinInput.setError ("Pin Can't not be Empty & lenth should 6 digit");
                    return;
                }


                UserModel userModel = new UserModel (phone, name, address, pin,autoaddresses,mpin,
                local,features,latitude,longitude, Timestamp.now ( ));
                FirebaseFirestore.getInstance ( )
                        .collection ("users")
                        .document (uid)
                        .set (userModel)
                        .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful ( )) {
                                    dialog.cancel ( );
                                }
                            }
                        });
            }
        });





        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);

        Dexter.withContext(this).withPermission(ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener () {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getmylocation(locality,autoaddress,mappin,feature,lat,lon,quantityDialogBinding);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable (android.R.color.transparent));
        dialog.show();
    }

    public void getmylocation(EditText locality, TextView autoaddress, EditText mappin, EditText feature, TextView lat, TextView lon, QuantityDialogBinding quantityDialogBinding) {
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
                        try {

                            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("You are here...!!");

                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));


                        String latitude= String.valueOf (location.getLatitude ());
                        String longitude=String.valueOf (location.getLongitude ());

                        String uid=FirebaseAuth.getInstance ( ).getUid ( );
                        lat.setText (latitude);
                        lon.setText (longitude);

                        getAddressFromLatLong (location,googleMap,mappin,locality,feature,autoaddress,lat,lon);

                        LocationModel orderPlaceModel = new LocationModel(uid,latitude,longitude,Timestamp.now ());
                        FirebaseFirestore.getInstance ( )
                                .collection ("Location")
                                .document (uid)
                                .set (orderPlaceModel)
                                .addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        SharedPreferences sp=getSharedPreferences("location",MODE_PRIVATE);
                                        SharedPreferences.Editor editor=sp.edit();
                                        editor.putString("latitude",latitude);
                                        editor.putString("longitude",longitude);
                                        editor.apply();

                                    }
                                });
                        }catch (Exception exception){
                         autoaddress.setText ("Gps issue try another location");
                         quantityDialogBinding.saveBtn.setEnabled (false);
                        }
                    }
                });
            }
        });
    }


     void getAddressFromLatLong(Location location, GoogleMap googleMap, EditText mappin, EditText locality, EditText feature, TextView autoaddress, TextView lat, TextView lon) {

         Geocoder geocoder=new Geocoder (MainActivity.this, Locale.getDefault ());
        try {
            List<Address> listAddress=geocoder.getFromLocation(location.getLatitude (), location.getLongitude (),4);
            if(listAddress.size ()>0){



                String alladress=listAddress.get (0).getAddressLine (0);
//                binding.HeaderAddress.setText (listAddress.get (0).getSubLocality ());
//                binding.featuresAddress.setText (listAddress.get (0).getFeatureName ());
//                binding.mapPin.setText ();

                autoaddress.setText (alladress);
                mappin.setText (listAddress.get (0).getPostalCode ());
                feature.setText (listAddress.get (0).getFeatureName ());
                locality.setText (listAddress.get (0).getSubLocality ());




            }
        } catch (IOException e){
            throw new RuntimeException (e);
        }
    }

}