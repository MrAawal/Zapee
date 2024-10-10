package com.flinkmart.mahi.roomdatabase;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemCheckoutBinding;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class myadapter2 extends RecyclerView.Adapter<myadapter2.myviewholder>{
    List<ProductEntity> products;
    TextView rateview;
    public myadapter2(List<ProductEntity> products, TextView rateview) {
        this.products = products;
        this.rateview= rateview;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull myadapter2.myviewholder holder, @SuppressLint("RecyclerView") int position) {


        holder.binding.name.setText(products.get(position).getPname());
        holder.binding.price.setText(String.valueOf("₹ "+products.get(position).getPrice()));
        holder.binding.txtQty.setText(String.valueOf(products.get(position).getQnt()));
        holder.binding.DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                AppDatabase db = Room.databaseBuilder(holder.binding.id.getContext(),
                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();
                productDao.deleteById(products.get(position).getPid());
                products.remove(position);
                notifyItemRemoved(position);
                updateprice();
                notifyDataSetChanged();

            }
        });

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

    public void updateprice()
    {
        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());
           rateview.setText("₹"+sum);
    }

}
