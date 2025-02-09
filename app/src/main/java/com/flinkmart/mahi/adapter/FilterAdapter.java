package com.flinkmart.mahi.adapter;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.databinding.CartSheetBinding;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Colour;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.model.Size;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyHolder> implements Filterable {

    Context context;

    CartModel cartItem;

    List<Item> modelList ;
    List<Item> modelListFilter;

    SizeAdapter sizeAdapter;
    ColorAdapter colorAdapter;
    String uid= FirebaseAuth.getInstance ( ).getUid ( );

    public FilterAdapter(Context context, List<Item> modelList) {
        this.context = context;
        this.modelList = modelList;
        this.modelListFilter = modelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newitem_product , parent , false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterAdapter.MyHolder holder, int position) {

        Item productModel=modelListFilter.get (position);
        holder.binding.label.setText (productModel.getTittle ());
        holder.binding.price.setText ("₹"+productModel.getPrice ());
        holder.binding.Discount.setText ("₹"+productModel.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);

        holder.binding.cart.setVisibility (View.VISIBLE);
        holder.binding.remove.setVisibility (View.INVISIBLE);
        holder.binding.qntLayout.setVisibility (View.INVISIBLE);


        Glide.with (context).load(productModel.getImage ())
                .into (holder.binding.image);
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



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
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



    }

    @Override
    public int getItemCount() {
        return modelListFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charcater = constraint.toString();
                if (charcater.isEmpty()){
                    modelListFilter = modelList ;
                }else{
                    List<Item> filterList = new ArrayList<>();
                    for (Item row: modelList){
                        if (row.getTittle ().toLowerCase().contains(charcater.toLowerCase())||row.getSubcategory().toLowerCase().contains(charcater.toLowerCase())||row.getCategory().toLowerCase().contains(charcater.toLowerCase())||row.getDescription().toLowerCase().contains(charcater.toLowerCase())){
                            filterList.add(row);
                        }
                    }
                    modelListFilter = filterList ;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = modelListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                modelListFilter = (ArrayList<Item>) results.values ;
                notifyDataSetChanged();
            }
        };
    }

    public void addProduct(Item productModel){
        modelListFilter.add (productModel);
        notifyDataSetChanged ();
    }


    class MyHolder extends RecyclerView.ViewHolder{

        NewitemProductBinding binding;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            binding=NewitemProductBinding.bind (itemView);
        }


    }







}