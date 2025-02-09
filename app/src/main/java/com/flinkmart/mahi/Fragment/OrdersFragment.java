package com.flinkmart.mahi.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.OrdersAdapter;
import com.flinkmart.mahi.databinding.FragmentOrdersBinding;
import com.flinkmart.mahi.model.Order;
import com.flinkmart.mahi.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class OrdersFragment extends Fragment {

FragmentOrdersBinding binding;
OrdersAdapter ordersListAdapter;

TextView text;
ProgressDialog progressDialog;

UserModel userModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        text=view.findViewById (R.id.textView);
        text.setVisibility (View.GONE);

        setUpRecyclerView(text);
        initCategory ();

        Handler handler=new Handler (  );
        handler.postDelayed (()->{
            text.setVisibility (View.VISIBLE);
            binding.progressBar12.setVisibility (View.INVISIBLE);
        },1000);

       getUsername ();

        progressDialog = new ProgressDialog (getActivity ());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        binding.progressBar12.setVisibility (View.VISIBLE);


        return  view;

    }

    private void initCategory() {
        String uid= FirebaseAuth.getInstance ( ).getUid ( );
        FirebaseFirestore.getInstance ( )
                .collection ("orders")
                .whereEqualTo ("uid",uid)
                .limit (20)
                .orderBy ("orderPlaceDate", Query.Direction.DESCENDING)
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList){
                            text.setText ("");
                            binding.progressBar12.setVisibility (View.INVISIBLE);
                            Order order = ds.toObject (Order.class);
                            ordersListAdapter.addProduct (order);
                        }

                    }
                });
    }
    private void setUpRecyclerView(TextView text) {
        //  orders
        binding.orders.setLayoutManager (new LinearLayoutManager (getContext ()));
        ordersListAdapter=new OrdersAdapter (getContext (),text);
        binding.orders.setAdapter (ordersListAdapter);

    }

    void  getUsername(){
        FirebaseUtil.currentUserDetails ().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful ())  {

                    userModel=  task.getResult ().toObject (UserModel.class);
                    if(userModel!=null){
                        binding.textView4.setText ("Hi "+userModel.getUsername ());
                        binding.textView11.setText (userModel.getAddress ());

                    }
                }
            }
        });
    }
}