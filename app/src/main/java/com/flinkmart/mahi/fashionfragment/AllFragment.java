package com.flinkmart.mahi.fashionfragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flinkmart.mahi.adapter.ItemAdapter;
import com.flinkmart.mahi.databinding.FragmentAllBinding;
import com.flinkmart.mahi.model.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllFragment extends Fragment {
    FragmentAllBinding binding;
    ItemAdapter dealListAdapter;
    List<Item> productLists=new ArrayList<> (  );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAllBinding.inflate (inflater, container, false);
        View view = binding.getRoot ( );
        setUpRecyclerView ();
        if(getArguments ()!=null){
            String cat=getArguments ().getString ("category");
            initDeals(cat);
        }else {
            Toast.makeText (getActivity (), "Arguments Not Found", Toast.LENGTH_SHORT).show ( );
        }



        return  view;
    }

    private void initDeals(String cat) {
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .whereEqualTo ("category","clothing")
//                .whereEqualTo ("subcategory","soap")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            Item productList = ds.toObject (Item.class);
                            dealListAdapter.addProduct(productList);
                        }

                    }
                });
    }

    private void setUpRecyclerView() {
        binding.DealsList.setLayoutManager (new GridLayoutManager (getActivity (),2));
        dealListAdapter=new ItemAdapter (getContext (),productLists);
        binding.DealsList.setAdapter (dealListAdapter);
    }

}