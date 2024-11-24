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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.activities.SearchProductDetailActivity;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SearchAdapter extends FirestoreRecyclerAdapter<Item,SearchAdapter.ItemViewHolder> {


    Context context;

    CartModel cartItem;
    String uid= FirebaseAuth.getInstance ( ).getUid ( );

    private List<Item>itemList;
    private List<Item>filteredList;

    public SearchAdapter(@NonNull FirestoreRecyclerOptions<Item> options, Context context, List<Item> itemList) {

        super (options);
        this.context = context;
        this.itemList = itemList;
    }

    public  void setFilteredList(List<Item>filtereddList){
        this.filteredList=itemList;
        notifyDataSetChanged ();
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {

        holder.binding.label.setText (model.getTittle ().toUpperCase());
        holder.binding.price.setText ("₹"+model.getPrice ());
        holder.binding.Discount.setText ("MRP:₹"+model.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.cart.setVisibility (View.VISIBLE);
        holder.binding.remove.setVisibility (View.INVISIBLE);
        Glide.with (context).load(model.getImage ())
                .into (holder.binding.image);
        holder.binding.cart.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();
                List<Product> products=productDao.getallproduct ();

                int id= Integer.parseInt (model.getId ());

                Boolean check=productDao.is_exist(id);
                if(check==false) {
                    productDao.insertrecord (new Product (id,model.getTittle (),model.getImage (),Integer.parseInt (model.getPrice ()),1,model.getDiscount (),model.getDescription ()));
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
                int id= Integer.parseInt (model.getId ());
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
                    String id = model.getId();
                    int price= Integer.parseInt (model.getPrice ());
                    int qntty=1;
                    CartModel orderProduct = new CartModel(id,orderNumber,uid,model.getTittle (),model.getImage (),model.getDiscount (),model.getStock (),model.getDescription (),model.getCategory (),model.getSubcategory (),"",qntty,price,true);
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
                String id = model.getId();
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
        int id= Integer.parseInt (model.getId ());
        Boolean check=productDao.is_exist(id);
        if(check==true){
            holder.binding.cart.setVisibility (View.INVISIBLE);
            holder.binding.remove.setVisibility (View.VISIBLE);
        }

        FirebaseUtil.favdetail (model.getId ()+uid).get ( ).addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
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
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
            Intent intent = new Intent(context, SearchProductDetailActivity.class);
            intent.putExtra("id", model.getId());
            intent.putExtra("description",model.getDescription ());
            intent.putExtra("name", model.getTittle ());
            intent.putExtra("image", model.getImage());
            intent.putExtra("price", model.getPrice());
            intent.putExtra("subcategory",model.getSubcategory());
            intent.putExtra("discount",model.getDiscount ());
            intent.putExtra("category",model.getCategory());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });




    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.newitem_product,parent,false);
        return new ItemViewHolder (view);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        NewitemProductBinding binding;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=NewitemProductBinding.bind (itemView);
        }
    }
}
