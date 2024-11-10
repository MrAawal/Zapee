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
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.databinding.ItemProductBinding;
import com.flinkmart.mahi.model.HorizonProductModel;

import java.util.List;

public class HorizonProductAdapter extends RecyclerView.Adapter<HorizonProductAdapter.LoanModelViewholder> {
    private Context context;
    private List<HorizonProductModel>productModels;

    public HorizonProductAdapter(Context context, List<HorizonProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
    }

    public void addProduct(HorizonProductModel productModel){
        productModels.add (productModel);
        notifyDataSetChanged ();
    }

    public void onBindViewHolder(@NonNull LoanModelViewholder holder, int position) {
        HorizonProductModel productModel=productModels.get (position);
        holder.binding.label.setText (productModel.getTittle ());
        holder.binding.price.setText (" ₹"+productModel.getPrice ());
        holder.binding.Discount.setText ("MRP:"+productModel.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with (context).load(productModel.getImage())
                .into (holder.binding.image);
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewProductDetailActivity.class);
                intent.putExtra("id", productModel.getId());
                intent.putExtra("description",productModel.getDescription ());
                intent.putExtra("name", productModel.getTittle ());
                intent.putExtra("image", productModel.getImage());
                intent.putExtra("price", productModel.getPrice());
                intent.putExtra("subcategory",productModel.getSubcategory());
                intent.putExtra("discount",productModel.getDiscount ());
                intent.putExtra("category",productModel.getCategory());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productModels.size ();
    }
    @NonNull
    @Override
    public LoanModelViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.item_product,parent,false);
        return new LoanModelViewholder (view);
    }


    class  LoanModelViewholder extends RecyclerView.ViewHolder{

        ItemProductBinding binding;
        public LoanModelViewholder(@NonNull View itemView) {
            super (itemView);
            binding = ItemProductBinding.bind(itemView);

      }
    }

}
