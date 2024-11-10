package com.flinkmart.mahi.Fragment;

import android.os.Bundle;

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
import com.flinkmart.mahi.adapter.CategoryListAdapter;
import com.flinkmart.mahi.categoryAdapter.CatAdapter1;
import com.flinkmart.mahi.categoryAdapter.CatAdapter2;
import com.flinkmart.mahi.categorymodel.CategoryModel1;
import com.flinkmart.mahi.categorymodel.CategoryModel2;
import com.flinkmart.mahi.lifestyleadapter.ElectronicAdapter;
import com.flinkmart.mahi.categoryAdapter.CatAdapter5;
import com.flinkmart.mahi.homeadapter.BottomAdapter;
import com.flinkmart.mahi.adapter.ItemAdapter;
import com.flinkmart.mahi.adapter.HorizonProductAdapter;
import com.flinkmart.mahi.lifestyleadapter.LifstyleAdapter;
import com.flinkmart.mahi.adapter.SubCategoryItemAdapter;
import com.flinkmart.mahi.databinding.FragmentHomeBinding;
import com.flinkmart.mahi.homeadapter.BestSellerAdapter;
import com.flinkmart.mahi.homeadapter.TrendingAdapter;



import com.flinkmart.mahi.homemodel.BestSellerModel;
import com.flinkmart.mahi.homemodel.TrendingModel;
import com.flinkmart.mahi.model.Catlist;
import com.flinkmart.mahi.lifestylemodel.ElectronicCategory;
import com.flinkmart.mahi.lifestylemodel.FootwearCategory;
import com.flinkmart.mahi.homemodel.BottomModel;
import com.flinkmart.mahi.model.HorizonProductModel;
import com.flinkmart.mahi.model.Item;
import com.flinkmart.mahi.lifestylemodel.LifstyleCategory;
import com.flinkmart.mahi.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    CategoryListAdapter catlistAdapter;
    SubCategoryItemAdapter itemAdapter;
    List<Catlist>catlists= new ArrayList<> ();
    ItemAdapter dealListAdapter;
    private List<Item> itemList;
    List<Item>productLists=new ArrayList<> (  );
    HorizonProductAdapter horizonProductAdapter;
    List<HorizonProductModel>horizonProductModels=new ArrayList<> ();


    ///DaillyNeeds
    BestSellerAdapter itemAdapter1;
    TrendingAdapter itemAdapter2;


    CatAdapter1 catAdapter1;
    CatAdapter2 catAdapter2;



    List<BestSellerModel>item1s=new ArrayList<> (  );
    List<TrendingModel>item2s=new ArrayList<> (  );


    List<CategoryModel1>categoryModel1s=new ArrayList<> (  );
    List<CategoryModel2>categoryModel2s=new ArrayList<> (  );
   //lifestyle

    LifstyleAdapter lifstyleAdapter;
    CatAdapter5 footwearAdapter;
    ElectronicAdapter electronicAdapter;
    BottomAdapter footwearItemAdapter;

    List<LifstyleCategory>lifstyleCategories=new ArrayList<> (  );
    List<FootwearCategory>footwearCategories=new ArrayList<> (  );
    List<ElectronicCategory>electronicCategories=new ArrayList<> (  );
    List<BottomModel>footwearItems=new ArrayList<> (  );



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        itemList=new ArrayList<> ();
        setUpRecyclerView ();

        banner ();
        initHorizonElectronics();
        initCategory();
        initTryCategory();
        initGrocceryProduct();
        initEssantial();
        initEssantialProduct();

        initClothing();
        initlifstyle();
        initfootwear ();
        initelectronics();
        footwearFashion();
        return  view;

    }


    private  void banner() {
        RequestQueue queue = Volley.newRequestQueue (getActivity ());

        StringRequest request = new StringRequest (Request.Method.GET, Constants.GET_OFFERS_URL, response -> {
            try {
                JSONObject object = new JSONObject (response);
                if (object.getString ("status").equals ("success")) {
                    JSONArray offerArray = object.getJSONArray ("news_infos");
                    for (int i = 0; i < offerArray.length ( ); i++) {
                        JSONObject childObj = offerArray.getJSONObject (i);
                        binding.carousel.addData (
                                new CarouselItem (
                                        Constants.NEWS_IMAGE_URL + childObj.getString ("image"),
                                        childObj.getString ("title")
                                )
                        );
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }, error -> {
        });
        queue.add (request);
    }

    private void initCategory() {
        FirebaseFirestore.getInstance ( )
                .collection ("category")
                .whereEqualTo ("show",true)
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            Catlist catlist = ds.toObject (Catlist.class);
                            catlistAdapter.addProduct(catlist);
                        }

                    }
                });
    }
    private void initTryCategory() {
        FirebaseFirestore.getInstance ( )
                .collection ("category")
                .whereEqualTo ("show",true)
                .whereEqualTo ("tag","1")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            CategoryModel1 catlist = ds.toObject (CategoryModel1.class);
                            catAdapter1.addProduct(catlist);
                        }

                    }
                });
    }

    private void initGrocceryProduct() {
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .limit (6)
                .whereEqualTo ("show",true)
                .whereEqualTo ("category","Groccery & Essantials")

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
    private void initEssantial(){
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
                            CategoryModel2 catlist = ds.toObject (CategoryModel2.class);
                            catAdapter2.addProduct(catlist);
                        }

                    }
                });
    }

    private void initEssantialProduct() {
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .limit (12)
                .whereEqualTo ("show",true)
                .whereEqualTo ("stock","10")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            TrendingModel productList = ds.toObject (TrendingModel.class);
                            itemAdapter2.addProduct(productList);
                        }

                    }
                });
    }

    private void initelectronics(){
        FirebaseFirestore.getInstance ( )
                .collection ("lifestyle")
                .whereEqualTo ("show",true)
                .whereEqualTo ("tittle","electronic")
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
    private void initHorizonElectronics(){
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .limit (12)
                .whereEqualTo ("show",true)
                .whereEqualTo ("stock","1")
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
    private void initlifstyle() {
        FirebaseFirestore.getInstance ( )
                .collection ("lifestyle")
                .whereEqualTo ("show",true)
                .whereEqualTo ("tittle","fashion")
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
                .whereEqualTo ("show",true)
                .whereEqualTo ("stock","1")
                .limit (6)
                .whereEqualTo ("category","clothing")
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
    private void initfootwear(){
        FirebaseFirestore.getInstance ( )
                .collection ("category")
                .whereEqualTo ("show",false)
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
    private void footwearFashion() {
        FirebaseFirestore.getInstance ( )
                .collection ("product")
                .limit (6)
                .whereEqualTo ("show",true)
                .whereEqualTo ("category","footwear")
                .get ( )
                .addOnSuccessListener (new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments ( );
                        for (DocumentSnapshot ds : dsList) {
                            BottomModel productList = ds.toObject (BottomModel.class);
                            footwearItemAdapter.addProduct(productList);
                        }

                    }
                });
    }



    private void setUpRecyclerView() {

        //category
        binding.categoryList.setLayoutManager (new GridLayoutManager (getContext (),4));
        catlistAdapter=new CategoryListAdapter (getContext (),catlists);
        binding.categoryList.setAdapter (catlistAdapter);

        //Try Category
        binding.TryCategory.setLayoutManager (new GridLayoutManager (getContext (),2));
        catAdapter1=new CatAdapter1 (getContext (),categoryModel1s);
        binding.TryCategory.setAdapter (catAdapter1);

        binding.grocceryList.setLayoutManager (new LinearLayoutManager (getActivity (),LinearLayoutManager.HORIZONTAL,false));
        itemAdapter1=new BestSellerAdapter (getContext (),item1s);
        binding.grocceryList.setAdapter (itemAdapter1);

        //essantial
        binding.essantials.setLayoutManager (new GridLayoutManager (getActivity (),3));
        catAdapter2=new CatAdapter2 (getContext (),categoryModel2s);
        binding.essantials.setAdapter (catAdapter2);

        binding.essantialsList.setLayoutManager (new GridLayoutManager (getActivity (),2));
        itemAdapter2=new TrendingAdapter (getContext (),item2s);
        binding.essantialsList.setAdapter (itemAdapter2);


        //Electronics
        binding.electronic.setLayoutManager (new LinearLayoutManager (getActivity (),LinearLayoutManager.HORIZONTAL,false));
        electronicAdapter=new ElectronicAdapter (getContext (),electronicCategories);
        binding.electronic.setAdapter (electronicAdapter);
        //electronicsList

        binding.horizonProduct.setLayoutManager (new LinearLayoutManager (getActivity (),LinearLayoutManager.HORIZONTAL,false));
        horizonProductAdapter=new HorizonProductAdapter (getContext (),horizonProductModels);
        binding.horizonProduct.setAdapter (horizonProductAdapter);

        /////fashion
        binding.fashionList.setLayoutManager (new LinearLayoutManager (getActivity (),LinearLayoutManager.HORIZONTAL,false));
        lifstyleAdapter=new LifstyleAdapter (getContext (),lifstyleCategories);
        binding.fashionList.setAdapter (lifstyleAdapter);

        //FashionList-------------------
        binding.DealsList.setLayoutManager (new GridLayoutManager (getActivity (),3));
        dealListAdapter=new ItemAdapter (getContext (),productLists);
        binding.DealsList.setAdapter (dealListAdapter);

        //We got you covered
        binding.footwearList.setLayoutManager (new  GridLayoutManager (getActivity (),2));
        footwearAdapter=new CatAdapter5 (getContext (),footwearCategories);
        binding.footwearList.setAdapter (footwearAdapter);

        binding.footwear.setLayoutManager (new GridLayoutManager (getActivity (),2));
        footwearItemAdapter=new BottomAdapter (getContext (),footwearItems);
        binding.footwear.setAdapter (footwearItemAdapter);

    }
}