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
import com.flinkmart.mahi.databinding.ItemStoreBinding;
import com.flinkmart.mahi.model.BranchModel;
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

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.branchViewholder> {
    private Context context;

    TextView warning;

    DatabaseReference databaseReference;

    List<BranchModel> branchemodel;

    List<BranchModel> modelListFilter;


    public BranchAdapter(Context context, TextView warning) {
        this.context=context;
        this.warning=warning;
        branchemodel=new ArrayList<> ();
        this.modelListFilter = branchemodel;
    }

    public void addProduct(BranchModel branch){
        modelListFilter.add (branch);
        notifyDataSetChanged ();
    }


    public void onBindViewHolder(@NonNull branchViewholder holder, int position) {
        BranchModel brance=branchemodel.get (position);

        holder.binding.branch.setText (brance.getStorename());
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                String uid=FirebaseAuth.getInstance ( ).getUid ( );

                holder.binding.lin.setBackgroundColor (context.getResources ().getColor (R.color.fav));
                BranchModel branch=new BranchModel (brance.getStorename (),brance.getStoreuid (),brance.getStoreLat (),brance.getStoreLon (),brance.getDelivery (),brance.getPincode (),uid,brance.getStorecate (),brance.minAmount,brance.getPolicy(),brance.getRadius (),brance.getAnnouncement (),brance.getKmcharge(),brance.getPacking (),true);
                FirebaseFirestore.getInstance ( )
                        .collection ("userStore")
                        .document (uid)
                        .set (branch);

                FirebaseFirestore.getInstance ( )
                        .collection ("users")
                        .document (uid)
                        .update ("store",brance.getStoreuid())
                        .addOnSuccessListener (new OnSuccessListener<Void> ( ) {
                            @Override
                            public void onSuccess(Void unused) {

//                                SharedPreferences sp=context.getSharedPreferences("store",MODE_PRIVATE);
//                                SharedPreferences.Editor editor=sp.edit();
//
//                                editor.putString("StoreId",brance.getStoreuid ());
//                                editor.putString ("storeName",brance.getStorename ());
//                                editor.putString ("storeLat",brance.getStoreLat ());
//                                editor.putString ("storeLon",brance.getStoreLat ());
//                                editor.apply();

                                Intent intent = new Intent (context, MainActivity.class);
                                    context.startActivity (intent);

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
                    holder.binding.address.setText ("Currently not taking order");
                }else{
                    holder.binding.address.setText ("Available");
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

        View view= LayoutInflater.from (context).inflate (R.layout.item_store,parent,false);
        return new branchViewholder (view);

    }


    class  branchViewholder extends RecyclerView.ViewHolder{

       ItemStoreBinding binding;
        public branchViewholder(@NonNull View itemView) {
            super (itemView);
            binding = ItemStoreBinding.bind(itemView);

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
                    List<BranchModel> filterList = new ArrayList<>();
                    for (BranchModel row: branchemodel){
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
                modelListFilter = (ArrayList<BranchModel>) results.values ;
                notifyDataSetChanged();
            }
        };
    }


}

