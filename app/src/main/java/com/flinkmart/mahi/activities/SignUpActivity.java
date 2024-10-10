package com.flinkmart.mahi.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

                if(user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                if(pass.isEmpty() || signupPassword.length ( ) < 6){
                    signupPassword.setError("Password Should Six Character");
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


                                        FirebaseUser user = auth.getCurrentUser();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpActivity.this, "Email format error or User Already Exist",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });


    }
}