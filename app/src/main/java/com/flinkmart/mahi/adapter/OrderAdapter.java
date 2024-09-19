package com.flinkmart.mahi.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.activities.OrderDetailActivity;
import com.flinkmart.mahi.activities.ProductDetailActivity;
import com.flinkmart.mahi.databinding.ItemOrderBinding;
import com.flinkmart.mahi.databinding.ItemProductBinding;
import com.flinkmart.mahi.databinding.QuantityDialogBinding;
import com.flinkmart.mahi.model.Order;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    Context context;
    ArrayList<Order> products;

    public OrderAdapter(Context context, ArrayList<Order> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        Order product = products.get(position);


        holder.binding.code.setText (product.getCode ());
        holder.binding.buyer.setText(product.getBuyer ());
        holder.binding.address.setText(product.getAddress ());
        holder.binding.status.setText(product.getStatus ());
        holder.binding.payStatus.setText(product.getPayment_status ());
        holder.binding.price.setText(product.getTotal_fees ());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("id", product.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        ItemOrderBinding binding;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemOrderBinding.bind(itemView);
        }
    }
}