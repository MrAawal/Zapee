package com.flinkmart.mahi.restuarant;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.AllProductActivity;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.databinding.ItemCategoriesHorizontalBinding;
import com.flinkmart.mahi.model.Colour;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSubCategoryAdapter extends RecyclerView.Adapter<RestaurantSubCategoryAdapter.holder> {

    Context context;
    List<SubCatlistModel> categoryItems;

    int row_index=-1;



    public RestaurantSubCategoryAdapter(Context context) {
        this.context = context;
        this.categoryItems =new ArrayList<> ();
    }

    public void addProduct(SubCatlistModel branch){
        categoryItems.add (branch);
        notifyDataSetChanged ();
    }

    @NonNull
    @Override
    public RestaurantSubCategoryAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories_horizontal, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantSubCategoryAdapter.holder holder, int position) {
        SubCatlistModel catlist=categoryItems.get (position);
        holder.binding.label.setText (catlist.getTittle ());
        Glide.with (context).load (catlist.getImage ())
                .into (holder.binding.image);
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, RestaurantAllProductActivity.class);
                intent.putExtra("catId", catlist.getId());
                intent.putExtra("catName", catlist.getTittle ());
                context.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }


    public class holder extends RecyclerView.ViewHolder{
        ItemCategoriesHorizontalBinding binding;
        public holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoriesHorizontalBinding.bind (itemView);
        }
    }
}
