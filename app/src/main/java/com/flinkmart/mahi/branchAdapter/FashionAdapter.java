package com.flinkmart.mahi.branchAdapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.MainActivity;
import com.flinkmart.mahi.databinding.ItemBranchHorizontalBinding;
import com.flinkmart.mahi.model.BranchModel;
import com.flinkmart.mahi.model.Fashion;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FashionAdapter extends RecyclerView.Adapter<FashionAdapter.branchViewholder> {
    private Context context;

    TextView warning;

    DatabaseReference databaseReference;

    List<Fashion> branchemodel;

    List<Fashion> modelListFilter;


    public FashionAdapter(Context context, TextView warning) {
        this.context=context;
        this.warning=warning;
        branchemodel=new ArrayList<> ();
        this.modelListFilter = branchemodel;
    }

    public void addProduct(Fashion branch){
        modelListFilter.add (branch);
        notifyDataSetChanged ();
    }


    public void onBindViewHolder(@NonNull branchViewholder holder, int position) {
        Fashion brance=branchemodel.get (position);
        holder.binding.branch.setText (brance.getStorename());
        holder.binding.Pincode.setText (brance.getPincode ());
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                String uid=FirebaseAuth.getInstance ( ).getUid ( );

                BranchModel branch=new BranchModel ();
                FirebaseFirestore.getInstance ( )
                        .collection ("userstore")
                        .document (uid)
                        .set (branch);

                FirebaseFirestore.getInstance ( )
                        .collection ("users")
                        .document (uid)
                        .update ("store",brance.getUid ())
                        .addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                            @Override
                            public void onSuccess(Void unused) {

                                SharedPreferences sp=context.getSharedPreferences("store",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sp.edit();

                                editor.putString("StoreId",brance.getUid ());
                                editor.putString ("storeName",brance.getStorename ());
                                editor.putString ("storeLat",brance.getStoreLat ());
                                editor.putString ("storeLon",brance.getStoreLat ());
                                editor.apply();

                                if(sp.contains("StoreId"))
                                   {
                                    Intent intent = new Intent (context, MainActivity.class);
                                    context.startActivity (intent);
                                   }
                            }
                        });




            }
        });



        if(branchemodel!=null){
          warning.setText ("");
        }

        databaseReference= FirebaseDatabase.getInstance ().getReference ("store/"+brance.getPincode ());

        databaseReference.addListenerForSingleValueEvent (new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean value=snapshot.child ("online").getValue (boolean.class);
                if(value!=true){
                    holder.binding.address.setText ("Store is offline");
                }else {
                    holder.binding.address.setText ("Store is Online");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public int getItemCount() {
        return modelListFilter.size ();
    }

    @NonNull
    @Override
    public branchViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from (context).inflate (R.layout.item_branch_horizontal,parent,false);
        return new branchViewholder (view);

    }


    class  branchViewholder extends RecyclerView.ViewHolder{

       ItemBranchHorizontalBinding binding;
        public branchViewholder(@NonNull View itemView) {
            super (itemView);
            binding = ItemBranchHorizontalBinding.bind(itemView);

        }
    }

    public Filter getFilter() {
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charcater = constraint.toString();
                if (charcater.isEmpty()){
                    modelListFilter = branchemodel ;
                }else{
                    List<Fashion> filterList = new ArrayList<>();
                    for (Fashion row: branchemodel){
                        if (row.getStorename ().toLowerCase().contains(charcater.toLowerCase())){
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
                modelListFilter = (ArrayList<Fashion>) results.values ;
                notifyDataSetChanged();
            }
        };
    }


}

