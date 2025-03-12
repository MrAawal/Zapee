package com.flinkmart.mahi.storeadapter;

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
import com.flinkmart.mahi.model.Catlist;
import com.flinkmart.mahi.storeactivity.RestaurantSubCatListActivity;

import java.util.List;

public class StoreCategoryAdapter extends RecyclerView.Adapter<StoreCategoryAdapter.holder> {

    Context context;
    List<Catlist> categoryItems;

    public StoreCategoryAdapter(Context context, List<Catlist> categoryItems) {
        this.context = context;
        this.categoryItems = categoryItems;
    }

    @NonNull
    @Override
    public StoreCategoryAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreCategoryAdapter.holder holder, int position) {
        Catlist catlist=categoryItems.get (position);
        holder.binding.label.setText (catlist.getTittle ());
        Glide.with (context).load (catlist.getImage ())
                .into (holder.binding.image);
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RestaurantSubCatListActivity.class);
                intent.putExtra("category",catlist.getId ());
                intent.putExtra("categoryName",catlist.getTittle ());
                context.startActivity(intent);
            }
        });

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
        ItemCategoryBinding binding;
        public holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoryBinding.bind (itemView);
        }
    }
}
