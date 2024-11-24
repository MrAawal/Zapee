package com.flinkmart.mahi.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.adapter.BranchAdapter;
import com.flinkmart.mahi.databinding.ActivityBranchBinding;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.UserModel1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.UUID;

public class BranchActivity extends AppCompatActivity {
    ActivityBranchBinding binding;
    BranchAdapter branchAdapter;
    private static  List<Branch> branchemodel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding=ActivityBranchBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

        String pin = getIntent().getStringExtra("pincode");
        binding.textView29.setVisibility (View.INVISIBLE);
        Handler handler=new Handler (  );
        handler.postDelayed (()->{
           binding.textView29.setVisibility (View.VISIBLE);
           binding.progressBar8.setVisibility (View.INVISIBLE);
        },2000);


        getStore (pin);


        branchAdapter=new BranchAdapter (this) ;
        binding.branchList.setAdapter (branchAdapter);
        binding.branchList.setLayoutManager (new LinearLayoutManager (this));
        binding.button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
                String uid=FirebaseAuth.getInstance ( ).getUid ( );
                List<Branch>itemList=branchAdapter.getSelectedItem();

                for (int i=0;i<itemList.size ();i++){
                    Branch branch1 = itemList.get (i);
                    branch1.setUid (FirebaseAuth.getInstance ( ).getUid ( ));
                    FirebaseFirestore.getInstance ( )
                            .collection ("userstore")
                            .document (uid)
                            .set (branch1);

                    Intent intent = new Intent (BranchActivity.this, MainActivity.class);
                    intent.putExtra ("category",branch1.getStorename ());
                    startActivity (intent);

                    if(itemList.isEmpty ( )){
                        binding.button.setVisibility (View.INVISIBLE);
                        Toast.makeText (BranchActivity.this, "error", Toast.LENGTH_SHORT).show ( );
                    }

                }
            }
        });

    }




    void  getStore(String pin){
                FirebaseFirestore.getInstance ()
                .collection ("branch")
                .whereEqualTo ("pincode",pin)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            Branch loanModel=ds.toObject (Branch.class);
                            branchAdapter.addProduct (loanModel);
                        }

                    }
                });
    }

    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder (BranchActivity.this);
        alertDialog.setTitle ("Store Selected");
        alertDialog.setMessage ("Store select compulsory is deliver our product.");

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