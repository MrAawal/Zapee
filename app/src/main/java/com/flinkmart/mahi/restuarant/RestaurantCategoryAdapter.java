package com.flinkmart.mahi.restuarant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemCategoriesHorizontalBinding;
import com.flinkmart.mahi.databinding.ItemCategoriesRestuarentBinding;
import com.flinkmart.mahi.model.Catlist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RestaurantCategoryAdapter extends RecyclerView.Adapter<RestaurantCategoryAdapter.holder> {

    Context context;
    List<Catlist> categoryItems;

    int row_index=-1;

    RecyclerView subcat;
    RestaurantSubCategoryAdapter subcatList;
    private  List<SubCatlistModel> subCatlistModels;

    ProgressBar progressBar;



    public RestaurantCategoryAdapter(Context context, List<Catlist> categoryItems, RecyclerView subcat, RestaurantSubCategoryAdapter subcatList, List<SubCatlistModel> subCatlistModels, ProgressBar progressBar) {
        this.context = context;
        this.categoryItems = categoryItems;
        this.subcatList=subcatList;
        this.subcat=subcat;
        this.subCatlistModels=subCatlistModels;
        this.progressBar=progressBar;
    }

    @NonNull
    @Override
    public RestaurantCategoryAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories_restuarent, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantCategoryAdapter.holder holder, int position) {
        Catlist catlist=categoryItems.get (position);
        holder.binding.label.setText (catlist.getTittle ());
        Glide.with (context).load (catlist.getImage ())
                .into (holder.binding.image);
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
                row_index=position;
                notifyDataSetChanged ();

                if (catlist.is_selected){
                    catlist.is_selected=false;

                }else {
                    catlist.is_selected=true;
                }
            }
        });


        if(row_index==position){
            progressBar.setVisibility (View.VISIBLE);
            holder.binding.label.setTextColor (context.getResources ().getColor (R.color.red));
            holder.binding.container.setBackgroundResource (R.drawable.catbg);
            subcatList=new RestaurantSubCategoryAdapter (context) ;
            LinearLayoutManager layoutManager1 =new GridLayoutManager (context,5);
            subcat.setLayoutManager (layoutManager1);
            subcat.setAdapter (subcatList);
            FirebaseFirestore.getInstance ()
                    .collection ("subcategory")
                    .whereEqualTo ("catname",catlist.getId ())
                    .get ()
                    .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                            for (DocumentSnapshot ds:dsList){
                                progressBar.setVisibility (View.INVISIBLE);
                                SubCatlistModel product=ds.toObject (SubCatlistModel.class);
                                subcatList.addProduct(product);
                            }


                        }
                    });
        }else {
            holder.binding.label.setTextColor (context.getResources ().getColor (R.color.black));
            holder.binding.container.setBackgroundColor (context.getResources ().getColor (R.color.white));
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
        ItemCategoriesRestuarentBinding binding;
        public holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoriesRestuarentBinding.bind (itemView);
        }
    }
}
