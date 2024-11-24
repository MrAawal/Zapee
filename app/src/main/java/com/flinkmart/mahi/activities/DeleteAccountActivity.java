package com.flinkmart.mahi.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ActivityDeleteAccountBinding;
import com.flinkmart.mahi.databinding.ActivityPrivacyBinding;
import com.flinkmart.mahi.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class DeleteAccountActivity extends AppCompatActivity {
    EditText usernameInput;
    EditText address;
    EditText phoneNumber;
    Button CreateProfile;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_delete_account);
        phoneNumber = (findViewById (R.id.phone));
        usernameInput = findViewById (R.id.username);
        address = findViewById (R.id.address);
        CreateProfile = findViewById (R.id.createProfile);

        getUsers( );

        CreateProfile.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                setUsers ( );
            }
        });

    }

    void getUsers() {
        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    userModel = task.getResult ( ).toObject (UserModel.class);
                    if (userModel != null) {
                        phoneNumber.setText (userModel.getPhone ( ));
                        usernameInput.setText (userModel.getUsername ( ));
                        address.setText (userModel.getAddress ( ));
                    }

                }
            }
        });
    }

    void setUsers() {
        String phonenumber = phoneNumber.getText ( ).toString ( );
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
            address.setError ("Address Cant not be Empty");
            return;
        }
        if (userModel != null) {
            userModel.setPhone (phonenumber);
            userModel.setUsername (username);
            userModel.setAddress (addresss);
            userModel.setCreatedTimestamp (Timestamp.now ( ));
        } else {
            userModel = new UserModel ( "",phonenumber,username, addresss, Timestamp.now ( ));
        }
        FirebaseUtil.deleteUserDetail ( ).set (userModel).addOnCompleteListener (new OnCompleteListener<Void> ( ) {
            @Override
            public void onComplete(@NonNull Task<Void> task){
                if (task.isSuccessful ( )) {
                    Toast.makeText (DeleteAccountActivity.this, "Submited!Account will delete Within 7 days", Toast.LENGTH_SHORT).show ( );
                    Intent intent = new Intent (DeleteAccountActivity.this, MainActivity.class);
                    intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity (intent);
                }
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish ( );
        return super.onSupportNavigateUp ( );
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder (DeleteAccountActivity.this);
        alertDialog.setTitle ("Data Safety");
        alertDialog.setMessage ("We use your data to reach you and deliver our product.");
        alertDialog.setPositiveButton ("NO ! I DON'T WANT", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish ();
            }
        });
//        alertDialog.setNegativeButton ("OK", new DialogInterface.OnClickListener ( ) {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss ( );
//            }
//        });

        alertDialog.setNeutralButton ("Understand", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ( );
            }
        });
        alertDialog.show ( );
    }
}