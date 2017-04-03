package com.zaafoo.preorder.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.zaafoo.preorder.fragments.RestaurantInfoFragment;
import com.zaafoo.preorder.fragments.RestaurantMenuDisplayFragment;
import com.zaafoo.preorder.fragments.RestaurantReviewFragment;

/**
 * Created by SUB on 3/29/2017.
 */

public class RestaurantPageAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;

    public RestaurantPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                RestaurantInfoFragment tab1 = new RestaurantInfoFragment();
                return tab1;
            case 1:
                RestaurantMenuDisplayFragment tab2 = new RestaurantMenuDisplayFragment();
                return tab2;
            case 2:
                RestaurantReviewFragment tab3 = new RestaurantReviewFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
