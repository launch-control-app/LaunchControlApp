package com.example.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {

    private HomeFragment homeFragment;

    public TabAdapter(FragmentManager fm) {
        super(fm);
        homeFragment = new HomeFragment();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch (position) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                return homeFragment;
            case 1:
                return new SpeedFragment();
            case 2:
                return new RPMFragment();
            case 3:
                return new TemperatureFragment();
            case 4:
                return new PressureFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 5 total pages.
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "HOME";
            case 1:
                return "SPEED";
            case 2:
                return "RPM";
            case 3:
                return "TEMPERATURE";
            case 4:
                return "PRESSURE";
        }
        return null;
    }
}
