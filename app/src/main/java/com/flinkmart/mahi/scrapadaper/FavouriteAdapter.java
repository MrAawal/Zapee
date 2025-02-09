package com.flinkmart.mahi.scrapadaper;
import static com.flinkmart.mahi.activities.NewCheckoutActivity.getRandomNumber;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Favourite;
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


public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.LoanModelViewholder> {
    private Context context;
    private static  List<Favourite>favouriteModels;

    CartModel cartItem;
    String uid= FirebaseAuth.getInstance ( ).getUid ( );
    TextView Subtotal;
    public FavouriteAdapter(Context context,TextView Subtotal){
        this.context=context;
        this.Subtotal=Subtotal;
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
        holder.binding.price.setText(String.valueOf ("₹"+favourite.getPrice ()+" * "+favourite.getQty ()+" Qty"));
        holder.binding.Discount.setText ("MRP:₹"+favourite.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
//        holder.binding.quantity.setText(String.valueOf (favourite.getQty()));
        Glide.with (context).load(favourite.getImage())
                .into (holder.binding.image);

        int sum=0,i;
        for(i=0;i< favouriteModels.size();i++)
            sum=sum+(favouriteModels.get(i).getPrice()*favouriteModels.get(i).getQty ());

        Subtotal.setText ("₹ "+sum);


        holder.binding.cart.setVisibility (View.VISIBLE);
        holder.binding.remove.setVisibility (View.INVISIBLE);
        Glide.with (context).load(favourite.getImage ())
                .into (holder.binding.image);
        holder.binding.cart.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();
                List<Product> products=productDao.getallproduct ();

                int id= Integer.parseInt (favourite.getId ());

                Boolean check=productDao.is_exist(id);
                if(check==false) {
                    productDao.insertrecord (new Product (id,favourite.getName (),favourite.getImage (),favourite.getPrice (),1,favourite.getDiscount (),favourite.getDescription ()));
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
                int id= Integer.parseInt (favourite.getId ());
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
                    String id = favourite.getId();
                    int price= favourite.getPrice ();
                    int qntty=1;
                    CartModel orderProduct = new CartModel(id,orderNumber,uid,favourite.getName (),favourite.getImage (),favourite.getDiscount (),favourite.getStock (),favourite.getDescription (),favourite.getCategory (),favourite.getSubcategory (),"",qntty,price,true);
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
                String id = favourite.getId();
                FirebaseFirestore.getInstance ()
                        .collection ("favourite")
                        .document ( id+uid)
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

        AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
        ProductDao productDao=db.ProductDao();
        List<Product> products=productDao.getallproduct ();
        int id= Integer.parseInt (favourite.getId ());
        Boolean check=productDao.is_exist(id);
        if(check==true){
            holder.binding.cart.setVisibility (View.INVISIBLE);
            holder.binding.remove.setVisibility (View.VISIBLE);
        }

        FirebaseUtil.favdetail (favourite.getId ()+uid).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
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
            public void onClick(View view){
                Intent intent = new Intent(context, NewProductDetailActivity.class);
                intent.putExtra("id", favourite.getId());
                intent.putExtra("description",favourite.getDescription ());
                intent.putExtra("name", favourite.getName ());
                intent.putExtra("image", favourite.getImage());
                intent.putExtra("price", favourite.getPrice());
                intent.putExtra("subcategory",favourite.getSubcategory());
                intent.putExtra("discount",favourite.getDiscount ());
                intent.putExtra("category",favourite.getCategory());
                context.startActivity(intent);
            }
        });

//        holder.binding.add.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                int quantity = favourite.getQty ();
//                quantity++;
//                favouriteModels.get(position).setQty (quantity);
//                notifyItemChanged (quantity);
//                notifyDataSetChanged();
//                int finalQuantity = quantity;
//                FirebaseFirestore.getInstance ()
//                        .collection ("favourite")
//                        .document ( favourite.getId ()+uid)
//                        .update("qty",quantity)
//                        .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful ()){
//                                    favouriteModels.get(position).setQty (finalQuantity);
//                                    notifyItemChanged (finalQuantity);
//                                    notifyDataSetChanged();
//                                }
//                            }
//                        });
//            }
//        });
//        holder.binding.reduce.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                int quantity = favourite.getQty ();
//               if(quantity>1)
//                quantity--;
//                favouriteModels.get(position).setQty (quantity);
//                notifyItemChanged (quantity);
//                notifyDataSetChanged();
//                notifyDataSetChanged();
//                int finalQuantity = quantity;
//                FirebaseFirestore.getInstance ()
//                        .collection ("favourite")
//                        .document ( favourite.getId ()+uid)
//                        .update("qty",quantity)
//                        .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful ()){
//                                    favouriteModels.get(position).setQty (finalQuantity);
//                                    notifyItemChanged (finalQuantity);
//                                    notifyDataSetChanged();
//                                }
//                            }
//                        });
//            }
//        });
//        holder.binding.delete.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                FirebaseFirestore.getInstance ()
//                        .collection ("favourite")
//                        .document ( favourite.getId ()+uid)
//                        .delete ()
//                        .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful ()){
//                                    favouriteModels.remove (position);
//                                    notifyItemRemoved (position);
//                                    notifyDataSetChanged ();
//
//                                }
//                            }
//                        });
//
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return favouriteModels.size ();
    }
    @NonNull
    @Override
    public LoanModelViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.newitem_product,parent,false);
//        View view= LayoutInflater.from (context).inflate (R.layout.item_favourite,parent,false);

        return new LoanModelViewholder (view);
    }


    public class  LoanModelViewholder extends RecyclerView.ViewHolder{

        NewitemProductBinding binding;
        public LoanModelViewholder(@NonNull View itemView) {
            super(itemView);
            binding = NewitemProductBinding.bind(itemView);
        }
    }


}