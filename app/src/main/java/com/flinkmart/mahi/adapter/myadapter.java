package com.flinkmart.mahi.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.cart.AppDatabase;
import com.flinkmart.mahi.cart.ProductDao;
import com.flinkmart.mahi.cart.ProductEntity;
import com.flinkmart.mahi.databinding.ItemCartBinding;
import com.flinkmart.mahi.databinding.QuantityDialogBinding;
import com.flinkmart.mahi.databinding.SinglerowdesignBinding;
import com.flinkmart.mahi.model.Product;

import org.jetbrains.annotations.NotNull;

import java.util.List;

    public class myadapter  extends RecyclerView.Adapter<myadapter.myviewholder>
    {
        List<ProductEntity> products;
        TextView rateview;
        public myadapter(List<ProductEntity> products, TextView rateview) {
            this.products = products;
            this.rateview= rateview;
        }
        @NonNull
        @NotNull
        @Override
        public myviewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowdesign,parent,false);
            return new myviewholder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull @NotNull myadapter.myviewholder holder, int position) {

            holder.binding.recid.setText(String.valueOf(products.get(position).getId ()));
            holder.binding.recpname.setText(products.get(position).getName ());
            holder.binding.recpprice.setText(String.valueOf("₹"+products.get(position).getPrice()));
            holder.binding.recqnt.setText(String.valueOf(products.get(position).getQuantity ()));
            holder.binding.delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppDatabase db = Room.databaseBuilder(holder.binding.recid.getContext(),
                            AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                    ProductDao productDao = db.ProductDao();

                    productDao.deleteById(products.get(position).getId ());
                    products.remove(position);
                    notifyItemRemoved(position);
                    updateprice();
                }
            });
            holder.binding.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qnt=products.get(position).getQuantity ();
                    qnt++;
                    products.get(position).setQuantity (qnt);
                    notifyDataSetChanged();
                    updateprice();
                }
            });

            holder.binding.minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qnt=products.get(position).getQuantity ();
                    if(qnt > 1)
                    qnt--;
                    products.get(position).setQuantity (qnt);
                    notifyDataSetChanged();
                    updateprice();
                }
            });

        }

        @Override
        public int getItemCount() {
            return products.size();
        }

       public class myviewholder extends RecyclerView.ViewHolder {

            SinglerowdesignBinding binding;
            public myviewholder(@NonNull View itemView) {
                super(itemView);
                binding = SinglerowdesignBinding.bind(itemView);
            }
        }


        public void updateprice()
        {
            int sum=0,i;
            for(i=0;i< products.size();i++)
                sum= (int) (sum+(products.get(i).getPrice()*products.get(i).getQuantity ()));

            rateview.setText("Total Amount : INR "+sum);
        }

    }
