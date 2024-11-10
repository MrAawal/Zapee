package com.flinkmart.mahi.fragmentgroccery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.FilterAdapter;
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
    FilterAdapter dealListAdapter;
    List<Item> productLists=new ArrayList<> (  );

    String[] item={"Dry fruit","Fresh fruit","Exotics","Organic"};

    ArrayAdapter<String> adapterItems;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAllBinding.inflate (inflater, container, false);
        View view = binding.getRoot ( );
        setUpRecyclerView ();
//        if(getArguments ()!=null){
//            String cat=getArguments ().getString ("category");

//        }else {
//            Toast.makeText (getActivity (), "Arguments Not Found", Toast.LENGTH_SHORT).show ( );
//        }

        initDeals();
        adapterItems = new ArrayAdapter<String> (getContext (), R.layout.list_item,item);

        binding.autoCompleteText.setAdapter(adapterItems);
        binding.autoCompleteText.setOnItemClickListener (new AdapterView.OnItemClickListener ( ) {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem=parent.getItemAtPosition (position).toString ();
                dealListAdapter.getFilter().filter(selectedItem);
                Toast.makeText (getActivity (), ""+selectedItem, Toast.LENGTH_SHORT).show ( );
            }
        });

        binding.SearchInput.setOnQueryTextListener (new SearchView.OnQueryTextListener ( ) {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                dealListAdapter.getFilter().filter(newText);
                binding.category.setMaxWidth (Integer.MAX_VALUE);
                return false;
            }
        });


        return  view;
    }

    private void initDeals() {
        FirebaseFirestore.getInstance ( )
                .collection ("product")
               .whereEqualTo ("category","footwear")
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
        dealListAdapter=new FilterAdapter (getContext (),productLists);
        binding.DealsList.setAdapter (dealListAdapter);
    }

}