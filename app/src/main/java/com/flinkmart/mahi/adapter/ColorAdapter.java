package com.flinkmart.mahi.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ListItemBinding;
import com.flinkmart.mahi.model.Colour;
import com.flinkmart.mahi.model.Size;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.branchViewholder> {
    private Context context;
    private boolean is_selected;
    private List<Colour> branchemodel;

    int row_index=-1;
    public ColorAdapter(Context context) {
        this.context=context;
        branchemodel=new ArrayList<> ();
    }

    public void addProduct(Colour branch){
        branchemodel.add (branch);
        notifyDataSetChanged ();
    }
    public List<Colour>getSelectedItem(){
        List<Colour>selectItem=new ArrayList<> ();
        for (int i=0;i<branchemodel.size ();i++){
            if(branchemodel.get (i).is_selected){
                selectItem.add (branchemodel.get (i));
            }
        }
        return selectItem;
    }

    public void onBindViewHolder(@NonNull branchViewholder holder, int position) {
        Colour brance=branchemodel.get (position);
        holder.binding.size.setText (brance.getColor ());

        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                row_index=position;
                notifyDataSetChanged ();

                SharedPreferences sp=context.getSharedPreferences("ProductColor",MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("color",brance.getColor ());
                editor.apply();

                if (brance.is_selected){
                    brance.is_selected=false;
                }else {
                    brance.is_selected=true;
                }


            }
        });

        if(row_index==position){
            holder.binding.size.setBackgroundResource (R.drawable.catbg);
            holder.binding.size.setTextColor (context.getResources ().getColor (R.color.red));
        }else {
            holder.binding.size.setTextColor (context.getResources ().getColor (R.color.black));
            holder.binding.size.setBackgroundResource (R.drawable.product);
        }
    }

    @Override
    public int getItemCount() {
        return branchemodel.size ();
    }

    @NonNull
    @Override
    public branchViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.list_item,parent,false);
        return new branchViewholder (view);

    }


    class  branchViewholder extends RecyclerView.ViewHolder{

        public boolean is_selected;

        ListItemBinding binding;

        public branchViewholder(@NonNull View itemView) {
            super (itemView);

           binding=ListItemBinding.bind (itemView);

        }
    }

}

