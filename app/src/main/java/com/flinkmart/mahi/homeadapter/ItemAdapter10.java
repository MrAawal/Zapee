package com.flinkmart.mahi.homeadapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.homemodel.Item4;

import java.util.List;

public class ItemAdapter10 extends RecyclerView.Adapter<ItemAdapter10.holder>{

    Context context;
    List<Item4> productItems;

    public ItemAdapter10(Context context) {
        this.context = context;
        this.productItems = productItems;
    }




    @NonNull
    @Override
    public ItemAdapter10.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newitem_product, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter10.holder holder, int position) {
        Item4 newProductModel=productItems.get (position);
        holder.binding.label.setText (newProductModel.getTittle ().toUpperCase ());
        holder.binding.price.setText ("₹"+newProductModel.getPrice ());
        holder.binding.Discount.setText ("₹"+newProductModel.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with (context).load(newProductModel.getImage ())
                .into (holder.binding.image);
        int mrp= Integer.parseInt (newProductModel.getDiscount ());
        int price= Integer.parseInt (newProductModel.getPrice ());
        String sum= String.valueOf (mrp-price);
        holder.binding.parcent.setText ("SAVE ₹"+sum);
//        holder.binding.reduce.setVisibility (View.INVISIBLE);
//        holder.binding.add.setVisibility (View.INVISIBLE);
//        holder.binding.quantity.setVisibility (View.INVISIBLE);
//        holder.binding.cart.setOnClickListener (new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View v) {
//                holder.binding.cart.setVisibility (View.INVISIBLE);
//                holder.binding.reduce.setVisibility (View.VISIBLE);
//                holder.binding.add.setVisibility (View.VISIBLE);
//                holder.binding.quantity.setVisibility (View.VISIBLE);
//            }
//        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(context, NewProductDetailActivity.class);
                intent.putExtra("id", newProductModel.getId());
                intent.putExtra("description",newProductModel.getDescription ());
                intent.putExtra("name", newProductModel.getTittle ());
                intent.putExtra("image", newProductModel.getImage());
                intent.putExtra("price", newProductModel.getPrice());
                intent.putExtra("subcategory",newProductModel.getSubcategory());
                intent.putExtra("discount",newProductModel.getDiscount ());
                intent.putExtra("category",newProductModel.getCategory());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productItems.size();
    }


    public void addProduct(Item4 newProductModel) {
        productItems.add (newProductModel);
        notifyDataSetChanged ();
    }





    public class holder extends RecyclerView.ViewHolder{
        NewitemProductBinding binding;
        public holder(@NonNull View itemView) {
            super(itemView);
            binding = NewitemProductBinding.bind(itemView);
        }
    }
}
