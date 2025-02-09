package com.flinkmart.mahi.lifestyleactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.scrab.FavouriteActivity;
import com.flinkmart.mahi.scrab.NewCartActivity;
import com.flinkmart.mahi.adapter.FilterAdapter;
import com.flinkmart.mahi.databinding.ActivityProductListBinding;
import com.flinkmart.mahi.fashionfragment.KidFragment;
import com.flinkmart.mahi.fashionfragment.ManFragment;
import com.flinkmart.mahi.fashionfragment.WomenFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class ClothingListActivity extends AppCompatActivity{
   ActivityProductListBinding binding;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FilterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProductListBinding.inflate(getLayoutInflater ());
        setContentView (binding.getRoot ());


//        tabLayout = findViewById(R.id.tab);
//        viewPager2 =findViewById(R.id.viewpager);
//        myViewPagerAdapter = new ViewPagerAdapter (this);
//        viewPager2.setAdapter(myViewPagerAdapter);

        String catt=getIntent ().getStringExtra ("category").toUpperCase ();






//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager2.setCurrentItem(tab.getPosition());
//            }
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                tabLayout.getTabAt(position).select();
//            }
//        });
        loadFragment(new KidFragment());
        binding.bottomNavigation.setOnItemSelectedListener (new NavigationBarView.OnItemSelectedListener ( ) {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id=item.getItemId ();
                if (id==R.id.kid){
                    loadFragment(new KidFragment ());
                    return true;
                } else if (id==R.id.men) {
                    loadFragment(new ManFragment ());
                    return true;
                }else if (id==R.id.women) {
                    loadFragment(new WomenFragment ());
                    return true;}
                return false;
            }
        });


        getSupportActionBar().setTitle(catt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager ( );
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction ();
        fragmentTransaction.replace (R.id.container,fragment);
        fragmentTransaction.addToBackStack (null);
        fragmentTransaction.commit ();
        String cat=getIntent ().getStringExtra ("category");
        Bundle bundle=new Bundle (  );
        bundle.putString ("category",cat);
        fragment.setArguments (bundle);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.cart) {
            startActivity(new Intent (this, NewCartActivity.class));
        } else if (item.getItemId() == R.id.fav) {
            startActivity(new Intent (this, FavouriteActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}