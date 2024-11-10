package com.flinkmart.mahi.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.flinkmart.mahi.fragmentessantials.AllFragment;
import com.flinkmart.mahi.fragmentessantials.KidFragment;
import com.flinkmart.mahi.fragmentessantials.ManFragment;
import com.flinkmart.mahi.fragmentessantials.WomenFragment;

public class ViewPagerAdapter4 extends FragmentStateAdapter {
    public ViewPagerAdapter4(@NonNull FragmentActivity fragmentActivity) {

        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new AllFragment ();
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