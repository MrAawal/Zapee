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
import com.flinkmart.mahi.lifestyleactivity.ClothingListActivity;
import com.flinkmart.mahi.categorymodel.CategoryModel4;
import com.flinkmart.mahi.databinding.ItemLifestyleBinding;

import java.util.List;

public class CatAdapter4 extends RecyclerView.Adapter<CatAdapter4.holder> {
    Context context;
    List<CategoryModel4> categoryItems;

    public CatAdapter4(Context context, List<CategoryModel4> categoryItems) {
        this.context = context;
        this.categoryItems = categoryItems;
    }

    @NonNull
    @Override
    public CatAdapter4.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lifestyle, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatAdapter4.holder holder, int position) {
        CategoryModel4 catlist=categoryItems.get (position);
        holder.binding.label.setText (catlist.getTittle ());
        Glide.with (context).load (catlist.getImage ())
                .into (holder.binding.image);
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClothingListActivity.class);
                intent.putExtra("category",catlist.getTittle());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }


    public void addProduct(CategoryModel4 catlist) {
        categoryItems.add (catlist);
        notifyDataSetChanged ();
    }

    public class holder extends RecyclerView.ViewHolder{
        ItemLifestyleBinding binding;
        public holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemLifestyleBinding.bind (itemView);
        }
    }
}