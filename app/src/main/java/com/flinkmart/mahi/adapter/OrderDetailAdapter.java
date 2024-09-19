package com.flinkmart.mahi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemVieworderBinding;
import com.flinkmart.mahi.model.OrderDetail;
import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ProductDetailViewHolder>{


    Context context;
    ArrayList<OrderDetail> categories;

    public OrderDetailAdapter(Context context, ArrayList<OrderDetail> categories) {
        this.context = context;
        this.categories = categories;
    }


    @NonNull
    @Override
    public OrderDetailAdapter.ProductDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderDetailAdapter.ProductDetailViewHolder (LayoutInflater.from(context).inflate(R.layout.item_vieworder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.ProductDetailViewHolder holder, int position) {
        OrderDetail category = categories.get(position);
        holder.binding.pItem.setText (category.getProduct_name());
        holder.binding.Quantity.setText (category.getAmount ());
        holder.binding.prix.setText (category.getPrice_item ());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ProductDetailViewHolder extends RecyclerView.ViewHolder {
        ItemVieworderBinding binding;

        public ProductDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemVieworderBinding.bind(itemView);
        }
    }
}

