package com.flinkmart.mahi.map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.activities.NewCheckoutActivity;
import com.flinkmart.mahi.interfaceformap.IOnloadLocationListener;
import com.flinkmart.mahi.interfaceformap.Latlong;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GeoQueryEventListener, IOnloadLocationListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private Location location;
    private LocationCallback locationCallback;

    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button btnFind;
    private RippleBackground rippleBg;

    private final float DEFAULT_ZOOM = 15;

    GeoFire geoFire;
    GeoQuery query;

    DatabaseReference databaseReference;
    List<LatLng> dengerousArea;
    Double distance;

    Marker marker;

    int radiusref=0;

    float radius = 0.5f;

    private boolean found = false;
    String storeId;

    private com.google.android.gms.location.LocationRequest locationRequest;

    private IOnloadLocationListener listener;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
//
        setContentView (R.layout.activity_map);

        materialSearchBar = findViewById (R.id.searchBar);
        btnFind = findViewById (R.id.btn_find);
        rippleBg = findViewById (R.id.ripple_bg);
        uid= FirebaseAuth.getInstance ( ).getUid ( );


        btnFind.setEnabled (false);
        btnFind.setText ("Finding nearby store");
        Places.initialize (MapActivity.this, getString (R.string.google_maps_api));
        placesClient = Places.createClient (this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance ( );

        Dexter.withContext (getApplicationContext ( )).withPermission (ACCESS_FINE_LOCATION)
                .withListener (new PermissionListener ( ) {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        rippleBg.startRippleAnimation ( );

//                        buildLocationRequist ( );

//                        buildLocationCallback ( );
//                        getDeviceLocation ( );
                        settingGeoFire ( );
                        initArea ( );
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest ( );
                    }
                }).check ( );


        materialSearchBar.setOnSearchActionListener (new MaterialSearchBar.OnSearchActionListener ( ) {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch (text.toString ( ), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    //opening or closing a navigation drawer
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {

                }
            }
        });

        materialSearchBar.addTextChangeListener (new TextWatcher ( ) {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder ( )
                        .setTypeFilter (TypeFilter.ADDRESS)
                        .setSessionToken (token)
                        .setQuery (s.toString ( ))
                        .build ( );
                placesClient.findAutocompletePredictions (predictionsRequest).addOnCompleteListener (new OnCompleteListener<FindAutocompletePredictionsResponse> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful ( )) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult ( );
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions ( );
                                List<String> suggestionsList = new ArrayList<> ( );
                                for (int i = 0; i < predictionList.size ( ); i++) {
                                    AutocompletePrediction prediction = predictionList.get (i);
                                    suggestionsList.add (prediction.getFullText (null).toString ( ));
                                }
                                materialSearchBar.updateLastSuggestions (suggestionsList);
                                if (!materialSearchBar.isSuggestionsVisible ( )) {
                                    materialSearchBar.showSuggestionsList ( );
                                }
                            }
                        } else {

                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setSuggestionsClickListener (new SuggestionsAdapter.OnItemViewClickListener ( ) {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size ( )) {
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get (position);
                String suggestion = materialSearchBar.getLastSuggestions ( ).get (position).toString ( );
                materialSearchBar.setText (suggestion);

                new Handler ( ).postDelayed (new Runnable ( ) {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions ( );
                    }
                }, 1000);
                InputMethodManager imm = (InputMethodManager) getSystemService (INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow (materialSearchBar.getWindowToken ( ), InputMethodManager.HIDE_IMPLICIT_ONLY);
                final String placeId = selectedPrediction.getPlaceId ( );
                List<Place.Field> placeFields = Arrays.asList (Place.Field.LAT_LNG);

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder (placeId, placeFields).build ( );
                placesClient.fetchPlace (fetchPlaceRequest).addOnSuccessListener (new OnSuccessListener<FetchPlaceResponse> ( ) {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace ( );
                        LatLng latLngOfPlace = place.getLatLng ( );
                        if (latLngOfPlace != null) {
                            mMap.moveCamera (CameraUpdateFactory.newLatLngZoom (latLngOfPlace, DEFAULT_ZOOM));
                        }
                    }
                }).addOnFailureListener (new OnFailureListener ( ) {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace ( );
                        }
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });
        btnFind.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                LatLng currentMarkerLocation = mMap.getCameraPosition ( ).target;
                rippleBg.startRippleAnimation ( );
                new Handler ( ).postDelayed (new Runnable ( ) {
                    @Override
                    public void run() {
                        rippleBg.stopRippleAnimation ( );
                        startActivity (new Intent (MapActivity.this, MainActivity.class));
                        finish ( );
                    }
                }, 1);

            }
        });
    }


    private void initArea() {
        listener = this;
        FirebaseDatabase.getInstance ( ).getReference ("storeLocation")
                .child ("location")
                .addListenerForSingleValueEvent (new ValueEventListener ( ) {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Latlong> latLngList = new ArrayList<> ( );

                        for (DataSnapshot dataSnapshot : snapshot.getChildren ( )) {
                            Latlong latLng = dataSnapshot.getValue (Latlong.class);
                            latLngList.add (latLng);

                        }
                        listener.onloadsucces (latLngList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onloadfaild (error.getMessage ( ));
                    }
                });


//        dengerousArea = new ArrayList<> ( );
//        dengerousArea.add (new LatLng (26.014903, 90.217047));
//        dengerousArea.add (new LatLng (26.031282, 90.308035));
//        dengerousArea.add (new LatLng (26.100170, 90.422537));
//        dengerousArea.add (new LatLng (26.021219, 90.400546));
//
//
//        FirebaseDatabase.getInstance ().getReference ("storeLocation").push ().setValue (dengerousArea);
//        FirebaseDatabase.getInstance ().getReference ("storeLocation").child ("location").setValue (dengerousArea);
    }
    private void settingGeoFire() {
        databaseReference = FirebaseDatabase.getInstance ( ).getReference ("storeLocation");
        geoFire = new GeoFire (databaseReference);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled (true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task = ((SettingsClient) settingsClient).checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(MapActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible())
                    materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSaveEnabled())
                    materialSearchBar.closeSearch();
                return false;
            }
        });
        addCircle();

    }
    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.getLastLocation ( )
                .addOnCompleteListener (new OnCompleteListener<Location> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful ( )) {
                            location = task.getResult ( );
                            if (location != null) {
                                geoFire.setLocation ("DeviceLocation", new GeoLocation (location.getLatitude ( ), location.getLongitude ( )), new GeoFire.CompletionListener ( ) {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {
                                        Double lattitude = location.getLatitude ( );
                                        Double longitude = location.getLongitude ( );

                                        mMap.moveCamera (CameraUpdateFactory.newLatLngZoom (new LatLng (location.getLatitude ( ), location.getLongitude ( )), DEFAULT_ZOOM));
                                        getClosestStore (lattitude, longitude);
                                        rippleBg.startRippleAnimation ( );


                                    }
                                });


                            } else {
                                final LocationRequest locationRequest = LocationRequest.create ( );
                                locationRequest.setInterval (10000);
                                locationRequest.setFastestInterval (5000);
                                locationRequest.setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback ( ) {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult (locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        location = locationResult.getLastLocation ( );
                                        mMap.moveCamera (CameraUpdateFactory.newLatLngZoom (new LatLng (location.getLatitude ( ), location.getLongitude ( )), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates (locationCallback);
                                    }
                                };
                                if (ActivityCompat.checkSelfPermission (getApplicationContext ( ), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (getApplicationContext ( ), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mFusedLocationProviderClient.requestLocationUpdates (locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText (MapActivity.this, "unable to get last location", Toast.LENGTH_SHORT).show ( );
                        }
                    }
                });
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback ( ) {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                if (mMap != null) {
                    Double lattitude = locationResult.getLastLocation ( ).getLatitude ( );
                    Double longitude = locationResult.getLastLocation ( ).getLongitude ( );
//                    userLocation(lattitude,longitude);
//                    getDeviceLocation ();
                    rippleBg.startRippleAnimation ( );
                    getClosestStore (lattitude, longitude);

                }

            }
        };
    }

    private void buildLocationRequist() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationRequest = new LocationRequest ( );
            locationRequest.setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval (5000);
            locationRequest.setFastestInterval (3000);
            locationRequest.setSmallestDisplacement (10f);
        }


    }

    private void getClosestStore(Double lattitude, Double longitude) {
        DatabaseReference nearestStore = FirebaseDatabase.getInstance ( ).getReference ( ).child ("geofirestore");
        GeoFire geoFire = new GeoFire (nearestStore);

        LatLng pick = new LatLng (lattitude, longitude);
//        mMap.addMarker (new MarkerOptions ().position (pick).title ("you are here"));

        GeoQuery geoQuery = geoFire.queryAtLocation (new GeoLocation (pick.latitude, pick.longitude), radius);
        geoQuery.removeAllListeners ( );
        geoQuery.addGeoQueryEventListener (new GeoQueryEventListener ( ) {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!found) {
                    found = true;
                    storeId = key;
                    rippleBg.stopRippleAnimation ( );
                    btnFind.setEnabled (true);


                    Double storeLat=location.latitude;
                    Double storeLon=location.longitude;
                    LatLng store=new LatLng(storeLat,storeLon);
                    calculateDistance(store,pick,storeId);
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!found) {
                    radius++;
                    getClosestStore (lattitude,longitude);
                    btnFind.setText (""+radius++);
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void calculateDistance(LatLng store, LatLng pick, String storeId) {

        databaseReference= FirebaseDatabase.getInstance ().getReference ("store/"+"voT4WYa4VNMQnYgFXlKVcIUeuEL2");
        databaseReference.addListenerForSingleValueEvent (new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                distance = SphericalUtil.computeDistanceBetween (pick, store);
                int fare = (int) (distance / 1000);
                btnFind.setText ("Continue with : " + storeId + " " + fare + " Km away");
                int circle = snapshot.child ("radius").getValue (Integer.class);
                radiusref = circle;


                if (fare > radiusref) {

//                  outofZone();
//                  btnFind.setVisibility (View.GONE);

                } else {
                    FirebaseFirestore.getInstance ( )
                            .collection ("users")
                            .document (uid)
                            .update ("store", storeId, "storeLat", store.latitude, "storeLon", store.longitude)
                            .addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                                @Override
                                public void onSuccess(Void unused) {

//                                SharedPreferences sp=context.getSharedPreferences("store",MODE_PRIVATE);
//                                SharedPreferences.Editor editor=sp.edit();
//
//                                editor.putString("StoreId",brance.getStoreuid ());
//                                editor.putString ("storeName",brance.getStorename ());
//                                editor.putString ("storeLat",brance.getStoreLat ());
//                                editor.putString ("storeLon",brance.getStoreLat ());
//                                editor.apply();

//                            Intent intent = new Intent (MapActivity.this, MainActivity.class);
//                            startActivity (intent);

                                }
                            });
                }

            }
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void outofZone() {
        MaterialAlertDialogBuilder alertDialog=new MaterialAlertDialogBuilder (MapActivity.this);
        alertDialog.setMessage ("We are sorry service not available in your location at this time.");
        alertDialog.setCancelable (false);
        alertDialog.create ();

        alertDialog.setPositiveButton ("Ok", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss ();
                onBackPressed ();

            }
        });
        alertDialog.show ();
    }


    private void addCircle() {
        for (LatLng latLng : dengerousArea){
            mMap.addCircle (new CircleOptions ( )
                    .center (latLng)
                    .radius (5000)
                    .strokeColor (getApplicationContext ( ).getResources ( ).getColor (R.color.fav))
                    .fillColor (0x220000FF)
                    .strokeWidth (1.0f)

            );


            MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("Address");
            mMap.addMarker(markerOptions);



            databaseReference = FirebaseDatabase.getInstance ( ).getReference ("storeLocation");
            geoFire = new GeoFire (databaseReference);


            GeoQuery geoQuery = geoFire.queryAtLocation (new GeoLocation (latLng.latitude, latLng.longitude), 2f);

            geoQuery.addGeoQueryEventListener (MapActivity.this);

        }

    }

    private void userLocation(Double lattitude, Double longitude) {

        LatLng latLng = new LatLng (lattitude, longitude);
        mMap.animateCamera (CameraUpdateFactory.newLatLngZoom (latLng, DEFAULT_ZOOM));

        geoFire.setLocation ("UserLocation", new GeoLocation (lattitude, longitude), new GeoFire.CompletionListener ( ) {
            @Override
            public void onComplete(String key, DatabaseError error) {

                if (marker != null) marker.remove ( );
                LatLng latLng = new LatLng (lattitude, longitude);
                mMap.animateCamera (CameraUpdateFactory.newLatLngZoom (latLng, DEFAULT_ZOOM));
            }
        });

    }
    @Override
    public void onKeyEntered(String key, GeoLocation location) {
    }

    @Override
    public void onKeyExited(String key) {

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {

    }

    public void onloadsucces(List<Latlong> latLngs) {
         dengerousArea=new ArrayList<> (  );
         for(Latlong latlong:latLngs){
             LatLng convert=new LatLng (latlong.getLatitude (),latlong.getLongitude ());
             dengerousArea.add (convert);
         }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient (MapActivity.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ( ).findFragmentById (R.id.map);
        mapFragment.getMapAsync (MapActivity.this);
        mapView = mapFragment.getView ( );
    }

    @Override
    public void onloadfaild(String message) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged (hasCapture);
    }
}