package com.flinkmart.mahi.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.CheckItemFavouriteBinding;
import com.flinkmart.mahi.databinding.ItemFavouriteBinding;
import com.flinkmart.mahi.model.Favourite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class CheckItemAdapter extends RecyclerView.Adapter<CheckItemAdapter.LoanModelViewholder> {
    private Context context;
    private static  List<Favourite>favouriteModels;

    public CheckItemAdapter(Context context){
        this.context=context;
        favouriteModels=new ArrayList<>();
    }
    public void addProduct(Favourite favorite){
        favouriteModels.add (favorite);
        notifyDataSetChanged ();
    }
    public List<Favourite>getSelectedItems(){
        List<Favourite>cartItems=new ArrayList<> (  );
        for(int i=0;i<favouriteModels.size ();i++){
            if (favouriteModels.get (i).is_selected){
                cartItems.add (favouriteModels.get (i));

            }
        }
        return cartItems;
    }




    public void onBindViewHolder(@NonNull LoanModelViewholder holder, int position) {
        Favourite favourite=favouriteModels.get (position);
        holder.binding.label.setText (favourite.getName ());
        holder.binding.price.setText(String.valueOf ("₹"+favourite.getPrice ()+" * " +favourite.getQty ()+" Qty"));
        holder.binding.Discount.setText ("MRP:₹"+favourite.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with (context).load(favourite.getImage())
                .into (holder.binding.image);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (favourite.is_selected){
                    favourite.is_selected=false;
                    holder.binding.cartItem.setBackgroundColor (context.getResources ().getColor (R.color.red));
                    Toast.makeText (context, "Deselected", Toast.LENGTH_SHORT).show ( );
                }else {
                    favourite.is_selected=true;
                    holder.binding.cartItem.setBackgroundColor (context.getResources ().getColor (R.color.fav));
                    Toast.makeText (context, "Reselected", Toast.LENGTH_SHORT).show ( );

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return favouriteModels.size ();
    }
    @NonNull
    @Override
    public LoanModelViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.check_item_favourite,parent,false);
        return new LoanModelViewholder (view);
    }


    class  LoanModelViewholder extends RecyclerView.ViewHolder{

        CheckItemFavouriteBinding binding;
        public LoanModelViewholder(@NonNull View itemView) {
            super(itemView);
            binding = CheckItemFavouriteBinding.bind(itemView);
        }
    }

}