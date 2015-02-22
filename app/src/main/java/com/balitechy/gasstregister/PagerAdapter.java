package com.balitechy.gasstregister;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int index) {
        switch(index){
            case 0:
                return new FragmentRegisterLocation();
            case 1:
                return new FragmentUnsyncedLocation();
            case 2:
                return new FragmentSyncedLocation();
        }
        return null;
    }



    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return context.getResources().getString(R.string.collect_text);
            case 1:
                return context.getResources().getString(R.string.unsynced_text);
            case 2:
                return context.getResources().getString(R.string.synced_text);
        }
        return super.getPageTitle(position);
    }
}