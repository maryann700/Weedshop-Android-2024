package com.weedshop.driver.Adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.weedshop.driver.R;

/**
 * Created by MTPC-83 on 5/31/2017.
 */

public class WizardPagerAdapter extends PagerAdapter {

    public Object instantiateItem(ViewGroup collection, int position) {

        int resId = 0;
        switch (position) {
            case 0:
                resId = R.id.lnrshop;
                break;
            case 1:
                resId = R.id.lnrdelivery;
                break;
        }

        return collection.findViewById(resId);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }
}
