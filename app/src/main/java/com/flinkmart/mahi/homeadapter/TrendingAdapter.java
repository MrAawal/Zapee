package com.flinkmart.mahi.homeadapter;

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
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.homemodel.TrendingModel;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.holder>{

    Context context;
    CartModel cartItem;

    List<Item> modelList ;
    List<Item> productItems;

    String uid= FirebaseAuth.getInstance ( ).getUid ( );

    public TrendingAdapter(Context context, List<Item> productItems) {
        this.context = context;
        this.modelList = modelList;
        this.productItems = productItems;
    }




    @NonNull
    @Override
    public TrendingAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newitem_product, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingAdapter.holder holder, int position) {

        Item newProductModel=productItems.get (position);
        holder.binding.label.setText (newProductModel.getTittle ().toUpperCase ());
        holder.binding.price.setText ("₹"+newProductModel.getPrice ());
        holder.binding.Discount.setText ("₹"+newProductModel.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);

        holder.binding.cart.setVisibility (View.VISIBLE);
        holder.binding.remove.setVisibility (View.INVISIBLE);

        Glide.with (context).load(newProductModel.getImage ())
                .into (holder.binding.image);

        holder.binding.cart.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();
                List<Product> products=productDao.getallproduct ();

                int id= Integer.parseInt (newProductModel.getId ());

                Boolean check=productDao.is_exist(id);
                if(check==false) {
                    productDao.insertrecord (new Product (id,newProductModel.getTittle (),newProductModel.getImage (),Integer.parseInt (newProductModel.getPrice ()),1,newProductModel.discount,newProductModel.description));
                }else {
                    Toast.makeText (context, "Item Exist", Toast.LENGTH_SHORT).show ( );
                }
                holder.binding.cart.setVisibility (View.INVISIBLE);
                holder.binding.remove.setVisibility (View.VISIBLE);

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
        holder.binding.heart2.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                String uid= FirebaseAuth.getInstance ( ).getUid ( );
                if(uid==null){
                    Toast.makeText (context, "Please Login", Toast.LENGTH_SHORT).show ( );
                }else{
                    String orderNumber = String.valueOf (getRandomNumber (11111, 99999));
                    String id = String.valueOf (newProductModel.getId ());
                    int price= Integer.parseInt (newProductModel.getPrice ());
                    int qntty=1;
                    CartModel orderProduct = new CartModel(id,orderNumber,uid,newProductModel.getTittle (),newProductModel.getImage (),newProductModel.getDiscount (),"",newProductModel.getDescription (),"","newProductModel.getSubcategory ()","",qntty,price,true);
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
                int id = Integer.parseInt (newProductModel.getId ());
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
        FirebaseUtil.favdetail (newProductModel.getId ()+uid).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
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


        AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
        ProductDao productDao=db.ProductDao();
        List<Product> products=productDao.getallproduct ();
        int id= Integer.parseInt (newProductModel.getId ());
        Boolean check=productDao.is_exist(id);
        if(check==true){
            holder.binding.cart.setVisibility (View.INVISIBLE);
            holder.binding.remove.setVisibility (View.VISIBLE);

        }
        FirebaseUtil.favdetail (newProductModel.getId ()+uid).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
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
                String idee= String.valueOf (newProductModel.getId ());

                Intent intent = new Intent(context, NewProductDetailActivity.class);
                intent.putExtra("id", idee);
                intent.putExtra("description",newProductModel.getDescription ());
                intent.putExtra("name", newProductModel.getTittle ());
                intent.putExtra("image", newProductModel.getImage());
                intent.putExtra("price", newProductModel.getPrice());
                intent.putExtra("subcategory",newProductModel.getSubcategory ());
                intent.putExtra("discount",newProductModel.getDiscount ());
                intent.putExtra("category",newProductModel.getCategory ());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productItems.size();
    }


    public void addProduct(Item newProductModel) {
        productItems.add (newProductModel);
        notifyDataSetChanged ();
    }

    public class holder extends RecyclerView.ViewHolder{
        NewitemProductBinding binding;
        public holder(@NonNull View itemView) {
            super(itemView);
            binding = NewitemProductBinding.bind(itemView);
        }
    }
}
