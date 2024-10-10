package com.flinkmart.mahi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.CategoryActivity;
import com.flinkmart.mahi.activities.SubCatListActivity;
import com.flinkmart.mahi.model.Catlist;

import java.util.ArrayList;
import java.util.List;

public class CatlistAdapter extends RecyclerView.Adapter<CatlistAdapter.branchViewholder>{

    private Context context;
    private List<Catlist> catmodel;
    public CatlistAdapter(Context context){
        this.context=context;
        catmodel=new ArrayList<> ();
    }
    public void addProduct(Catlist catlist){
        catmodel.add (catlist);
        notifyDataSetChanged ();
    }


    public void onBindViewHolder(@NonNull branchViewholder holder, int position) {
        Catlist catlist=catmodel.get (position);
        holder.name.setText (catlist.getTittle ());
        Glide.with (context).load (catlist.getImage ())
                .into (holder.image);
        
        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubCatListActivity.class);
                intent.putExtra("category", catlist.getTittle ());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return catmodel.size ();
    }

    @NonNull
    @Override
    public branchViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from (context).inflate (R.layout.item_branch,parent,false);
        return new branchViewholder (view);

    }

    class  branchViewholder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView image;
        public branchViewholder(@NonNull View itemView) {
            super (itemView);
            image=itemView.findViewById (R.id.imageView2);
            name=itemView.findViewById (R.id.branch);
        }
    }

}


