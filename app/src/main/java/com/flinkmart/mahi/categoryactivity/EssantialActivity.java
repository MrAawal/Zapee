package com.flinkmart.mahi.categoryactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.activities.FavouriteActivity;
import com.flinkmart.mahi.activities.NewCartActivity;
import com.flinkmart.mahi.adapter.ViewPagerAdapter4;
import com.google.android.material.tabs.TabLayout;
public class EssantialActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter4 myViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essantial);
        String category=getIntent ().getStringExtra ("category");

        getSupportActionBar().setTitle(category);


        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 =findViewById(R.id.view_pager);
        myViewPagerAdapter = new ViewPagerAdapter4(this);
        viewPager2.setAdapter(myViewPagerAdapter);



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment=new Fragment ();
                Bundle bundle=new Bundle ( );
                bundle.putString ("category",category);
                fragment.setArguments (bundle);

                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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