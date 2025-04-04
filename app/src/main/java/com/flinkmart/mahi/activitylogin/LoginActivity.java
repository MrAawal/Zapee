package com.flinkmart.mahi.activitylogin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText loginEmail,loginPassword;
    private TextView signupRedirectText;
    private Button loginButton;
    private TextView phone;

    TextView forgotPassword;




    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i=new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
            finish ();
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_login);

        ProgressBar progressBar=findViewById (R.id.progressBar4);



        mAuth=FirebaseAuth.getInstance();
        loginEmail=findViewById(R.id.login_email);
        loginPassword=findViewById(R.id.login_password);
        loginButton=findViewById(R.id.login_button);
        signupRedirectText=findViewById(R.id.signupRedirectText);
        forgotPassword=findViewById(R.id.EmailLogin);
        phone=findViewById (R.id.imageButton4);
        phone.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PhoneLoginActivity.class));
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String user=loginEmail.getText().toString();
                String pass=loginPassword.getText().toString();
                progressBar.setVisibility (View.VISIBLE);
                loginButton.setVisibility (View.GONE);


                if(user.isEmpty()){
                    progressBar.setVisibility (View.INVISIBLE);
                    loginEmail.setError("Email cannot be empty");
                }
                if(pass.isEmpty()){
                    loginPassword.setError("Password cannot be empty");
                    progressBar.setVisibility (View.INVISIBLE);
                    loginButton.setVisibility (View.VISIBLE);
                }else {

                    mAuth.signInWithEmailAndPassword (user, pass)
                            .addOnCompleteListener (new OnCompleteListener<AuthResult> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful ( )) {
                                        Intent i = new Intent (getApplicationContext ( ), MainActivity.class);
                                        i.putExtra("storecate", "groccery");
                                        startActivity (i);
                                        finish ( );
                                    } else{
                                        progressBar.setVisibility (View.INVISIBLE);
                                        loginButton.setVisibility (View.VISIBLE);

                                        AlertDialog.Builder builder=new AlertDialog.Builder (LoginActivity.this);
                                        builder.setMessage ("Email format error or user not exist!");

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
        forgotPassword.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (getApplicationContext ( ), ForgotPasswordActivity.class);
                startActivity (i);
                finish ( );
            }
        });
    }

}