package com.flinkmart.mahi.categoryAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.SubCatListActivity;
import com.flinkmart.mahi.databinding.ItemCategoryBinding;
import com.flinkmart.mahi.databinding.ItemCategoryLargeBinding;
import com.flinkmart.mahi.lifestyleactivity.FootwearListActivity;
import com.flinkmart.mahi.databinding.ItemLifestyleBinding;
import com.flinkmart.mahi.lifestylemodel.FootwearCategory;

import java.util.List;

public class CatAdapter5 extends RecyclerView.Adapter<CatAdapter5.holder> {

    Context context;
    List<FootwearCategory> categoryItems;

    public CatAdapter5(Context context, List<FootwearCategory> categoryItems) {
        this.context = context;
        this.categoryItems = categoryItems;
    }

    @NonNull
    @Override
    public CatAdapter5.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_large, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatAdapter5.holder holder, int position) {
        FootwearCategory catlist=categoryItems.get (position);
        holder.binding.label.setText (catlist.getTittle ());
        Glide.with (context).load (catlist.getImage())
                .into (holder.binding.image);
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, SubCatListActivity.class);
                intent.putExtra("category",catlist.getTittle());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }


    public void addProduct(FootwearCategory catlist) {
        categoryItems.add (catlist);
        notifyDataSetChanged ();
    }

    public class holder extends RecyclerView.ViewHolder{
        ItemCategoryLargeBinding binding;
        public holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoryLargeBinding.bind (itemView);
        }
    }
}