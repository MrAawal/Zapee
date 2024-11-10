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
import com.flinkmart.mahi.lifestyleactivity.ClothingListActivity;
import com.flinkmart.mahi.databinding.ItemLifestyleBinding;
import com.flinkmart.mahi.lifestylemodel.LifstyleCategory;

import java.util.List;

public class LifstyleAdapter extends RecyclerView.Adapter<LifstyleAdapter.holder> {

    Context context;
    List<LifstyleCategory> categoryItems;

    public LifstyleAdapter(Context context, List<LifstyleCategory> categoryItems) {
        this.context = context;
        this.categoryItems = categoryItems;
    }

    @NonNull
    @Override
    public LifstyleAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LifstyleAdapter.holder holder, int position) {
        LifstyleCategory catlist=categoryItems.get (position);
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


    public void addProduct(LifstyleCategory catlist) {
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
