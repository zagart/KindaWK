package com.vvsemir.kindawk.ui;

import android.app.Application;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vvsemir.kindawk.Models.VkApiIntentService;
import com.vvsemir.kindawk.R;

public class TopPagerAdapter extends FragmentPagerAdapter {
    private static final String PROFILE="Profile";
    private static final String NEWS = "News";
    private static final String FRIENDS = "Friends";

    public TopPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return PROFILE;
            case 1:
                return NEWS;
            case 2:
                return FRIENDS;
            default:
                return super.getPageTitle(position);
        }
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                //Bundle args = new Bundle();
                //args.putInt(DemoObjectFragment.ARG_OBJECT, i );
                //fragment.setArguments(args);
                return ProfileFragment.newInstance(VkApiIntentService.ACTION_ACCOUNT_GET_PROFILE_INFO_RESPONSE, null);
            case 1:
                return new Fragment();
            case 2:
                return new Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
