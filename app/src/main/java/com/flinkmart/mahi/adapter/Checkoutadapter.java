package com.flinkmart.mahi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemCartBinding;
import com.flinkmart.mahi.databinding.ItemCheckoutBinding;
import com.flinkmart.mahi.databinding.QuantityDialogBinding;
import com.flinkmart.mahi.model.Product;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.flinkmart.mahi.roomdatabase.ProductEntity;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Checkoutadapter extends RecyclerView.Adapter<Checkoutadapter.myviewholder>{
    List<ProductEntity> products;
    TextView rateview;
    public Checkoutadapter(List<ProductEntity> products, TextView rateview) {
        this.products = products;
        this.rateview= rateview;
    }

    @NonNull
    @Override
    public Checkoutadapter.myviewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout,parent,false);
        return new Checkoutadapter.myviewholder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Checkoutadapter.myviewholder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.name.setText(products.get(position).getPname());
        holder.binding.price.setText("₹ "+products.get(position).getPrice());
        holder.binding.txtQty.setText(products.get(position).getQnt());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class myviewholder extends RecyclerView.ViewHolder{
        ItemCheckoutBinding binding;
        public myviewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = ItemCheckoutBinding.bind(itemView);
        }
    }
}