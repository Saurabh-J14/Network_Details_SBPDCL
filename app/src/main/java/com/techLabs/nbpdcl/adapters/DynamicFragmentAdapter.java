package com.techLabs.nbpdcl.adapters;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class DynamicFragmentAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public DynamicFragmentAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeFragment(int position) {
        fragmentList.remove(position);
        fragmentTitleList.remove(position);
        notifyDataSetChanged();
    }

    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    public void clearFragments() {
        fragmentList.clear();
        fragmentTitleList.clear();
    }

    public List getTitleList() {
        return fragmentTitleList;
    }

    public Fragment getFragmentAt(int position) {
        if (position >= 0 && position < fragmentList.size()) {
            return fragmentList.get(position);
        }
        return null;
    }


}
