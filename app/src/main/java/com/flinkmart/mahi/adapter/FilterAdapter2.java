package com.flinkmart.mahi.adapter;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.NewProductDetailActivity;
import com.flinkmart.mahi.databinding.CartSheetBinding;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
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

public class FilterAdapter2 extends RecyclerView.Adapter<FilterAdapter2.MyHolder> implements Filterable {

    Context context;
    List<Item> modelList ;
    List<Item> modelListFilter;
    String uid= FirebaseAuth.getInstance ( ).getUid ( );
    TextView quantity;
    CardView layout;

    SizeAdapter sizeAdapter;
    ColorAdapter colorAdapter;

    public FilterAdapter2(Context context, List<Item> modelList, TextView quantity, CardView layout) {
        this.context = context;
        this.modelList = modelList;
        this.modelListFilter = modelList;
        this.quantity=quantity;
        this.layout=layout;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newitem_product , parent , false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterAdapter2.MyHolder holder, int position) {

        Item productModel=modelListFilter.get (position);
        holder.binding.label.setText (productModel.getTittle ());
        holder.binding.price.setText ("₹"+productModel.getPrice ());
        holder.binding.Discount.setText ("₹"+productModel.getDiscount ());
        holder.binding.Discount.setPaintFlags (Paint.STRIKE_THRU_TEXT_FLAG);

        holder.binding.cart.setVisibility (View.VISIBLE);
        holder.binding.remove.setVisibility (View.INVISIBLE);
        int mrp= Integer.parseInt (productModel.getDiscount ());
        int price= Integer.parseInt (productModel.price);
        String sum= String.valueOf (mrp-price);

        holder.binding.parcent.setText ("SAVE ₹"+sum);

        Glide.with (context).load(productModel.getImage ())
                .into (holder.binding.image);
        

        AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
        ProductDao productDao=db.ProductDao();
        List<Product> products=productDao.getallproduct ();
        int id= Integer.parseInt (productModel.getId ());
        Boolean check=productDao.is_exist(id);


        if(check==true){
           holder.binding.cart.setVisibility (View.INVISIBLE);
           holder.binding.remove.setVisibility (View.VISIBLE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewProductDetailActivity.class);
                intent.putExtra("id", productModel.getId());
                intent.putExtra("description",productModel.getDescription ());
                intent.putExtra("name", productModel.getTittle ());
                intent.putExtra("image", productModel.getImage());
                intent.putExtra("price", productModel.getPrice());
                intent.putExtra("subcategory",productModel.getSubcategory());
                intent.putExtra("discount",productModel.getDiscount ());
                intent.putExtra("category",productModel.getCategory());
                context.startActivity(intent);
            }
        });

        holder.binding.cart.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                bottomSheet (holder,productModel,position,modelListFilter,holder.binding.cart,holder.binding.remove);
//                  sizeCart(holder,productModel,position);
            }
        });

        holder.binding.remove.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                removeItem (holder,position,productModel);
            }
        });


    }

    private void sizeCart(MyHolder holder, Item productModel, int position) {

        holder.binding.cart.setVisibility (View.GONE);
        holder.binding.remove.setVisibility (View.VISIBLE);
        layout.setVisibility (View.VISIBLE);

        AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
        ProductDao productDao=db.ProductDao();
        List<Product> products=productDao.getallproduct ();
        int id= Integer.parseInt (productModel.getId ());

        if(products.size ()<0){
            layout.setVisibility (View.GONE);
        }else {
            layout.setVisibility (View.VISIBLE);
        }


        if(products.size ()>1){
            quantity.setText (String.valueOf (products.size ()+" PRODUCTS ADDED"));
        }else{
            quantity.setText (String.valueOf (products.size ()+" PRODUCT ADDED"));
        }


        Boolean check=productDao.is_exist(id);
        if(check==false){
            productDao.insertrecord (new Product (id,productModel.getTittle (),productModel.getImage (),Integer.parseInt (productModel.getPrice ()),1,productModel.getDiscount (),productModel.getDescription ()));
        }else {
//            Toast.makeText (context, "Item Exist", Toast.LENGTH_SHORT).show ( );
        }





    }


    private void quantityMinus(MyHolder holder, Item productModel, int position) {
        holder.binding.remove.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                int qnt=modelListFilter.get (position).getQty ();
                if(qnt>1)
                    qnt--;

                productModel.setQty (qnt);
                holder.binding.quantity.setText (String.valueOf (qnt));

                SharedPreferences sp=context.getSharedPreferences("ProductQnty"+productModel.getId (),MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("qnt", String.valueOf (qnt));
                editor.apply();

                int sum=0,add=0,i;
                for(i=0;i< modelListFilter.size();i++) {
                    int price = Integer.parseInt (modelListFilter.get (position).getPrice ( ));
                    sum = price * qnt;
//                    quantityDialogBinding.pprice.setText ("₹" + sum);
                    notifyDataSetChanged ( );
                }
            }
        });




    }

    private void quantityPlus(MyHolder holder, Item productModel, int position) {

        holder.binding.add.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                int qnt=modelListFilter.get (position).getQty ();
                qnt++;
                productModel.setQty (qnt);

                holder.binding.quantity.setText (String.valueOf (qnt));

                SharedPreferences sp=context.getSharedPreferences("ProductQnty"+productModel.getId (),MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("qnt", String.valueOf (qnt));
                editor.apply();

                int sum=0,add=0,i;
                for(i=0;i< modelListFilter.size();i++) {
                    int price = Integer.parseInt (modelListFilter.get (position).getPrice ( ));
                    sum = price * qnt;


//                    holder.binding.pprice.setText ("₹" + sum);
                    notifyDataSetChanged ( );
                }
            }
        });
    }

    private void removeItem(MyHolder holder, int position, Item productModel) {

//        layout.setVisibility (View.VISIBLE);

        int id= Integer.parseInt (productModel.getId ());
        AppDatabase db = Room.databaseBuilder(holder.binding.remove.getContext(),
                AppDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        List<Product> products=productDao.getallproduct ();


        if(products.size ()>2){
            quantity.setText (String.valueOf (products.size ()-1+" Products in cart"));
        }else {
            quantity.setText (String.valueOf (products.size ()-1+ " Product in cart"));
        }
        if(products.size ()==1){
            layout.setVisibility (View.GONE);
        }



        productDao.deleteById(id);
        holder.binding.remove.setVisibility (View.INVISIBLE);
        holder.binding.cart.setVisibility (View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return modelListFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charcater = constraint.toString();
                if (charcater.isEmpty()){
                    modelListFilter = modelList ;
                }else{
                    List<Item> filterList = new ArrayList<>();
                    for (Item row: modelList){
                        if (row.getTittle ().toLowerCase().contains(charcater.toLowerCase())){
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
                modelListFilter = (ArrayList<Item>) results.values ;
                notifyDataSetChanged();
            }
        };
    }

    public void addProduct(Item productModel){
        modelListFilter.add (productModel);
        notifyDataSetChanged ();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        NewitemProductBinding binding;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            binding=NewitemProductBinding.bind (itemView);
        }


    }



    private void bottomSheet(MyHolder holder, Item productModel, int position, List<Item> modelListFilter, TextView cart, TextView remove){
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        CartSheetBinding quantityDialogBinding = CartSheetBinding.inflate(LayoutInflater.from(context));

//        quantityDialogBinding.container.setBackgroundResource (R.drawable.catbg);

        bottomSheetDialog.setContentView (quantityDialogBinding.getRoot ());

        quantityDialogBinding.pname.setText (productModel.getTittle ());
        quantityDialogBinding.pprice.setText ("₹"+productModel.getPrice ());


        RecyclerView colorList=quantityDialogBinding.colourList;

        Button save=quantityDialogBinding.saveBtn;

        AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
        ProductDao productDao=db.ProductDao();


        sizeAdapter=new SizeAdapter (context,colorAdapter,colorList,productModel,save)  ;
        LinearLayoutManager layoutManager = new GridLayoutManager (context, 4);
        quantityDialogBinding.sizeList.setLayoutManager (layoutManager);
        quantityDialogBinding.sizeList.setAdapter (sizeAdapter);

        colorAdapter=new ColorAdapter (context) ;
        LinearLayoutManager layoutManager1 = new GridLayoutManager (context, 4);
        quantityDialogBinding.colourList.setLayoutManager (layoutManager1);
        quantityDialogBinding.colourList.setAdapter (colorAdapter);

        sizeList(productModel,save);



        final List<SlideModel>imageList=new ArrayList<> ();
        FirebaseDatabase.getInstance ().getReference ("productimages").child (productModel.getId ()).addListenerForSingleValueEvent (new ValueEventListener ( ) {
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
                int qnt=modelListFilter.get (position).getQty ();
                qnt++;
                productModel.setQty (qnt);
                quantityDialogBinding.quantity.setText (String.valueOf (qnt));

                SharedPreferences sp=context.getSharedPreferences("ProductQnty"+productModel.getId (),MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("qnt", String.valueOf (qnt));
                editor.apply();


//                String qtty = sp.getString ("ProductQnty"+productModel, "1");

//                quantityDialogBinding.quantity.setText (qtty);

                int sum=0,add=0,i;
                for(i=0;i< modelListFilter.size();i++) {
                    int price= Integer.parseInt (modelListFilter.get (position).getPrice ());
                    sum=price*qnt;


                    quantityDialogBinding.pprice.setText ("₹"+sum);
                    notifyDataSetChanged ();

                }
            }
        });
        quantityDialogBinding.reduce.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                int qnt=modelListFilter.get (position).getQty ();
                if(qnt>1)
                    qnt--;

                productModel.setQty (qnt);
                quantityDialogBinding.quantity.setText (String.valueOf (qnt));

                SharedPreferences sp=context.getSharedPreferences("ProductQnty"+productModel.getId (),MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("qnt", String.valueOf (qnt));
                editor.apply();

                int sum=0,add=0,i;
                for(i=0;i< modelListFilter.size();i++) {
                    int price= Integer.parseInt (modelListFilter.get (position).getPrice ());
                    sum=price*qnt;
                    quantityDialogBinding.pprice.setText ("₹"+sum);
                    notifyDataSetChanged ();
                }
            }
        });

        quantityDialogBinding.saveBtn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v){
                AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
                ProductDao productDao=db.ProductDao();
                int id= Integer.parseInt (productModel.getId ());

                SharedPreferences sp2=context.getSharedPreferences("ProductColor",MODE_PRIVATE);

                SharedPreferences sp=context.getSharedPreferences("ProductSize",MODE_PRIVATE);

                SharedPreferences sp3=context.getSharedPreferences("ProductQnty"+productModel.getId (),MODE_PRIVATE);

                String quantit=sp3.getString ("qnt","1");
                String size = sp.getString ("size", "")+"-"+sp2.getString ("color", "");

                Boolean check = productDao.is_exist (id);
                if (check == false){
                    productDao.insertrecord (new Product (id, productModel.getTittle ( )+"-"+size, productModel.getImage ( ), Integer.parseInt (productModel.getPrice ( )), Integer.parseInt (quantit), productModel.getDiscount (), productModel.description));
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

                    editor2.remove ("qnt");
                    editor2.apply ();

                    editor.remove ("ProductQnty"+productModel.getId ());
                    editor.apply ();

                    cart.setVisibility (View.INVISIBLE);
                    remove.setVisibility (View.VISIBLE);

                    addtocart(holder,productModel,position);

                    notifyDataSetChanged ();



                } else {
                    Toast.makeText (context, "Item Exist", Toast.LENGTH_SHORT).show ( );
                }


            }





        });




        bottomSheetDialog.show ();
    }

    private void sizeList(Item productModel, Button save) {
        FirebaseFirestore.getInstance ()
                .collection ("productSize")
                .whereEqualTo ("id",productModel.getId ())
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

    private void addtocart(MyHolder holder, Item productModel, int position) {
        holder.binding.cart.setVisibility (View.GONE);
        holder.binding.remove.setVisibility (View.GONE);
        layout.setVisibility (View.VISIBLE);

        AppDatabase db= Room.databaseBuilder(context,AppDatabase.class,"cart_db").allowMainThreadQueries().build();
        ProductDao productDao=db.ProductDao();
        List<Product> products=productDao.getallproduct ();
        int id= Integer.parseInt (productModel.getId ());

        if(products.size ()<0){
            layout.setVisibility (View.GONE);
        }else {
            layout.setVisibility (View.VISIBLE);
        }


        if(products.size ()>1){
            quantity.setText (String.valueOf (products.size ()+" PRODUCTS ADDED"));
        }else{
            quantity.setText (String.valueOf (products.size ()+" PRODUCT ADDED"));
        }


//        if(products.size ()>0){
//            quantity.setText (String.valueOf (products.size ()+" PRODUCTS ADDED"));
//        }else {
//            quantity.setText (String.valueOf (products.size ()+" PRODUCT ADDED"));
//        }

        Boolean check=productDao.is_exist(id);
        if(check==false){
            productDao.insertrecord (new Product (id,productModel.getTittle (),productModel.getImage (),Integer.parseInt (productModel.getPrice ()),1,productModel.getDiscount (),productModel.getDescription ()));
        }else {
//            Toast.makeText (context, "Item Exist", Toast.LENGTH_SHORT).show ( );
        }



    }
}