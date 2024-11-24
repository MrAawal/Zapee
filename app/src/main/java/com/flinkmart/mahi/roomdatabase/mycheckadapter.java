package com.flinkmart.mahi.roomdatabase;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemCartBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class mycheckadapter extends RecyclerView.Adapter<mycheckadapter.myviewholder>{
    List<Product> products;
    TextView rateview,quantity;

    public mycheckadapter(List<Product> products, TextView rateview, TextView quantity) {
        this.products = products;
        this.rateview= rateview;
        this.quantity=quantity;
    }

    @NonNull
    @NotNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull mycheckadapter.myviewholder holder, int position) {

        holder.binding.label.setText(products.get(position).getPname());
        holder.binding.price.setText(String.valueOf(products.get(position).getPrice()));
        holder.binding.quantity.setText(String.valueOf(products.get(position).getQnt()));
        holder.binding.Discount.setText ("₹"+products.get (position).getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with (holder.binding.image.getContext ()).load(products.get (position).getImage())
                .into (holder.binding.image);
        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase db = Room.databaseBuilder(holder.binding.delete.getContext(),
                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();

                productDao.deleteById(products.get(position).getPid());
                products.remove(position);
                notifyItemRemoved(position);
                updateprice();
                updateQuantity ();
                notifyDataSetChanged ();

            }
        });

        holder.binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qnt=products.get(position).getQnt();
                qnt++;
                products.get(position).setQnt(qnt);
                notifyItemInserted (position);
                updateprice();
                updateQuantity();
                notifyDataSetChanged();
                AppDatabase db = Room.databaseBuilder(holder.binding.add.getContext(),
                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();
                productDao.update (qnt,products.get (position).getPid ());

            }
        });

        holder.binding.reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qnt=products.get(position).getQnt();
                qnt--;
                products.get(position).setQnt(qnt);

                notifyDataSetChanged();
                updateprice();
                updateQuantity ();

                AppDatabase db = Room.databaseBuilder(holder.binding.reduce.getContext(),
                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();
                productDao.update (qnt,products.get (position).getPid ());

            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class myviewholder extends RecyclerView.ViewHolder{
        ItemCartBinding binding;
        public myviewholder(@NonNull View itemView) {
            super (itemView);
            binding = ItemCartBinding.bind(itemView);

        }
    }


    public void updateprice()
    {
        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());

        rateview.setText("₹"+sum);
    }
    public void updateQuantity() {
        int qty = 0, i;
        for (i = 0; i < products.size ( ); i++)
            qty = qty + (products.get (i).getQnt ( ));

        quantity.setText (""+qty+" Items");

    }

}
