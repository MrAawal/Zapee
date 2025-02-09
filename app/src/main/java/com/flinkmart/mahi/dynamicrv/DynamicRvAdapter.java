package com.flinkmart.mahi.dynamicrv;


import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.NewitemProductBinding;
import com.flinkmart.mahi.model.Item;

import java.util.List;

class  LoadingViewHolder extends RecyclerView.ViewHolder {

    public LoadingViewHolder(@NonNull View itemView) {
        super (itemView);
    }
}

class ItemViewHolder extends  RecyclerView.ViewHolder{

    NewitemProductBinding binding;
    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        binding=NewitemProductBinding.bind (itemView);
    }

}


public class DynamicRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private  final  int VIEW_TYPE_ITEM=0,VIEW_TYPE_LOADING=1;
    LoadMore loadMore;
    boolean isLoding;

    Activity activity;

    List<Item> items;
    int visibleThreshold=5;
    int lastVisibleItem,totalItemCount;

    public DynamicRvAdapter(RecyclerView recyclerView,Activity activity, List<Item> items) {
        this.activity = activity;
        this.items = items;


        final LinearLayoutManager linearLayoutManager=(LinearLayoutManager) recyclerView.getLayoutManager ();

        recyclerView.addOnScrollListener (new RecyclerView.OnScrollListener ( ) {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled (recyclerView, dx, dy);

                totalItemCount=linearLayoutManager.getItemCount ();
                lastVisibleItem=linearLayoutManager.findLastVisibleItemPosition ();
                if(isLoding && totalItemCount<=(lastVisibleItem+visibleThreshold)){

                    if (loadMore!=null){
                        loadMore.onLoadMore ();
                        isLoding=true;
                    }


                }
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return items.get (position)==null?VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    public void setLoadMore(LoadMore loadMore){
        this.loadMore=loadMore;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==VIEW_TYPE_ITEM){
            View view= LayoutInflater.from (activity).inflate (R.layout.newitem_product,parent,false);
            return new LoadingViewHolder (view);
        } else if (viewType==VIEW_TYPE_LOADING) {
            Toast.makeText (activity, "Loading...", Toast.LENGTH_SHORT).show ( );
//           View view= LayoutInflater.from (activity).inflate (R.layout.item_categories,parent,false);
//           return new LoadingViewHolder (view);
        }


        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof ItemViewHolder){
            Item item=items.get (position);
            ItemViewHolder viewHolder=(ItemViewHolder)holder;

            viewHolder.binding.label.setText (item.getTittle ());



        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder=(LoadingViewHolder) holder;
        }

    }

    @Override
    public int getItemCount() {
        return items.size ();
    }

    public void setLoded(){
        isLoding=false;
    }

    public void addProduct(Item productModel){
        items.add (productModel);
        notifyDataSetChanged ();
    }

}
