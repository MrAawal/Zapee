package com.flinkmart.mahi.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.activities.ProductDetailActivity;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.model.NewProductModel;
import com.flinkmart.mahi.R;
import java.util.ArrayList;
import java.util.List;

public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.LoanModelViewholder> {
    private Context context;
    private List<NewProductModel>productModels;
    public NewProductAdapter(Context context){
        this.context=context;
        productModels=new ArrayList<>();
    }
    public void addProduct(NewProductModel productModel){
        productModels.add (productModel);
        notifyDataSetChanged ();
    }


    public void onBindViewHolder(@NonNull LoanModelViewholder holder, int position) {

        NewProductModel productModel=productModels.get (position);
        holder.binding.label.setText (productModel.getTittle ());
        holder.binding.price.setText ("₹"+productModel.getPrice ());
        holder.binding.Discount.setText ("MRP : ₹"+productModel.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with (context).load(productModel.getImage ())
                .into (holder.binding.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewProductDetailActivity.class);
                intent.putExtra("id", productModel.getId());
                intent.putExtra("description", productModel.getDescription ());
                intent.putExtra("name", productModel.getTittle ());
                intent.putExtra("image", productModel.getImage());
                intent.putExtra("price", productModel.getPrice());
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return productModels.size ();
    }
    @NonNull
    @Override
    public LoanModelViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.newitem_product,parent,false);
        return new LoanModelViewholder (view);
    }


    class  LoanModelViewholder extends RecyclerView.ViewHolder{

        NewitemProductBinding binding;
        public LoanModelViewholder(@NonNull View itemView) {
            super(itemView);
            binding = NewitemProductBinding.bind(itemView);
        }
    }
}