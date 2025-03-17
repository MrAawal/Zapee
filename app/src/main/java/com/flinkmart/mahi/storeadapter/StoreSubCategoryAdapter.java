package com.flinkmart.mahi.storeadapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.adapter.FilterAdapter2;
import com.flinkmart.mahi.databinding.ItemCategoriesBinding;
import com.flinkmart.mahi.model.Catlist;
import com.flinkmart.mahi.model.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StoreSubCategoryAdapter extends RecyclerView.Adapter<StoreSubCategoryAdapter.holder> {
    Context context;
    List<Catlist> categoryItems;
    RecyclerView recyclerView1;
    StoreHomeProductAdapter categoryListAdapter;
    ArrayList<Item>itemList;

    TextView quantity;
    CardView layout;

    int row_index=-1;

    boolean select=true;

    private int checkedPosition=0;



    public StoreSubCategoryAdapter(Context context, List<Catlist> categoryItems, RecyclerView recyclerView1, StoreHomeProductAdapter categoryListAdapter, ArrayList<Item> itemList, TextView quantity, CardView layout) {
        this.context = context;
        this.categoryItems = categoryItems;
        this.recyclerView1=recyclerView1;
        this.categoryListAdapter=categoryListAdapter;
        this.itemList=itemList;
        this.quantity=quantity;
        this.layout=layout;
        notifyDataSetChanged ();

    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        Catlist catlist=categoryItems.get (position);
        holder.binding.label.setText (catlist.getTittle ());
        Glide.with (context).load (catlist.getImage ())
                .into (holder.binding.image);

//        final LinearLayoutManager linearLayoutManager=(LinearLayoutManager) recyclerView1.getLayoutManager ();
//        LinearLayoutManager layoutManager = new GridLayoutManager (context, 2);
//        recyclerView1.setLayoutManager (layoutManager);
//        categoryListAdapter = new FilterAdapter2 (context,itemList,quantity,layout);
//        recyclerView1.setAdapter(categoryListAdapter);
//
//
//
//
//        FirebaseFirestore.getInstance ()
//                .collection ("product")
//                .whereEqualTo ("subcategory", catlist.getId ())
//                .get ()
//                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
//                        for (DocumentSnapshot ds:dsList){
//                            Item product=ds.toObject (Item.class);
//                            categoryListAdapter.addProduct(product);
//                        }
//
//
//                    }
//                });

        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
//                categoryListAdapter.getFilter().filter(catlist.getId ());
//                holder.binding.container.setBackgroundColor (context.getResources ().getColor (R.color.fav));
//                new Handler ().postDelayed(new Runnable() {
//                    @Override
//                    public void run(){
//                        holder.binding.container.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
//                    }
//                }, 2500); // Delay in milliseconds
            }
        });

        holder.binding.container.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
               row_index=position;
               notifyDataSetChanged ();



//               CatListAdapter.this.notify ();
            }
        });

        if(row_index==position){
            holder.binding.container.setBackgroundResource (R.drawable.catb2);
            holder.binding.label.setTextColor (context.getResources ().getColor (R.color.white));

            final LinearLayoutManager linearLayoutManager=(LinearLayoutManager) recyclerView1.getLayoutManager ();
            LinearLayoutManager layoutManager = new GridLayoutManager (context, 2);
            recyclerView1.setLayoutManager (layoutManager);
            categoryListAdapter = new StoreHomeProductAdapter (context,itemList,quantity,layout);
            recyclerView1.setAdapter(categoryListAdapter);


            FirebaseFirestore.getInstance ()
                    .collection ("product")
                    .whereEqualTo ("show",true)
                    .whereEqualTo ("subcategory", catlist.getId ())
                    .get ()
                    .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                            for (DocumentSnapshot ds:dsList){
                                Item product=ds.toObject (Item.class);
                                categoryListAdapter.addProduct(product);
                            }


                        }
                    });
            categoryListAdapter.getFilter().filter(catlist.getId ());

        }else {
            holder.binding.container.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
            holder.binding.label.setTextColor (context.getResources ().getColor (R.color.black));
            holder.binding.label.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        }


    }


    @Override
    public int getItemCount() {
        return categoryItems.size();
    }


    public void addProduct(Catlist catlist) {
        categoryItems.add (catlist);
        notifyDataSetChanged ();
    }

    public class holder extends RecyclerView.ViewHolder{
        ItemCategoriesBinding binding;

        public holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoriesBinding.bind (itemView);
        }






    }
}
