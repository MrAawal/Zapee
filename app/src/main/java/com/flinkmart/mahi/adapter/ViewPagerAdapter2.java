package com.flinkmart.mahi.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.flinkmart.mahi.electronics.AllFragment;
import com.flinkmart.mahi.electronics.KidFragment;
import com.flinkmart.mahi.electronics.ManFragment;
import com.flinkmart.mahi.electronics.WomenFragment;

public class ViewPagerAdapter2 extends FragmentStateAdapter {
    public ViewPagerAdapter2(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new com.flinkmart.mahi.electronics.AllFragment ();
            case 1:
                return new KidFragment ();
            case 2:
                return new ManFragment ();
            case 3:
                return new WomenFragment ();
            default:
                return new AllFragment ();
        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }
}