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
import com.flinkmart.mahi.activities.OrderDetailActivity;
import com.flinkmart.mahi.activities.ProductDetailActivity;
import com.flinkmart.mahi.databinding.ItemOrderBinding;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.model.NewProductModel;
import com.flinkmart.mahi.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.LoanModelViewholder> {
    private Context context;
    private List<Order>ordersModel;
    public OrdersAdapter(Context context){
        this.context=context;
        ordersModel=new ArrayList<>();
    }
    public void addProduct(Order order){
        ordersModel.add (order);
        notifyDataSetChanged ();
    }


    public void onBindViewHolder(@NonNull LoanModelViewholder holder, int position) {

        Order order=ordersModel.get (position);
        holder.binding.orderNumber.setText ("Order Id : "+order.getOrderNumber ());
        holder.binding.price.setText ("₹"+order.getTotalPrice ());
        holder.binding.status.setText (order.getStatus ());
        holder.binding.payStatus.setText (order.getPayment());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderNumber", order.getOrderNumber());
                intent.putExtra("totalPrice", order.getTotalPrice ());
                intent.putExtra("address", order.getCustomerAddress());
                intent.putExtra("payment", order.getPayment ());
                intent.putExtra("date", order.getOrderPlaceDate());
                intent.putExtra("status", order.getStatus());
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return ordersModel.size ();
    }
    @NonNull
    @Override
    public LoanModelViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.item_order,parent,false);
        return new LoanModelViewholder (view);
    }


    class  LoanModelViewholder extends RecyclerView.ViewHolder{

        ItemOrderBinding binding;
        public LoanModelViewholder(@NonNull View itemView) {
            super(itemView);
            binding = ItemOrderBinding.bind(itemView);
        }
    }
}