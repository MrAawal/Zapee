package com.flinkmart.mahi.adapter;


import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.OrderDetailActivity;
import com.flinkmart.mahi.databinding.ItemOrderBinding;
import com.flinkmart.mahi.model.ImageModel;
import com.flinkmart.mahi.model.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersProfileAdapter extends RecyclerView.Adapter<OrdersProfileAdapter.LoanModelViewholder> {
    private Context context;
    private List<Order>ordersModel=new ArrayList<> ();
    TextView text;

    public OrdersProfileAdapter(Context context, TextView text) {
        this.context = context;
        this.text=text;
        ordersModel = new ArrayList<>();
    }


    public void addProduct(Order order){
        ordersModel.add (order);
        notifyDataSetChanged ();
    }
    @NonNull
    @Override
    public LoanModelViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from (context).inflate (R.layout.item_order,parent,false);
        return new LoanModelViewholder (view);
    }

    public void onBindViewHolder(@NonNull LoanModelViewholder holder, int position) {

        if(ordersModel.size ()>0){
            text.setText ("");
            text.setVisibility (View.GONE);
        }

        Order order=ordersModel.get (position);
        holder.binding.status.setText ("#"+order.getOrderNumber ());
        holder.binding.textView14.setText (order.getCustomerName ());
        holder.binding.textView15.setText (order.getCustomerNumber ());
        holder.binding.textView17.setText (order.getCustomerAddress ());
        holder.binding.partner.setText (order.getPartner ());

        holder.binding.partner.setTextColor (context.getResources ().getColor (R.color.purple_500));

        CharSequence date= DateFormat.format ("EEEE,MMM d,yyyy h:mm:ss a",order.getOrderPlaceDate ().toDate ());
        holder.binding.date.setText (date);


        ImageAdapter  imageAdapter= new ImageAdapter (context);
        holder.binding.productList.setAdapter (imageAdapter);
        holder.binding.productList.setLayoutManager (new LinearLayoutManager (context));
        int orderNumber= Integer.parseInt (order.getOrderNumber ());

        FirebaseFirestore.getInstance ()
                        .collection ("OrderProduct")
                        .whereEqualTo ("pid",orderNumber)
                        .get ()
                                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                      List<DocumentSnapshot> DsList=queryDocumentSnapshots.getDocuments ();
                                      for (DocumentSnapshot ds:DsList){
                                          ImageModel imageModel=ds.toObject (ImageModel.class);
                                          imageAdapter.addProduct (imageModel);
                                      }
                                    }
                                });






        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderNumber", order.getOrderNumber());
                intent.putExtra("totalPrice", order.getTotalPrice ());
                intent.putExtra("address", order.getCustomerAddress());
                intent.putExtra("payment", order.getPayment ());
                intent.putExtra("date", date);
                intent.putExtra("status", order.getPartner ());
                context.startActivity(intent);
            }
        });


    }




    @Override
    public int getItemCount() {
        return ordersModel.size ();
    }



    class  LoanModelViewholder extends RecyclerView.ViewHolder{

        ItemOrderBinding binding;
        public LoanModelViewholder(@NonNull View itemView) {
            super(itemView);

            binding = ItemOrderBinding.bind(itemView);

        }
    }
}