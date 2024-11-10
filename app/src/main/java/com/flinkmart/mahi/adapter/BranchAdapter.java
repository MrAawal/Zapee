package com.flinkmart.mahi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ItemBranchHorizontalBinding;
import com.flinkmart.mahi.databinding.ItemFavouriteBinding;
import com.flinkmart.mahi.model.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.branchViewholder> {
    private Context context;
    private List<Branch> branchemodel;
    public BranchAdapter(Context context) {
        this.context=context;
        branchemodel=new ArrayList<> ();
    }

    public void addProduct(Branch branch){
        branchemodel.add (branch);
        notifyDataSetChanged ();
    }
    public List<Branch>getSelectedItem(){
        List<Branch>selectItem=new ArrayList<> ();
        for (int i=0;i<branchemodel.size ();i++){
            if(branchemodel.get (i).is_selected){
                selectItem.add (branchemodel.get (i));
            }
        }
        return selectItem;
    }

    public void onBindViewHolder(@NonNull branchViewholder holder, int position) {
        Branch brance=branchemodel.get (position);
        holder.binding.branch.setText (brance.getStorename());
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if (brance.is_selected){
                    brance.is_selected=false;
                    holder.binding.lin.setBackgroundColor (context.getResources ().getColor (R.color.white));
                    Toast.makeText (context, "Remove"+brance.getStorename (), Toast.LENGTH_SHORT).show ( );
                }else {
                    brance.is_selected=true;
                    holder.binding.lin.setBackgroundColor (context.getResources ().getColor (R.color.teal_700));
                    Toast.makeText (context, "Added"+brance.getStorename (), Toast.LENGTH_SHORT).show ( );
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return branchemodel.size ();
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

}

