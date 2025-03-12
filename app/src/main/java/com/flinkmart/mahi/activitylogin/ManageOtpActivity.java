package com.flinkmart.mahi.activitylogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.databinding.ActivityManageOtpBinding;
import com.flinkmart.mahi.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.concurrent.TimeUnit;

public class ManageOtpActivity extends AppCompatActivity {
    EditText t2;
    Button b2;
    Button back;
    String phonenumber;
    String otpid;
    FirebaseAuth mAuth;

    UserModel userModel;

    ActivityManageOtpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding= ActivityManageOtpBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());


        phonenumber=getIntent().getStringExtra("mobile").toString();
        t2=(EditText)findViewById(R.id.login_otp);
        b2=(Button)findViewById(R.id.login_next_btn);
        mAuth=FirebaseAuth.getInstance();



        initiateotp();


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(t2.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Blank Field can not be processed",Toast.LENGTH_LONG).show();
                else if(t2.getText().toString().length()!=6)
                    Toast.makeText(getApplicationContext(),"INvalid OTP",Toast.LENGTH_LONG).show();
                else
                {
                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(otpid,t2.getText().toString());
                    signInWithPhoneAuthCredential(credential);

                    binding.loginProgressBar.setVisibility (View.VISIBLE);


                }

            }
        });
    }

    private void initiateotp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
                {
                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken){
                        otpid=s;
                        binding.loginProgressBar.setVisibility (View.GONE);
                        binding.resendOtpTextview.setText ("OTP successfully send");
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
                    {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        binding.resendOtpTextview.setText (""+e.getMessage());

                    }
                });

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            binding.loginProgressBar.setVisibility (View.GONE);
                            getCurrenuUser();
                        } else {
                            binding.resendOtpTextview.setText ("Signin Code Error");
                            binding.loginProgressBar.setVisibility (View.GONE);

                        }
                    }
                });
    }
    private void getCurrenuUser() {
        FirebaseUtil.currentUserDetails ( ).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    userModel = task.getResult ( ).toObject (UserModel.class);
                    if (userModel != null) {
                        startActivity(new Intent (ManageOtpActivity.this, MainActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent (ManageOtpActivity.this, CompleteProfileActivity.class));
                        finish();
                    }
                }
            }
        });

    }


}