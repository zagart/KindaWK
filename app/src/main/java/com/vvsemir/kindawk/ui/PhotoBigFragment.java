package com.vvsemir.kindawk.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.UserActivity;
import com.vvsemir.kindawk.provider.Friend;
import com.vvsemir.kindawk.provider.FriendsList;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class PhotoBigFragment extends KindaFragment {
    public static final String FRAGMENT_TAG = "photoBigFragment";
    private static final String CURRENT_URI = "currentUri";

    private ImageView imageView;
    private String imageUri;

    public PhotoBigFragment() {
    }

    public static PhotoBigFragment newInstance(String uri) {
        PhotoBigFragment fragment = new PhotoBigFragment();
        fragment.imageUri = uri;

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_URI)) {
            imageUri = savedInstanceState.getString(CURRENT_URI);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_photo, container, false);
        imageView = view.findViewById(R.id.photoBigView);
        loadData();

        return view;
    }

    @Override
    public void loadData() {
        new ImageLoader(getContext()).loadAndShow(imageView, imageUri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            ((UserActivity)getActivity()).onBackPressed();
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ((UserActivity)getActivity()).updateMenuForFragment();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_URI, imageUri);
    }
}
