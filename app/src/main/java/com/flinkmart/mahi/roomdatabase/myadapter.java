package com.flinkmart.mahi.roomdatabase;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.Update;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemCartBinding;
import com.flinkmart.mahi.databinding.ItemCheckoutBinding;
import org.jetbrains.annotations.NotNull;
import java.util.List;
public class myadapter  extends RecyclerView.Adapter<myadapter.myviewholder>{
    List<ProductEntity> products;
    TextView rateview;
    public myadapter(List<ProductEntity> products, TextView rateview) {
        this.products = products;
        this.rateview= rateview;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull myadapter.myviewholder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.name.setText(products.get(position).getPname());
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
                notifyDataSetChanged();
                updateprice();
            }
        });

        holder.binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qnt=products.get(position).getQnt();
                qnt++;
                products.get(position).setQnt(qnt);
                notifyItemChanged (qnt);
                notifyDataSetChanged();
                updateprice();
                notifyDataSetChanged();

            }
        });

        holder.binding.btnReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int qnt=products.get(position).getQnt();
                if(qnt > 1)
                qnt--;
                products.get(position).setQnt(qnt);
                notifyItemChanged (qnt);
                notifyDataSetChanged();
                updateprice();

            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class myviewholder extends RecyclerView.ViewHolder{
        ItemCartBinding binding;

        public myviewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);
        }
    }

    @SuppressLint("SuspiciousIndentation")
    public void updateprice()
    {
        int sum=0,i;
        for(i=0;i< products.size();i++)
            sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());
           rateview.setText("₹"+sum);
    }
}
