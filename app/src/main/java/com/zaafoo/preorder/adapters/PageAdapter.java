package com.zaafoo.preorder.adapters;

/**
 * Created by SUB on 3/28/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zaafoo.preorder.fragments.AccountFragment;
import com.zaafoo.preorder.fragments.OfferFragment;
import com.zaafoo.preorder.fragments.RestaurantFragment;

public class PageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                RestaurantFragment tab1 = new RestaurantFragment();
                return tab1;
            case 1:
                OfferFragment tab2 = new OfferFragment();
                return tab2;
            case 2:
                AccountFragment tab3 = new AccountFragment();
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