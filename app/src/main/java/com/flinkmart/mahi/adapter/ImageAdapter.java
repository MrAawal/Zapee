package com.flinkmart.mahi.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemCategoriesBinding;
import com.flinkmart.mahi.databinding.ItemCategoryBinding;
import com.flinkmart.mahi.databinding.ItemFavouriteBinding;
import com.flinkmart.mahi.databinding.ItemImageBinding;
import com.flinkmart.mahi.model.Favourite;
import com.flinkmart.mahi.model.ImageModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.LoanModelViewholder> {
    private Context context;
    private static  List<ImageModel>favouriteModels;
    FirebaseAuth auth;
    public ImageAdapter(Context context){
        this.context=context;
        favouriteModels=new ArrayList<>();
    }
    public void addProduct(ImageModel favorite){
        favouriteModels.add (favorite);
        notifyDataSetChanged ();
    }


    public void onBindViewHolder(@NonNull FavouriteAdapter.LoanModelViewholder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return favouriteModels.size ();
    }
    @NonNull
    @Override
    public ImageAdapter.LoanModelViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.item_image,parent,false);
        return new ImageAdapter.LoanModelViewholder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanModelViewholder holder, int position) {
        ImageModel favourite=favouriteModels.get (position);
        holder.binding.label.setText (favourite.getName ());
        holder.binding.price.setText(String.valueOf ("₹"+favourite.getPrice ()+" * "+favourite.getQty ()+" Qty"));
        holder.binding.Discount.setText ("MRP:₹"+favourite.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with (context).load(favourite.getImage())
                .into (holder.binding.image);

    }


    class  LoanModelViewholder extends RecyclerView.ViewHolder{
        ItemImageBinding binding;
        public LoanModelViewholder(@NonNull View itemView) {
            super(itemView);
            binding = ItemImageBinding.bind(itemView);
        }
    }


}