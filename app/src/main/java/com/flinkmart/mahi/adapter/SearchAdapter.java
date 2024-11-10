package com.flinkmart.mahi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.activities.SearchProductDetailActivity;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.model.Item;

import java.util.List;

public class SearchAdapter extends FirestoreRecyclerAdapter<Item,SearchAdapter.ItemViewHolder> {


    Context context;
    private List<Item>itemList;
    private List<Item>filteredList;

    public SearchAdapter(@NonNull FirestoreRecyclerOptions<Item> options, Context context, List<Item> itemList) {

        super (options);
        this.context = context;
        this.itemList = itemList;
    }

    public  void setFilteredList(List<Item>filtereddList){
        this.filteredList=itemList;
        notifyDataSetChanged ();
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {

        holder.binding.label.setText (model.getTittle ().toUpperCase());
        holder.binding.price.setText ("₹"+model.getPrice ());
        holder.binding.Discount.setText ("MRP:₹"+model.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with (context).load(model.getImage ())
                .into (holder.binding.image);
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
            Intent intent = new Intent(context, SearchProductDetailActivity.class);
            intent.putExtra("id", model.getId());
            intent.putExtra("description",model.getDescription ());
            intent.putExtra("name", model.getTittle ());
            intent.putExtra("image", model.getImage());
            intent.putExtra("price", model.getPrice());
            intent.putExtra("subcategory",model.getSubcategory());
            intent.putExtra("discount",model.getDiscount ());
            intent.putExtra("category",model.getCategory());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.newitem_product,parent,false);
        return new ItemViewHolder (view);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        NewitemProductBinding binding;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=NewitemProductBinding.bind (itemView);
        }
    }
}
