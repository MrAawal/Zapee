package com.flinkmart.mahi.activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flinkmart.mahi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail,signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            Intent i=new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
            finish ();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth=FirebaseAuth.getInstance();
        signupEmail=findViewById(R.id.username);
        signupPassword=findViewById(R.id.semail);
        signupButton=findViewById(R.id.signup_button);
        loginRedirectText=findViewById(R.id.bills);
        ProgressBar progressBar=findViewById (R.id.progressBar11);



        loginRedirectText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(i);
                finish ();

            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user=signupEmail.getText().toString();
                String pass=signupPassword.getText().toString();

                progressBar.setVisibility (View.VISIBLE);
                signupButton.setVisibility (View.GONE);

                if(user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                    progressBar.setVisibility (View.INVISIBLE);
                    signupButton.setVisibility (View.VISIBLE);
                }
                if(pass.isEmpty() || signupPassword.length ( ) < 6){
                    signupPassword.setError("Password Should Six Character");
                    progressBar.setVisibility (View.INVISIBLE);
                    signupButton.setVisibility (View.VISIBLE);
                }
                else{
                    auth.createUserWithEmailAndPassword(user,pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText (SignUpActivity.this, "User register Succes", Toast.LENGTH_SHORT).show ( );
                                        Intent i=new Intent(SignUpActivity.this, CompleteProfileActivity.class);
                                        startActivity(i);
                                        finish ();
                                        FirebaseUser user = auth.getCurrentUser();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        progressBar.setVisibility (View.INVISIBLE);
                                        signupButton.setVisibility (View.VISIBLE);

                                        AlertDialog.Builder builder=new AlertDialog.Builder (SignUpActivity.this);
                                        builder.setMessage ("Email format error or user already exist!");

                                        builder.setPositiveButton ("Ok", new DialogInterface.OnClickListener ( ) {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                builder.setCancelable (true);
                                            }
                                        });

                                        builder.show ();

                                    }
                                }
                            });

                }
            }
        });


    }
}