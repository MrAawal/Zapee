package com.flinkmart.mahi.activities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activitylogin.PhoneLoginActivity;
import com.flinkmart.mahi.map.MapActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class SplashActivity extends AppCompatActivity {

    public static int SPLASH_TIMER=3000;
    String user;
    SupportMapFragment smf;
    FusedLocationProviderClient client;

    LocationManager locationManager;

    CardView img1;
    TextView text;
    Animation top,bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature (Window.FEATURE_NO_TITLE);
//        this.getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);



        img1=(CardView) findViewById(R.id.cardView8);
        text=(TextView) findViewById(R.id.sublogo);

        top= AnimationUtils.loadAnimation(getApplicationContext(),R.animator.logo);
        bottom= AnimationUtils.loadAnimation(getApplicationContext(),R.animator.text);

        img1.setAnimation(top);
        text.setAnimation(bottom);


        locationManager= (LocationManager) getSystemService (Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER)){
            new Handler ().postDelayed(new Runnable() {
                @Override
                public void run() {
                    user=FirebaseAuth.getInstance ( ).getUid ( );
                    if (user!=null){
                        Intent i = new Intent (getApplicationContext ( ), MapActivity.class);
                        startActivity (i);
                        finish ();
                    }else{
                        startActivity(new Intent (getApplicationContext (), PhoneLoginActivity.class));
                        finish ();
                    }
                }
            }, SPLASH_TIMER); // Delay in milliseconds
        }else {
            showAlertMessageLocationDisabled();
        }

    }

    private void showAlertMessageLocationDisabled() {
        AlertDialog.Builder dialog=new AlertDialog.Builder (this);
        dialog.setTitle ("Location is off !");
        dialog.setMessage ("Please turn on location from settings and retry ");
        dialog.setPositiveButton ("Go to setting", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity (new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                finish ();
            }
        });

        dialog.setNegativeButton ("Not now", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed ();
                finish ();
            }
        });


        dialog.setCancelable (false);
        dialog.show ();

    }


    private void getPermition(){
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);
                Dexter.withContext(this).withPermission(ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener () {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        onBackPressed ();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();


    }


}