package com.flinkmart.mahi.lifestyleadapter;

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
import com.flinkmart.mahi.activities.SubCatListActivity;
import com.flinkmart.mahi.databinding.ItemCategoryBinding;
import com.flinkmart.mahi.lifestyleactivity.ElectronicsListActivity;
import com.flinkmart.mahi.databinding.ItemLifestyleBinding;
import com.flinkmart.mahi.lifestylemodel.ElectronicCategory;

import java.util.List;

public class ElectronicAdapter extends RecyclerView.Adapter<ElectronicAdapter.holder> {

    Context context;
    List<ElectronicCategory> categoryItems;

    public ElectronicAdapter(Context context, List<ElectronicCategory> categoryItems) {
        this.context = context;
        this.categoryItems = categoryItems;
    }

    @NonNull
    @Override
    public ElectronicAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElectronicAdapter.holder holder, int position) {
        ElectronicCategory catlist=categoryItems.get (position);
        holder.binding.label.setText (catlist.getTittle ());
        Glide.with (context).load (catlist.getImage())
                .into (holder.binding.image);
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, AllProductActivity.class);
                intent.putExtra("catId",catlist.getId());
                intent.putExtra("catName",catlist.getTittle());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }


    public void addProduct(ElectronicCategory catlist) {
        categoryItems.add (catlist);
        notifyDataSetChanged ();
    }

    public class holder extends RecyclerView.ViewHolder{
        ItemCategoryBinding binding;
        public holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoryBinding.bind (itemView);
        }
    }
}
