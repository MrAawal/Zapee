package com.flinkmart.mahi.adapter;


import static com.flinkmart.mahi.activities.NewCheckoutActivity.getRandomNumber;

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
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryItemAdapter extends RecyclerView.Adapter<SubCategoryItemAdapter.LoanModelViewholder> {
    private Context context;
    private List<Item>productModels;

    CartModel cartItem;
    String uid= FirebaseAuth.getInstance ( ).getUid ( );
    public SubCategoryItemAdapter(Context context){
        this.context=context;
        productModels=new ArrayList<>();
    }



    public void addProduct(Item productModel){
        productModels.add (productModel);
        notifyDataSetChanged ();
    }



    public void onBindViewHolder(@NonNull LoanModelViewholder holder, int position) {

        Item productModel=productModels.get (position);
        holder.binding.label.setText (productModel.getTittle ());
        holder.binding.price.setText ("₹"+productModel.getPrice ());
        holder.binding.Discount.setText ("MRP:₹"+productModel.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);

        Glide.with (context).load(productModels.get (position).getImage ())
                .into (holder.binding.image);
        holder.binding.cart.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();
                List<Product> products=productDao.getallproduct ();

                int id= Integer.parseInt (productModels.get (position).getId ());

                Boolean check=productDao.is_exist(id);
                if(check==false) {
                    productDao.insertrecord (new Product (id,productModels.get (position).getTittle (),productModels.get (position).getImage (),Integer.parseInt (productModels.get (position).getPrice ()),1,productModels.get (position).getDiscount (),productModels.get (position).getDescription ()));
                }else {
                    Toast.makeText (context, "Item Exist", Toast.LENGTH_SHORT).show ( );
                }
                holder.binding.cart.setVisibility (View.INVISIBLE);
                holder.binding.remove.setVisibility (View.VISIBLE);
                holder.binding.remove.setBackgroundColor (context.getResources ().getColor (R.color.teal_700));

            }
        });

        holder.binding.remove.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                int id= Integer.parseInt (productModels.get (position).getId ());
                AppDatabase db = Room.databaseBuilder(holder.binding.remove.getContext(),
                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();

                productDao.deleteById(id);
                holder.binding.remove.setVisibility (View.INVISIBLE);
                holder.binding.cart.setVisibility (View.VISIBLE);

            }
        });

        holder.binding.heart2.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                String uid= FirebaseAuth.getInstance ( ).getUid ( );
                if(uid==null){
                    Toast.makeText (context, "Please Login", Toast.LENGTH_SHORT).show ( );
                }else{
                    String orderNumber = String.valueOf (getRandomNumber (11111, 99999));
                    String id = productModels.get (position).getId();
                    int price= Integer.parseInt (productModels.get (position).getPrice ());
                    int qntty=1;
                    CartModel orderProduct = new CartModel(id,orderNumber,uid,productModels.get (position).getTittle (),productModels.get (position).getImage (),productModels.get (position).getDiscount (),productModels.get (position).getStock (),productModels.get (position).getDescription (),productModels.get (position).getCategory (),productModels.get (position).getSubcategory (),"",qntty,price,true);
                    FirebaseFirestore.getInstance ( )
                            .collection ("favourite")
                            .document (id+uid)
                            .set (orderProduct);
                    holder.binding.heart2.setVisibility (View.INVISIBLE);
                    holder.binding.heart1.setVisibility (View.VISIBLE);
                    Toast.makeText (context, "Added in Favourite List", Toast.LENGTH_SHORT).show ( );
                }

            }
        });
        holder.binding.heart1.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                String id = productModels.get (position).getId();
                FirebaseFirestore.getInstance ()
                        .collection ("favourite")
                        .document ( id+uid)
                        .delete ()
                        .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful ()){
                                    holder.binding.heart1.setVisibility (View.INVISIBLE);
                                    holder.binding.heart2.setVisibility (View.VISIBLE);
//                                    Toast.makeText (context, "Item Remove", Toast.LENGTH_SHORT).show ( );
                                }
                            }
                        });

            }
        });

        AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
        ProductDao productDao=db.ProductDao();
        List<Product> products=productDao.getallproduct ();
        int id= Integer.parseInt (productModels.get (position).getId ());
        Boolean check=productDao.is_exist(id);
        if(check==true){
            holder.binding.cart.setVisibility (View.INVISIBLE);
            holder.binding.remove.setVisibility (View.VISIBLE);
        }

        FirebaseUtil.favdetail (productModels.get (position).getId ()+uid).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    cartItem = task.getResult ( ).toObject (CartModel.class);
                    if (cartItem!= null){
                        holder.binding.heart2.setVisibility (View.INVISIBLE);
                    }else{

                    }
                }
            }

        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        return productModels.size ();
    }
    @NonNull
    @Override
    public LoanModelViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.newitem_product,parent,false);
        return new LoanModelViewholder (view);
    }


    class  LoanModelViewholder extends RecyclerView.ViewHolder{

        NewitemProductBinding binding;
        public LoanModelViewholder(@NonNull View itemView) {
            super(itemView);
            binding = NewitemProductBinding.bind(itemView);
        }
    }
}