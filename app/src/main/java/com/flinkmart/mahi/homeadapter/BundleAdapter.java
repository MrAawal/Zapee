package com.flinkmart.mahi.homeadapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.databinding.ItemBundleBinding;
import com.flinkmart.mahi.model.HorizonProductModel;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;

import java.util.List;

public class BundleAdapter extends RecyclerView.Adapter<BundleAdapter.LoanModelViewholder> {
    private Context context;
    private List<HorizonProductModel>productModels;

    public BundleAdapter(Context context, List<HorizonProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
    }

    public void addProduct(HorizonProductModel productModel){
        productModels.add (productModel);
        notifyDataSetChanged ();
    }

    public void onBindViewHolder(@NonNull LoanModelViewholder holder, int position) {
        HorizonProductModel productModel=productModels.get (position);
        holder.binding.label.setText (productModel.getTittle ());
        holder.binding.price.setText ("₹"+productModel.getPrice ());
        holder.binding.Discount.setText ("MRP:"+productModel.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with (context).load(productModel.getImage())
                .into (holder.binding.image);
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewProductDetailActivity.class);
                intent.putExtra("id", productModel.getId());
                intent.putExtra("description",productModel.getDescription ());
                intent.putExtra("name", productModel.getTittle ());
                intent.putExtra("image", productModel.getImage());
                intent.putExtra("price", productModel.getPrice());
                intent.putExtra("subcategory",productModel.getSubcategory());
                intent.putExtra("discount",productModel.getDiscount ());
                intent.putExtra("category",productModel.getCategory());
                context.startActivity(intent);
            }
        });

        holder.binding.cart.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();
                List<Product> products=productDao.getallproduct ();

                int id= Integer.parseInt (productModel.getId ());

                Boolean check=productDao.is_exist(id);
                if(check==false) {
                    productDao.insertrecord (new Product (id,productModel.getTittle (),productModel.getImage (),Integer.parseInt (productModel.getPrice ()),1,productModel.getDiscount (),productModel.getDescription ()));
                }else {
                    Toast.makeText (context, "Item Exist", Toast.LENGTH_SHORT).show ( );
                }
                holder.binding.cart.setVisibility (View.INVISIBLE);
                holder.binding.remove.setVisibility (View.VISIBLE);

                holder.binding.qntLayout.setVisibility (View.INVISIBLE);

                holder.binding.add.setOnClickListener (new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View v) {

                        holder.binding.add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int qnt=products.get(position).getQnt();
                                qnt++;
                                products.get(position).setQnt(qnt);

                                int qty = 0, i;
                                for (i = 0; i < products.size ( ); i++)
                                    qty = qty + (products.get (i).getQnt ( ));
                                holder.binding.quantity.setText (""+qty);

                                notifyDataSetChanged();

                                AppDatabase db = Room.databaseBuilder(holder.binding.add.getContext(),
                                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                                ProductDao productDao = db.ProductDao();
                                productDao.update (qnt,products.get (position).getPid ());

                            }
                        });

                    }
                });

                holder.binding.reduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int qnt=products.get(position).getQnt();
                        qnt--;
                        products.get(position).setQnt(qnt);



                        int qty = 0, i;
                        for (i = 0; i < products.size ( ); i++)
                            qty = qty + (products.get (i).getQnt ( ));
                        holder.binding.quantity.setText (""+qty+" Items");

                        notifyDataSetChanged();

                        AppDatabase db = Room.databaseBuilder(holder.binding.reduce.getContext(),
                                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                        ProductDao productDao = db.ProductDao();
                        productDao.update (qnt,products.get (position).getPid ());

                    }
                });

            }
        });

        holder.binding.remove.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                int id= Integer.parseInt (productModel.getId ());
                AppDatabase db = Room.databaseBuilder(holder.binding.remove.getContext(),
                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();

                productDao.deleteById(id);
                holder.binding.remove.setVisibility (View.INVISIBLE);
                holder.binding.cart.setVisibility (View.VISIBLE);
            }
        });

        AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
        ProductDao productDao=db.ProductDao();

        int id= Integer.parseInt (productModel.getId ());
        Boolean check=productDao.is_exist(id);
        if(check==true){
            holder.binding.cart.setVisibility (View.INVISIBLE);
            holder.binding.remove.setVisibility (View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return productModels.size ();
    }
    @NonNull
    @Override
    public LoanModelViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.item_bundle,parent,false);
        return new LoanModelViewholder (view);
    }


    class  LoanModelViewholder extends RecyclerView.ViewHolder{

        ItemBundleBinding binding;
        public LoanModelViewholder(@NonNull View itemView) {
            super (itemView);
            binding = ItemBundleBinding.bind(itemView);

      }
    }

}
