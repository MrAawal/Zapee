package com.flinkmart.mahi.scrapadaper;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemFavouriteBinding;
import com.flinkmart.mahi.model.CartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.LoanModelViewholder> {
    private Context context;
    private static  List<CartModel>favouriteModels;
    String uid= FirebaseAuth.getInstance ( ).getUid ( );
    TextView Subtotal;

    private int cartqty=0;
    public CartAdapter(Context context, TextView Subtotal){
        this.context=context;
        this.Subtotal=Subtotal;
        favouriteModels=new ArrayList<>();
    }
    public void addProduct(CartModel favorite){
        favouriteModels.add (favorite);
        notifyDataSetChanged ();
    }
    public List<CartModel>getSelectedItems(){
        List<CartModel>cartItems=new ArrayList<> (  );
        for(int i=0;i<favouriteModels.size ();i++){
            if (favouriteModels.get (i).is_selected){
                cartItems.add (favouriteModels.get (i));
            }
        }
        return cartItems;
    }




    public void onBindViewHolder(@NonNull LoanModelViewholder holder, int position) {
        CartModel favourite=favouriteModels.get (position);
        holder.binding.label.setText (favourite.getName ());
        holder.binding.price.setText(String.valueOf ("₹"+favourite.getPrice ()+" * "+favourite.getQty ()+" Qty"));
        holder.binding.Discount.setText ("MRP:₹"+favourite.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.quantity.setText(String.valueOf (favourite.getQty()));
        Glide.with (context).load(favourite.getImage())
                .into (holder.binding.image);

        int sum=0,i;
        for(i=0;i< favouriteModels.size();i++)
            sum=sum+(favouriteModels.get(i).getPrice()*favouriteModels.get(i).getQty ());


        int qnty=0;
        for (CartModel favourite1:getSelectedItems ()){
            qnty+=favourite1.getQty ();
        }
        cartqty=qnty;

        Subtotal.setText (String.valueOf (cartqty));

        Subtotal.setVisibility (cartqty == 0 ? View.GONE:View.VISIBLE);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

            }
        });

        holder.binding.add.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                int quantity = favourite.getQty ();
                quantity++;

                favouriteModels.get(position).setQty (quantity);
                notifyItemChanged (quantity);
                notifyDataSetChanged();
                int finalQuantity = quantity;
                FirebaseFirestore.getInstance ()
                        .collection ("cart")
                        .document ( favourite.getId ()+uid)
                        .update("qty",quantity)
                        .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful ()){
                                    favouriteModels.get(position).setQty (finalQuantity);
                                    notifyItemChanged (finalQuantity);
                                    notifyDataSetChanged();
                                }
                            }
                        });

            }
        });
        holder.binding.reduce.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                int quantity = favourite.getQty ();
               if(quantity>1)
                quantity--;
                favouriteModels.get(position).setQty (quantity);
                notifyItemChanged (quantity);
                notifyDataSetChanged();
                int finalQuantity = quantity;
                FirebaseFirestore.getInstance ()
                        .collection ("cart")
                        .document ( favourite.getId ()+uid)
                        .update("qty",quantity)
                        .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful ()){
                                    favouriteModels.get(position).setQty (finalQuantity);
                                    notifyItemChanged (finalQuantity);
                                    notifyDataSetChanged();
                                    notifyDataSetChanged ();
                                }
                            }
                        });
            }
        });
        holder.binding.delete.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance ()
                        .collection ("cart")
                        .document ( favourite.getId ()+uid)
                        .delete ()
                        .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful ()){
                                    favouriteModels.remove (position);
                                    notifyItemRemoved (position);
                                    notifyDataSetChanged ();

                                }
                            }
                        });


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
        View view= LayoutInflater.from (context).inflate (R.layout.item_favourite,parent,false);
        return new LoanModelViewholder (view);
    }


    class  LoanModelViewholder extends RecyclerView.ViewHolder{

        ItemFavouriteBinding binding;
        public LoanModelViewholder(@NonNull View itemView) {
            super(itemView);
            binding = ItemFavouriteBinding.bind(itemView);
        }
    }

}