package com.mayanksharma.instagramclone.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFraagmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumbers = new HashMap<>();
    private final HashMap<Integer, String> mFragmentNames = new HashMap<>();

    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFraagmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFraagmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmentName)
    {
        mFraagmentList.add(fragment);
        mFragments.put(fragment, mFraagmentList.size()-1);
        mFragmentNumbers.put(fragmentName, mFraagmentList.size()-1);
        mFragmentNames.put(mFraagmentList.size()-1, fragmentName);
    }

    /**return the fragment with the name @param
     *
     * @param fragmentName
     * @return
     */
    public Integer getFragmentNumber(String fragmentName) {
        if (mFragmentNumbers.containsKey(fragmentName))
        {
            return mFragmentNumbers.get(fragmentName);
        }else
        {
            return null;
        }
    }

    /**return the fragment with the name @param
     *
     * @param fragment
     * @return
     */
    public Integer getFragmentNumber(Fragment fragment) {
        if (mFragmentNumbers.containsKey(fragment))
        {
            return mFragmentNumbers.get(fragment);
        }else
        {
            return null;
        }
    }

    /**return the fragment with the name @param
     *
     * @param fragmentNumber
     * @return
     */
    public String getFragmentName(Integer fragmentNumber) {
        if (mFragmentNames.containsKey(fragmentNumber))
        {
            return mFragmentNames.get(fragmentNumber);
        }else
        {
            return null;
        }
    }
}
