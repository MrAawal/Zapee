package com.flinkmart.mahi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.AllProductActivity;
import com.flinkmart.mahi.databinding.ItemCategoriesBinding;
import com.flinkmart.mahi.model.Catlist;

import java.util.ArrayList;
import java.util.List;
public class SublistAdapter extends RecyclerView.Adapter<SublistAdapter.branchViewholder>{

    private Context context;
    private List<Catlist> catlists;


    TextView recyclerView;


    public SublistAdapter(Context context){
        this.context=context;
        this.recyclerView=recyclerView;
        catlists=new ArrayList<> ();
    }
    public void addProduct(Catlist catlist){
        catlists.add (catlist);
        notifyDataSetChanged ();
    }

    public List<Catlist>getSelectedItem(){
        List<Catlist>selectItem=new ArrayList<> ();
        for (int i=0;i<catlists.size ();i++){
            if(catlists.get (i).is_selected){
                selectItem.add (catlists.get (i));
            }
        }
        return selectItem;
    }

    public void onBindViewHolder(@NonNull branchViewholder holder, int position) {
        Catlist catlist=catlists.get (position);
        holder.binding.label.setText (catlist.getTittle ());
        Glide.with (context).load (catlist.getImage ())
                .into (holder.binding.image);


                holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent (context, AllProductActivity.class);
                        intent.putExtra("category",catlist.getTittle());
                        context.startActivity(intent);


                    }
                });
            }



    @Override
    public int getItemCount() {
        return catlists.size ();
    }

    @NonNull
    @Override
    public branchViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.item_categories,parent,false);
        return new branchViewholder (view);

    }

    class  branchViewholder extends RecyclerView.ViewHolder{

        ItemCategoriesBinding binding;
        public branchViewholder(@NonNull View itemView) {
            super (itemView);
            binding=ItemCategoriesBinding.bind (itemView);
        }
    }

}


