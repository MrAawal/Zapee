package com.flinkmart.mahi.branchAdapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.activities.SubCatListActivity;
import com.flinkmart.mahi.categorymodel.CategoryModel1;
import com.flinkmart.mahi.databinding.ItemBranchHorizontalBinding;
import com.flinkmart.mahi.databinding.ItemCategoryLargeBinding;
import com.flinkmart.mahi.model.Branch;
import com.flinkmart.mahi.model.Resturant;
import com.flinkmart.mahi.restuarant.RestuarantActivity;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RestAdapter extends RecyclerView.Adapter<RestAdapter.holder> {

    Context context;
    List<Resturant> categoryItems;

    public RestAdapter(Context context, List<Resturant> categoryItems) {
        this.context = context;
        this.categoryItems = categoryItems;
    }

    @NonNull
    @Override
    public RestAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branch_horizontal, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestAdapter.holder holder, int position) {
        Resturant brance=categoryItems.get (position);
        holder.binding.branch.setText (brance.getStorename ());
        Glide.with (context).load (brance.getImage())
                .into (holder.binding.imageView6);
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                holder.binding.progressBar9.setVisibility (View.VISIBLE);
                holder.binding.imageView6.setEnabled (false);
                String uid= FirebaseAuth.getInstance ( ).getUid ( );
                Branch branch=new Branch (brance.getUid (),brance.getStoreLat (),brance.getStoreLon (),true,uid,brance.storename ,"");
                FirebaseFirestore.getInstance ()
                        .collection ("userRestaurant")
                        .document (uid)
                        .set (branch);

                FirebaseFirestore.getInstance ( )
                        .collection ("users")
                        .document (uid)
                        .update ("store",brance.getUid ())
                        .addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                            @Override
                            public void onSuccess(Void unused) {


                                SharedPreferences sp=context.getSharedPreferences("userRestaurant",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sp.edit();
                                editor.putString("StoreId",brance.getUid ());
                                editor.putString ("storeName",brance.getStorename ());
                                editor.putString ("storeLat",brance.getStoreLat ());
                                editor.putString ("storeLon",brance.getStoreLat ());
                                editor.apply();

                                Intent intent = new Intent (context, RestuarantActivity.class);
                                intent.putExtra ("restId", brance.getUid ( ));
                                intent.putExtra ("restName", brance.getStorename ( ));
                                context.startActivity (intent);

                                AppDatabase db = Room.databaseBuilder (context,
                                        AppDatabase.class, "cart_db").allowMainThreadQueries ( ).build ( );
                                ProductDao productDao = db.ProductDao ( );

                                db.clearAllTables ( );
                            }
                        });

            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }


    public void addProduct(Resturant catlist) {
        categoryItems.add (catlist);
        notifyDataSetChanged ();
    }

    public class holder extends RecyclerView.ViewHolder{
        ItemBranchHorizontalBinding binding;
        public holder(@NonNull View itemView) {
            super(itemView);
            binding = ItemBranchHorizontalBinding.bind (itemView);
        }
    }
}
