package com.flinkmart.mahi.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.flinkmart.mahi.FirebaseUtil.FirebaseUtil;
import com.flinkmart.mahi.activities.SearchActivity;
import com.flinkmart.mahi.activities.SubCatListActivity;
import com.flinkmart.mahi.adapter.CategoryListAdapter;
import com.flinkmart.mahi.storeadapter.StoreAdapter;
import com.flinkmart.mahi.categoryAdapter.CatAdapter1;
import com.flinkmart.mahi.categoryAdapter.CatAdapter2;
import com.flinkmart.mahi.categorymodel.CategoryModel1;
import com.flinkmart.mahi.categorymodel.CategoryModel2;
import com.flinkmart.mahi.categoryAdapter.BeautyCategoryAdapter;
import com.flinkmart.mahi.categoryAdapter.WeGotYouCoveredCategory;
import com.flinkmart.mahi.homeadapter.BottomAdapter;
import com.flinkmart.mahi.adapter.BeautyListAdapter;
import com.flinkmart.mahi.homeadapter.BundleAdapter;
import com.flinkmart.mahi.lifestyleadapter.LifstyleAdapter;
import com.flinkmart.mahi.adapter.SubCategoryItemAdapter;
import com.flinkmart.mahi.databinding.FragmentHomeBinding;
import com.flinkmart.mahi.homeadapter.BestSellerAdapter;
import com.flinkmart.mahi.homeadapter.LatestProductAdapter;



import com.flinkmart.mahi.homemodel.BestSellerModel;
import com.flinkmart.mahi.model.Catlist;
import com.flinkmart.mahi.lifestylemodel.ElectronicCategory;
import com.flinkmart.mahi.lifestylemodel.FootwearCategory;
import com.flinkmart.mahi.homemodel.BottomModel;
import com.flinkmart.mahi.model.HorizonProductModel;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.lifestylemodel.LifstyleCategory;
import com.flinkmart.mahi.storemodel.StoreModel;
import com.flinkmart.mahi.model.UserModel;
import com.flinkmart.mahi.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    CategoryListAdapter catlistAdapter;
    SubCategoryItemAdapter itemAdapter;

    StoreAdapter restuarantAdapter;
    List<StoreModel> branchemodel=new ArrayList<> ();

    List<Catlist>catlists= new ArrayList<> ();

    BeautyListAdapter dealListAdapter;
    private List<Item> itemList;
    List<Item>productLists=new ArrayList<> (  );
    BundleAdapter horizonProductAdapter;
    List<HorizonProductModel>horizonProductModels=new ArrayList<> ();
    UserModel userModel;

    ///DaillyNeeds
    BestSellerAdapter itemAdapter1;
    LatestProductAdapter itemAdapter2;


    CatAdapter1 catAdapter1;
    CatAdapter2 catAdapter2;


    List<BestSellerModel>item1s=new ArrayList<> (  );
    List<Item>item2s=new ArrayList<> (  );

    List<CategoryModel1>categoryModel1s=new ArrayList<> (  );
    List<CategoryModel2>categoryModel2s=new ArrayList<> (  );
   //lifestyle

    LifstyleAdapter lifstyleAdapter;
    WeGotYouCoveredCategory footwearAdapter;
    BeautyCategoryAdapter electronicAdapter;
    BottomAdapter bottomAdapter;

    List<LifstyleCategory>lifstyleCategories=new ArrayList<> (  );
    List<FootwearCategory>footwearCategories=new ArrayList<> (  );
    List<ElectronicCategory>electronicCategories=new ArrayList<> (  );
    List<BottomModel>footwearItems=new ArrayList<> (  );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        binding.shimmer.startShimmer ();
        binding.shimmer.setVisibility (View.VISIBLE);
        binding.home.setVisibility (View.INVISIBLE);



        binding.searchBar.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (getActivity (), SearchActivity.class));
            }
        });


        itemList=new ArrayList<> ();
        setUpRecyclerView ();
        getUsername();
        banner ();
        initBundleProduct();
        initCategory();
        initTryCategory();
        initwegotcategory ();

        initBestsellerProduct();
        initTrendingProduct();
        initEssantialProduct();
        initwegotcovered();

        initClothing();
        initsubcat2();
        initsubcat1();

        return  view;

    }



    private  void banner() {
        final List<SlideModel>imageList=new ArrayList<> ();
        FirebaseDatabase.getInstance ().getReference ().child ("banner").addListenerForSingleValueEvent (new ValueEventListener ( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren ( )) {
                    imageList.add (new SlideModel (ds.child ("image").getValue (  ).toString (), ds.child ("tittle").getValue (  ).toString (), ScaleTypes.FIT));
                    binding.carousel.setImageList (imageList,ScaleTypes.FIT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                RequestQueue queue = Volley.newRequestQueue (getActivity ());
                StringRequest request = new StringRequest (Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
                    try {
                        JSONObject object = new JSONObject (response);
                        if (object.getString ("status").equals ("success")) {
                            JSONArray offerArray = object.getJSONArray ("news_infos");
                            for (int i = 0; i < offerArray.length ( ); i++) {
                                JSONObject childObj = offerArray.getJSONObject (i);
                                imageList.add (new SlideModel (Constants.NEWS_IMAGE_URL + childObj.getString ("image"), "", ScaleTypes.FIT));
                                binding.carousel.setImageList (imageList,ScaleTypes.FIT);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace ( );
                    }
                }, ( error1) -> {
                });
                queue.add (request);
            }
        });




    }
    private void initBundleProduct(){
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .whereEqualTo ("show",true)
                .whereEqualTo ("category","package")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {

                            HorizonProductModel productList = ds.toObject (HorizonProductModel.class);
                            horizonProductAdapter.addProduct(productList);
                        }

                    }
                });

    }
    private void initCategory(){
            FirebaseFirestore.getInstance ( )
                    .collection ("category")
                    .whereEqualTo ("tag","1")
                    .whereEqualTo ("show",true)
                    .get ()
                    .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                            for (DocumentSnapshot ds : dsList) {
                                binding.shimmer.stopShimmer ();
                                binding.home.setVisibility (View.VISIBLE);
                                binding.shimmer.setVisibility (View.INVISIBLE);
                                Catlist catlist = ds.toObject (Catlist.class);
                                catlistAdapter.addProduct(catlist);
                            }
                        }
                    });


    }

   private void  restuarant(String pin){
        FirebaseFirestore.getInstance ()
                .collection ("Restaurant")
                .whereEqualTo ("pincode",pin)
                .whereEqualTo ("category","restaurant")
                .whereEqualTo ("show",true)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            StoreModel resturants=ds.toObject (StoreModel.class);
                            restuarantAdapter.addProduct (resturants);
                        }

                    }
                });
    }
    private void  store(String pin){
        FirebaseFirestore.getInstance ()
                .collection ("Restaurant")
                .whereEqualTo ("category","store")
                .whereEqualTo ("show",true)
                .get ()
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList=queryDocumentSnapshots.getDocuments ();
                        for (DocumentSnapshot ds:dsList){
                            StoreModel resturants=ds.toObject (StoreModel.class);
                            restuarantAdapter.addProduct (resturants);
                        }

                    }
                });
    }

    private void initTryCategory() {
        final List<SlideModel>imageList=new ArrayList<> ();
        FirebaseFirestore.getInstance ( )
                .collection ("category")
                .whereEqualTo ("show",true)
                .whereEqualTo ("tag","2")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            CategoryModel1 catlist = ds.toObject (CategoryModel1.class);
                            catAdapter1.addProduct(catlist);
                            imageList.add (new SlideModel(catlist.getImage ().toString (), catlist.getId (), ScaleTypes.FIT));
                            binding.carousel2.setImageList (imageList,ScaleTypes.FIT);



                            binding.carousel2.setItemClickListener (new ItemClickListener ( ) {
                                @Override
                                public void onItemSelected(int i) {
                                    Intent intent = new Intent(getActivity (), SubCatListActivity.class);
                                    intent.putExtra("category",imageList.get (i).getTitle ());
                                    intent.putExtra("categoryName","Back");
                                    startActivity(intent);
                                }
                            });

                        }

                    }
                });
    }
    private void initwegotcategory(){
        FirebaseFirestore.getInstance ( )
                .collection ("category")
                .whereEqualTo ("show",true)
                .whereEqualTo ("tag","3")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            FootwearCategory catlist = ds.toObject (FootwearCategory.class);
                            footwearAdapter.addProduct(catlist);
                        }

                    }
                });
    }
    private void initBestsellerProduct() {
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .whereEqualTo ("show",true)
                .whereEqualTo ("category","latest")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            Item productList = ds.toObject (Item.class);
                            itemAdapter2.addProduct(productList);
                        }
                    }
                });

    }
    private void initTrendingProduct() {
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .whereEqualTo ("show",true)
                .whereEqualTo ("category","trending")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            BestSellerModel productList = ds.toObject (BestSellerModel.class);
                            itemAdapter1.addProduct(productList);
                        }

                    }
                });
    }
    private void initwegotcovered() {
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .whereEqualTo ("category","1759")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            BottomModel productList = ds.toObject (BottomModel.class);
                            bottomAdapter.addProduct(productList);
                        }

                    }
                });
    }

    private void initEssantialProduct(){
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .whereEqualTo ("show",true)
                .whereEqualTo ("category","1759")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            CategoryModel2 catlist = ds.toObject (CategoryModel2.class);
                            catAdapter2.addProduct(catlist);
                        }

                    }
                });
    }


    private void initsubcat1(){
        final List<SlideModel>imageList=new ArrayList<> ();
        FirebaseFirestore.getInstance ( )
                .collection ("subcategory")
                .limit (12)
                .whereEqualTo ("show",true)
                .whereEqualTo ("catname","8309")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            ElectronicCategory catlist = ds.toObject (ElectronicCategory.class);
                            electronicAdapter.addProduct(catlist);


                        }

                    }
                });
    }


    private void initsubcat2(){
        final List<SlideModel>imageList=new ArrayList<> ();
        FirebaseFirestore.getInstance ( )
                .collection ("subcategory")
                .whereEqualTo ("show",true)
                .whereEqualTo ("catname","2889")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            LifstyleCategory catlist = ds.toObject (LifstyleCategory.class);
                            lifstyleAdapter.addProduct(catlist);
                        }

                    }
                });
    }
    private void initClothing(){
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .limit (24)
                .whereEqualTo ("category","8309")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            Item productList = ds.toObject (Item.class);
                            dealListAdapter.addProduct(productList);
                        }

                    }
                });
    }

    void  getUsername(){
        FirebaseUtil.currentUserDetails ().get ().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot> ( ) {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful ())  {

                    userModel=  task.getResult ().toObject (UserModel.class);
                    if(userModel!=null){
//                        binding.textView4.setText ("Hi "+userModel.getUsername ());
//                        binding.textView11.setText (userModel.getAddress ());
                        String pin=userModel.getPin ();

                        restuarant (pin);
                        store (pin);

                    }
                }
            }
        });

    }

    private void setUpRecyclerView() {

        //category
        binding.categoryList.setLayoutManager (new GridLayoutManager (getContext (),4));
        catlistAdapter=new CategoryListAdapter (getContext (),catlists);
        binding.categoryList.setAdapter (catlistAdapter);

        binding.horizonProduct.setLayoutManager (new LinearLayoutManager (getActivity (),LinearLayoutManager.HORIZONTAL,false));
        horizonProductAdapter=new BundleAdapter (getContext (),horizonProductModels);
        binding.horizonProduct.setAdapter (horizonProductAdapter);

        //Try Category
        binding.TryCategory.setLayoutManager (new LinearLayoutManager (getActivity (),LinearLayoutManager.HORIZONTAL,false));
        catAdapter1=new CatAdapter1 (getContext (),categoryModel1s);
        binding.TryCategory.setAdapter (catAdapter1);

        binding.grocceryList.setLayoutManager (new LinearLayoutManager (getActivity (),LinearLayoutManager.HORIZONTAL,false));
        itemAdapter1=new BestSellerAdapter (getContext (),item1s);
        binding.grocceryList.setAdapter (itemAdapter1);




        //leatest Product
        binding.LatestProduct.setLayoutManager (new GridLayoutManager (getActivity (),3));
        itemAdapter2=new LatestProductAdapter (getContext (),item2s);
        binding.LatestProduct.setAdapter (itemAdapter2);


        //Electronics
        binding.beautySubcategory.setLayoutManager (new GridLayoutManager (getContext (),4));
        electronicAdapter=new BeautyCategoryAdapter (getContext (),electronicCategories);
        binding.beautySubcategory.setAdapter (electronicAdapter);
        //electronicsList


        //Beauty Product-------------------
        binding.beautyList.setLayoutManager (new GridLayoutManager (getActivity (),3));
        dealListAdapter=new BeautyListAdapter (getContext (),productLists);
        binding.beautyList.setAdapter (dealListAdapter);

        //We got you covered
        binding.weGotYouCovered.setLayoutManager (new  LinearLayoutManager (getActivity (),LinearLayoutManager.HORIZONTAL,false));
        footwearAdapter=new WeGotYouCoveredCategory (getContext (),footwearCategories);
        binding.weGotYouCovered.setAdapter (footwearAdapter);


        binding.restuarant.setLayoutManager (new GridLayoutManager (getActivity (),3));
        restuarantAdapter=new StoreAdapter (getContext (),branchemodel);
        binding.restuarant.setAdapter (restuarantAdapter);

    }




}