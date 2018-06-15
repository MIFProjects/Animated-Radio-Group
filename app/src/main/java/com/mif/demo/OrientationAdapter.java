package com.mif.demo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OrientationAdapter extends FragmentPagerAdapter {
    private Map<Integer, String> mFragmentTags = new HashMap<>();

    private Context context;
    private FragmentManager mFragmentManager;
    private ArrayList<String> stringArrayList = new ArrayList<>();

    public OrientationAdapter(FragmentManager fm, Context context) {
        super(fm);
        mFragmentManager = fm;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return Fragment.instantiate(context, stringArrayList.get(position), null);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return stringArrayList.size();
    }

    public void addItem(String fragmentName) {
        stringArrayList.add(fragmentName);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }
}
