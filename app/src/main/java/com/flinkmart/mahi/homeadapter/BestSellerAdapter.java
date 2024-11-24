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
import com.flinkmart.mahi.databinding.ItemProductBinding;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.homemodel.BestSellerModel;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;

import java.util.List;

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.holder>{

    Context context;
    List<BestSellerModel> productItems;

    public BestSellerAdapter(Context context, List<BestSellerModel> productItems) {
        this.context = context;
        this.productItems = productItems;
    }




    @NonNull
    @Override
    public BestSellerAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bundle, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestSellerAdapter.holder holder, int position) {
        BestSellerModel newProductModel=productItems.get (position);
        holder.binding.label.setText (newProductModel.getTittle ().toUpperCase ());
        holder.binding.price.setText ("₹"+newProductModel.getPrice ());
        holder.binding.Discount.setText ("MRP:₹"+newProductModel.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with (context).load(newProductModel.getImage ())
                .into (holder.binding.image);
//        holder.binding.reduce.setVisibility (View.INVISIBLE);
//        holder.binding.add.setVisibility (View.INVISIBLE);
//        holder.binding.quantity.setVisibility (View.INVISIBLE);
//        holder.binding.cart.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                holder.binding.cart.setVisibility (View.INVISIBLE);
//                holder.binding.reduce.setVisibility (View.VISIBLE);
//                holder.binding.add.setVisibility (View.VISIBLE);
//                holder.binding.quantity.setVisibility (View.VISIBLE);
//            }
//        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewProductDetailActivity.class);
                intent.putExtra("id", newProductModel.getId());
                intent.putExtra("description",newProductModel.getDescription ());
                intent.putExtra("name", newProductModel.getTittle ());
                intent.putExtra("image", newProductModel.getImage());
                intent.putExtra("price", newProductModel.getPrice());
                intent.putExtra("subcategory",newProductModel.getSubcategory());
                intent.putExtra("discount",newProductModel.getDiscount ());
                intent.putExtra("category",newProductModel.getCategory());
                context.startActivity(intent);
            }
        });

        holder.binding.cart.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();
                List<Product> products=productDao.getallproduct ();

                int id= Integer.parseInt (newProductModel.getId ());

                Boolean check=productDao.is_exist(id);
                if(check==false) {
                    productDao.insertrecord (new Product (id,newProductModel.getTittle (),newProductModel.getImage (),Integer.parseInt (newProductModel.getPrice ()),1,newProductModel.getDiscount (),newProductModel.getDescription ()));
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
                int id= Integer.parseInt (newProductModel.getId ());
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

        int id= Integer.parseInt (newProductModel.getId ());
        Boolean check=productDao.is_exist(id);
        if(check==true){
            holder.binding.cart.setVisibility (View.INVISIBLE);
            holder.binding.remove.setVisibility (View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return productItems.size();
    }


    public void addProduct(BestSellerModel newProductModel) {
        productItems.add (newProductModel);
        notifyDataSetChanged ();
    }





    public class holder extends RecyclerView.ViewHolder{
        ItemBundleBinding binding;
        public holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemBundleBinding.bind(itemView);
        }
    }
}
