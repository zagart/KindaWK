package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;

public class FriendsFragment extends ReceiverFragment {

    public FriendsFragment() {
    }

    public static FriendsFragment newInstance(String response, Parcelable data, Boolean preserveProviderData) {
        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(initBundle(response, data, preserveProviderData));
        return fragment;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPostCreate() {

    }

    @Override
    public void updateViews(Parcelable data) {

    }

    @Override
    public void loadData() {

    }
}
