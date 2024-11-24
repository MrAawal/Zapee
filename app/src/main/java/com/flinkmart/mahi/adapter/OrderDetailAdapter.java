package com.flinkmart.mahi.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemOrderBinding;
import com.flinkmart.mahi.databinding.ItemVieworderBinding;
import com.flinkmart.mahi.model.Order;
import com.flinkmart.mahi.model.OrderDetails;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.LoanModelViewholder> {
    private Context context;
    private List<OrderDetails>ordersModel;
    public OrderDetailAdapter(Context context){
        this.context=context;
        ordersModel=new ArrayList<>();
    }
    public void addProduct(OrderDetails order){
        ordersModel.add (order);
        notifyDataSetChanged ();
    }


    public void onBindViewHolder(@NonNull LoanModelViewholder holder, int position) {
        OrderDetails order=ordersModel.get (position);
        holder.binding.name.setText (order.getName ());
        holder.binding.price.setText ("₹"+order.getPrice ());
        holder.binding.quantity.setText (order.getQty ()+"-Qty");
    }



    @Override
    public int getItemCount() {
        return ordersModel.size ();
    }
    @NonNull
    @Override
    public LoanModelViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.item_vieworder,parent,false);
        return new LoanModelViewholder (view);
    }


    class  LoanModelViewholder extends RecyclerView.ViewHolder{

        ItemVieworderBinding binding;
        public LoanModelViewholder(@NonNull View itemView) {
            super(itemView);
            binding = ItemVieworderBinding.bind(itemView);
        }
    }
}