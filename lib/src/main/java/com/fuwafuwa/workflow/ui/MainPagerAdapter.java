package com.fuwafuwa.workflow.ui;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.security.InvalidParameterException;


/**
 * Created by fred on 2016/11/1.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private String[] titles;
    private Fragment[] fragments;

    public MainPagerAdapter(FragmentManager fm, String[] titles, Fragment... fragments) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        updateData(titles, fragments);
    }

    public void clear() {
        if (fragments != null) {
            titles = null;
            fragments = null;
        }
    }

    private void updateData(String[] titles, Fragment[] fragments) {
        if (fragments == null || fragments.length == 0) return;
        if (titles == null) {
            this.titles = new String[fragments.length];
        } else {
            this.titles = titles;
        }
        if (fragments.length != this.titles.length) throw new InvalidParameterException();
        this.fragments = fragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments == null || fragments.length == 0) return null;
        return fragments[position];
    }

    @Override
    public int getCount() {
        return titles != null ? titles.length : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}