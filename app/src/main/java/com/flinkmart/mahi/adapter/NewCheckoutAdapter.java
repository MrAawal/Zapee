package com.flinkmart.mahi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemCartBinding;
import com.flinkmart.mahi.databinding.QuantityDialogBinding;
import com.flinkmart.mahi.model.ProductTiny;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

public class NewCheckoutAdapter extends RecyclerView.Adapter<NewCheckoutAdapter.CartViewHolder> {

    Context context;
    ArrayList<ProductTiny> products;
    CartListener cartListener;
    Cart cart;

    public interface CartListener {
        public void onQuantityChanged();
    }


    public NewCheckoutAdapter(Context context, ArrayList<ProductTiny> products, CartListener cartListener) {
        this.context = context;
        this.products = products;
        this.cartListener = cartListener;
        cart = TinyCartHelper.getCart();
    }
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_checkout, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductTiny product = products.get(position);
        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.image);

        holder.binding.name.setText(product.getName());
        holder.binding.price.setText("₹ " + product.getPrice());
        holder.binding.txtQty.setText(product.getQuantity() + " item(s)");
        holder.binding.DeleteButton.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                products.remove(position);
                cart.removeItem (product);
                notifyItemRemoved (position);
                notifyDataSetChanged();
                cartListener.onQuantityChanged ();

            }
        });


    }



    @Override
    public int getItemCount() {
        return products.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        ItemCartBinding binding;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);
        }
    }
}
