package com.flinkmart.mahi.roomdatabase;

import static com.flinkmart.mahi.R.color.*;

import android.graphics.Paint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemCartBinding;
import com.flinkmart.mahi.model.BranchModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class myadapter  extends RecyclerView.Adapter<myadapter.myviewholder>{
    List<Product> products;
    TextView rateview,quantity,text,banner;


    CardView cardView;

    ImageView imageView;

    int minAmount=0;

    BranchModel branch;

    DatabaseReference databaseReference;


    public myadapter(List<Product> products, TextView rateview, TextView quantity, TextView text, TextView banner, CardView cardView, ImageView imageView) {
        this.products = products;
        this.rateview= rateview;
        this.text=text;
        this.quantity=quantity;
        this.banner=banner;
        this.cardView=cardView;
        this.imageView=imageView;
    }

    @NonNull
    @NotNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull myadapter.myviewholder holder, int position) {

        holder.binding.label.setText(products.get(position).getPname());
        holder.binding.price.setText("₹"+String.valueOf(products.get(position).getPrice()));
        holder.binding.quantity.setText(String.valueOf(products.get(position).getQnt()));
        holder.binding.Discount.setText ("₹"+products.get (position).getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with (holder.binding.image.getContext ()).load(products.get (position).getImage())
                .into (holder.binding.image);
        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase db = Room.databaseBuilder(holder.binding.delete.getContext(),
                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();

                productDao.deleteById(products.get(position).getPid());
                products.remove(position);
                notifyItemRemoved(position);
                updateprice();
                updateQuantity ();
                notifyDataSetChanged ();

            }
        });

        holder.binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qnt=products.get(position).getQnt();
                qnt++;
                products.get(position).setQnt(qnt);
                notifyItemInserted (position);
                updateprice();
                updateQuantity();
                updateCart ();

                notifyDataSetChanged();

                AppDatabase db = Room.databaseBuilder(holder.binding.add.getContext(),
                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();
                productDao.update (qnt,products.get (position).getPid ());

            }
        });

        holder.binding.reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qnt=products.get(position).getQnt();
                if(qnt>1)
                qnt--;
                products.get(position).setQnt(qnt);

                notifyDataSetChanged();
                updateprice();
                updateQuantity ();
                updateCart ();;

                AppDatabase db = Room.databaseBuilder(holder.binding.reduce.getContext(),
                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();
                productDao.update (qnt,products.get (position).getPid ());

            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class myviewholder extends RecyclerView.ViewHolder{
        ItemCartBinding binding;
        public myviewholder(@NonNull View itemView) {
            super (itemView);
            binding = ItemCartBinding.bind(itemView);

        }
    }


    public void updateprice()
    {
        FirebaseUtil.currentUserStore( ).get().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful ( )) {
                    branch = task.getResult ( ).toObject (BranchModel.class);
                    if(branch!=null){
                            databaseReference= FirebaseDatabase.getInstance ().getReference ("store/"+branch.getStoreuid ());
                            databaseReference.addListenerForSingleValueEvent (new ValueEventListener ( ) {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int minAmount=snapshot.child ("minimum").getValue (Integer.class);


                                    int sum=0,add=0,i;
                                    for(i=0;i< products.size();i++)
                                        sum=sum+(products.get(i).getPrice()*products.get(i).getQnt());

                                    rateview.setText("₹"+sum);

                                    add=minAmount-sum;

                                    if(sum<minAmount){
                                        banner.setText ("Add More ₹"+add+"For Get Free Delivery");
                                        banner.setTextColor (banner.getContext ().getResources ().getColor (R.color.red));
                                    } else {

                                        banner.setText("Congratulations You Got Free Delivery");
                                        banner.setTextColor (banner.getContext ().getResources ().getColor (purple_500));
                                    }
                                    if(sum==0){
                                        banner.setVisibility (View.GONE);
                                        text.setVisibility (View.VISIBLE);
                                        imageView.setVisibility (View.VISIBLE);
                                        cardView.setVisibility (View.GONE);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });






                    }
                }
            }
        });

    }
    public void updateQuantity() {
        int qty = 0, i;
        for (i = 0; i < products.size ( ); i++)
            qty = qty + (products.get (i).getQnt ( ));

        if (qty>1){
            quantity.setText (""+qty+" ITEMS");
        }else {
            quantity.setText (""+qty+" ITEM ");
        }
    }

    public void updateCart() {
        int cqty = 0, i2;
        for (i2 = 0; i2 < products.size ( ); i2++)
            cqty = cqty + (products.get (i2).getQnt ( ));

        if (cqty>1){
            quantity.setText (""+cqty+" ITEMS");
        }else {
            quantity.setText (""+cqty+" ITEM ");
        }
    }



}
