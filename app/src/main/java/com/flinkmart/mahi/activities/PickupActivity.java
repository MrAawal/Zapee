package com.flinkmart.mahi.activities;

import static com.flinkmart.mahi.activities.NewCheckoutActivity.getRandomNumber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ActivityPickupBinding;
import com.flinkmart.mahi.databinding.ActivityProfileBinding;
import com.flinkmart.mahi.model.Courier;
import com.flinkmart.mahi.model.OrderPlaceModel;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.model.UserModel1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PickupActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button create;
    FirebaseUser user;

    UserModel userModel;

    ActivityPickupBinding binding;
    EditText daddress,dname,dpin,dcon;
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



        binding.button7.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (PickupActivity.this, MainActivity.class);
                intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity (intent);
                Toast.makeText (PickupActivity.this, "Your Order is succesfully place", Toast.LENGTH_SHORT).show ( );

                FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful ( )) {
                            userModel = task.getResult ( ).toObject (UserModel.class);
                            if (userModel != null) {
                                SharedPreferences sp = getSharedPreferences ("location", MODE_PRIVATE);
                                if (sp.contains ("latitude")) {
                                    String lat = sp.getString ("latitude", "");
                                    String lon = sp.getString ("longitude", "");

                                    dname = findViewById (R.id.dropName);
                                    dpin = findViewById (R.id.dropPin);
                                    dcon = findViewById (R.id.dropContact);
                                    daddress = findViewById (R.id.dropAddress);


                                    String orderNumber = String.valueOf (getRandomNumber (11111, 99999));
                                    String uid = FirebaseAuth.getInstance ( ).getUid ( );

                                    String dropAdd = daddress.getText ( ).toString ( );
                                    String dropPin = dpin.getText ( ).toString ( );
                                    String dropContact = dcon.getText ( ).toString ( );

                                    Courier orderPlaceModel = new Courier (uid,orderNumber, lon, lat, userModel.address, dropAdd, userModel.getPhone ( ), dropContact,userModel.getPin ( ),dropPin);
                                    FirebaseFirestore.getInstance ( )
                                            .collection ("courier")
                                            .document (orderNumber)
                                            .set (orderPlaceModel);
                                }
                            }
                        }

                    }

                });

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


}