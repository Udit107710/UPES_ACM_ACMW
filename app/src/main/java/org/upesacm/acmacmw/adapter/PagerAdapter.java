package org.upesacm.acmacmw.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.upesacm.acmacmw.fragment.homepage.AcmFragment;
import org.upesacm.acmacmw.fragment.homepage.AcmWFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AcmFragment();
            case 1:

                return new AcmWFragment();
            default:
                return new Fragment();
        }
    }


    @Override
    public int getCount() {
        return 2;
    }
}
