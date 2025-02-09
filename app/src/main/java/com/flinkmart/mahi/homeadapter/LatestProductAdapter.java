package com.flinkmart.mahi.homeadapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.adapter.ColorAdapter;
import com.flinkmart.mahi.adapter.SizeAdapter;
import com.flinkmart.mahi.databinding.CartSheetBinding;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.model.CartModel;
import com.flinkmart.mahi.model.Colour;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.model.Size;
import com.flinkmart.mahi.roomdatabase.AppDatabase;
import com.flinkmart.mahi.roomdatabase.Product;
import com.flinkmart.mahi.roomdatabase.ProductDao;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LatestProductAdapter extends RecyclerView.Adapter<LatestProductAdapter.holder>{

    Context context;
    CartModel cartItem;

    List<Item> modelList ;
    List<Item> productItems;

    SizeAdapter sizeAdapter;
    ColorAdapter colorAdapter;

    public  static List<Size>sizes=new ArrayList<> ();
    public  static List<Colour>colours=new ArrayList<> (  );

    String uid= FirebaseAuth.getInstance ( ).getUid ( );

    public LatestProductAdapter(Context context, List<Item> productItems) {
        this.context = context;
        this.modelList = modelList;
        this.productItems = productItems;
    }




    @NonNull
    @Override
    public LatestProductAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newitem_product, parent , false);
       return  new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LatestProductAdapter.holder holder, int position) {

        Item newProductModel=productItems.get (position);
        holder.binding.label.setText (newProductModel.getTittle ().toUpperCase ());
        holder.binding.price.setText ("₹"+newProductModel.getPrice ());
        holder.binding.Discount.setText ("₹"+newProductModel.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);


        int mrp= Integer.parseInt (newProductModel.getDiscount ());
        int price= Integer.parseInt (newProductModel.getPrice ());
        String sum= String.valueOf (mrp-price);
        holder.binding.parcent.setText ("SAVE ₹"+sum);

        holder.binding.cart.setVisibility (View.VISIBLE);
        holder.binding.remove.setVisibility (View.INVISIBLE);

        Glide.with (context).load(newProductModel.getImage ())
                .into (holder.binding.image);

        holder.binding.cart.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

                bottomSheet (newProductModel,productItems,position,holder.binding.remove,holder.binding.cart);


            }
        });

        holder.binding.remove.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                int id= Integer.parseInt (newProductModel.getId ());
                AppDatabase db = Room.databaseBuilder(holder.binding.remove.getContext(),
                        AppDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();
                productDao.deleteById(id);
                holder.binding.remove.setVisibility (View.INVISIBLE);
                holder.binding.cart.setVisibility (View.VISIBLE);

            }
        });




        AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
        ProductDao productDao=db.ProductDao();
        List<Product> products=productDao.getallproduct ();
        int id= Integer.parseInt (newProductModel.getId ());
        Boolean check=productDao.is_exist(id);
        if(check==true){
            holder.binding.cart.setVisibility (View.INVISIBLE);
            holder.binding.remove.setVisibility (View.VISIBLE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idee= String.valueOf (newProductModel.getId ());

                Intent intent = new Intent(context, NewProductDetailActivity.class);
                intent.putExtra("id", idee);
                intent.putExtra("description",newProductModel.getDescription ());
                intent.putExtra("name", newProductModel.getTittle ());
                intent.putExtra("image", newProductModel.getImage());
                intent.putExtra("price", newProductModel.getPrice());
                intent.putExtra("subcategory",newProductModel.getSubcategory ());
                intent.putExtra("discount",newProductModel.getDiscount ());
                intent.putExtra("category",newProductModel.getCategory ());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productItems.size();
    }


    public void addProduct(Item newProductModel) {
        productItems.add (newProductModel);
        notifyDataSetChanged ();
    }

    private void bottomSheet(Item newProductModel, List<Item> productItems, int position, TextView remove, TextView cart){
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        CartSheetBinding quantityDialogBinding = CartSheetBinding.inflate(LayoutInflater.from(context));

        bottomSheetDialog.setContentView (quantityDialogBinding.getRoot ());
        TextView totalQuantity=quantityDialogBinding.quantity;

        quantityDialogBinding.pname.setText (newProductModel.getTittle ());
        quantityDialogBinding.pprice.setText ("₹"+newProductModel.getPrice ());


        RecyclerView colorList=quantityDialogBinding.colourList;

        Button save=quantityDialogBinding.saveBtn;

        sizeAdapter=new SizeAdapter (context, colorAdapter, colorList, newProductModel, save)  ;
        LinearLayoutManager layoutManager = new GridLayoutManager (context, 4);
        quantityDialogBinding.sizeList.setLayoutManager (layoutManager);
        quantityDialogBinding.sizeList.setAdapter (sizeAdapter);

        colorAdapter=new ColorAdapter (context)  ;
        LinearLayoutManager layoutManager1 = new GridLayoutManager (context, 4);
        quantityDialogBinding.colourList.setLayoutManager (layoutManager1);
        quantityDialogBinding.colourList.setAdapter (colorAdapter);

        sizeList(newProductModel,save);
//        colorList (newProductModel);





        final List<SlideModel>imageList=new ArrayList<> ();
        FirebaseDatabase.getInstance ().getReference ("productimages").child (newProductModel.getId ()).addListenerForSingleValueEvent (new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren ( )) {
                    imageList.add (new SlideModel (ds.child ("link").getValue (  ).toString (), "", ScaleTypes.FIT));
                    quantityDialogBinding.carousel.setImageList (imageList,ScaleTypes.FIT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        quantityDialogBinding.add.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                int qnt=productItems.get (position).getQty ();
                qnt++;
                newProductModel.setQty (qnt);
                quantityDialogBinding.quantity.setText (String.valueOf (qnt));

                SharedPreferences sp=context.getSharedPreferences("ProductQnty"+newProductModel.getId (),MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("qnt", String.valueOf (qnt));
                editor.apply();

                int sum=0,add=0,i;
                for(i=0;i< productItems.size();i++) {
                    int price= Integer.parseInt (productItems.get (position).getPrice ());
                    sum=price*qnt;
                    quantityDialogBinding.pprice.setText ("₹"+sum);
                    notifyDataSetChanged ();

                }
            }
        });
        quantityDialogBinding.reduce.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                int qnt=productItems.get (position).getQty ();
                if(qnt>1)
                qnt--;

                newProductModel.setQty (qnt);
                quantityDialogBinding.quantity.setText (String.valueOf (qnt));

                SharedPreferences sp=context.getSharedPreferences("ProductQnty"+newProductModel.getId (),MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("qnt", String.valueOf (qnt));
                editor.apply();

                int sum=0,add=0,i;
                for(i=0;i< productItems.size();i++) {
                    int price= Integer.parseInt (productItems.get (position).getPrice ());
                    sum=price*qnt;
                    quantityDialogBinding.pprice.setText ("₹"+sum);
                    notifyDataSetChanged ();
                }
            }
        });

        String quantity= totalQuantity.getText ().toString ();
        quantityDialogBinding.saveBtn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){

                AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();
                int id= Integer.parseInt (newProductModel.getId ());

                SharedPreferences sp2=context.getSharedPreferences("ProductColor",MODE_PRIVATE);

                SharedPreferences sp=context.getSharedPreferences("ProductSize",MODE_PRIVATE);

                SharedPreferences sp3=context.getSharedPreferences("ProductQnty"+newProductModel.getId (),MODE_PRIVATE);


                 String quantit=sp3.getString ("qnt","1");
                 String size = sp.getString ("size", "")+"-"+sp2.getString ("color", "");

                        Boolean check = productDao.is_exist (id);
                        if (check == false){
                            productDao.insertrecord (new Product (id, newProductModel.getTittle ( )+"-"+size, newProductModel.getImage ( ), Integer.parseInt (newProductModel.getPrice ( )), Integer.parseInt (quantit), newProductModel.getDiscount (), newProductModel.description));
                            bottomSheetDialog.cancel ();
                            SharedPreferences.Editor editor=sp.edit ();
                            SharedPreferences.Editor editor1=sp2.edit ();
                            SharedPreferences.Editor editor2=sp3.edit ();

                            editor.clear ().commit ();
                            editor.apply();

                            editor2.clear ().commit ();
                            editor.apply();

                            editor1.clear ().commit ();
                            editor.apply();

                            editor1.remove ("color");
                            editor1.apply ();

                            editor2.remove ("size");
                            editor2.apply ();

                            editor.remove ("ProductQnty"+newProductModel.getId ());
                            editor.apply ();

                            cart.setVisibility (View.INVISIBLE);
                            remove.setVisibility (View.VISIBLE);

                            notifyDataSetChanged ();

                        } else {
                    Toast.makeText (context, "Item Exist", Toast.LENGTH_SHORT).show ( );
                        }


                }





        });




        bottomSheetDialog.show ();
    }

    private void sizeList(Item newProductModel, Button save) {
        FirebaseFirestore.getInstance ()
                .collection ("productSize")
                .whereEqualTo ("id",newProductModel.getId ())
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                           save.setEnabled (false);
                            save.setTextColor (context.getResources ().getColor (R.color.red));
                            Size product=ds.toObject (Size.class);
                            sizeAdapter.addProduct(product);
                        }


                    }
                });



    }
    private void colorList(Item newProductModel) {
        FirebaseFirestore.getInstance ()
                .collection ("productColor")
                .whereEqualTo ("id",newProductModel.getId ())
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



    }


    public class holder extends RecyclerView.ViewHolder{
        NewitemProductBinding binding;
        public holder(@NonNull View itemView) {
            super(itemView);
            binding = NewitemProductBinding.bind(itemView);
        }
    }
}
