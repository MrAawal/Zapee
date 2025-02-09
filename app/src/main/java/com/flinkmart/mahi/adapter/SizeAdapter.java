package com.flinkmart.mahi.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ListItemBinding;
import com.flinkmart.mahi.model.Colour;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.model.Size;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.branchViewholder> {
    private Context context;
    private boolean is_selected;
    private List<Size> branchemodel;


    int row_index=-1;
    ColorAdapter colorAdapter;

    RecyclerView colorList;
    Item productModel;
    Button save;
    public SizeAdapter(Context context, ColorAdapter colorAdapter, RecyclerView colorList, Item productModel, Button save) {
        this.context=context;
        branchemodel=new ArrayList<> ();
        this.colorAdapter=colorAdapter;
        this.colorList=colorList;
        this.productModel=productModel;
        this.save=save;
    }

    public void addProduct(Size branch){
        branchemodel.add (branch);
        notifyDataSetChanged ();
    }
    public List<Size>getSelectedItem(){
        List<Size>selectItem=new ArrayList<> ();
        for (int i=0;i<branchemodel.size ();i++){
            if(branchemodel.get (i).is_selected){
                selectItem.add (branchemodel.get (i));
            }
        }
        return selectItem;
    }

    public void onBindViewHolder(@NonNull branchViewholder holder, int position) {
        Size brance=branchemodel.get (position);
        holder.binding.size.setText (brance.getSize ());


        holder.itemView.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                SharedPreferences sp=context.getSharedPreferences("ProductSize",MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("size", brance.getSize ( ));
                editor.apply();

                row_index=position;
                notifyDataSetChanged ();

                if (brance.is_selected){
                    brance.is_selected=false;
                }else {
                    brance.is_selected=true;

                }


            }
        });

        if(row_index==position){
            holder.binding.size.setTextColor (context.getResources ().getColor (R.color.red));
            holder.binding.size.setBackgroundResource (R.drawable.catbg);

            colorAdapter=new ColorAdapter (context);

            LinearLayoutManager layoutManager1 = new GridLayoutManager (context, 4);
            colorList.setLayoutManager (layoutManager1);
            colorList.setAdapter (colorAdapter);

            save.setEnabled (true);
            save.setTextColor (context.getResources ().getColor (R.color.white));

            FirebaseFirestore.getInstance ()
                    .collection ("productColor")
                    .whereEqualTo ("id",productModel.getId ())
                    .whereEqualTo ("size",brance.getSize ())
                    .get ()
                    .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                            for (DocumentSnapshot ds:dsList){
                                Colour product=ds.toObject (Colour.class);
                                colorAdapter.addProduct(product);
                            }


                        }
                    });
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

