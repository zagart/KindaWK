package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvsemir.kindaimageloader.ILoaderCallback;
import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.UserActivity;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.provider.Photo;
import com.vvsemir.kindawk.provider.UserProfile;
import com.vvsemir.kindawk.provider.UserProfileProvider;
import com.vvsemir.kindawk.service.CallbackExceptionFactory;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

import java.util.List;

public class ProfileFragment extends KindaFragment  {
    public static final String FRAGMENT_TAG = "ProfileFragmentTag";

    ImageView profilePhotoView;
    TextView firstNameView;
    TextView lastNameView;
    TextView countryView;
    TextView birthDateView;
    TextView statusView;
    TextView phoneView;
    RecyclerView recyclerView;
    View progressView;
    int currentUserId;

    private LinearLayoutManager layoutManager;
    PhotosRecyclerAdapter photosRecyclerAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        currentUserId = AuthManager.getCurrentToken().getUserId();
        photosRecyclerAdapter = new PhotosRecyclerAdapter(getActivity());
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.user_top_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        profilePhotoView = view.findViewById(R.id.profilePhotoView);
        firstNameView = view.findViewById(R.id.profFirstNameView);
        lastNameView = view.findViewById(R.id.profLastNameView);
        countryView = view.findViewById(R.id.profCountryView);
        statusView  = view.findViewById(R.id.profStatusView);
        phoneView  = view.findViewById(R.id.profPhoneView);
        recyclerView = view.findViewById(R.id.profPhotoList);
        progressView = view.findViewById(R.id.profPhotoProgress);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photosRecyclerAdapter);

        //if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        //    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //} else {
        //    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
       // }


        loadData();

        return view;
    }

    public void updateViewsWithData(Parcelable data) {
        if(data == null) {
            return;
        }

        try{
            UserProfile  profile = (UserProfile)data;
            checkSetText(firstNameView, profile.getFirstName());
            checkSetText(lastNameView, profile.getLastName());
            checkSetText(countryView, profile.getCountry().getTitle());
            checkSetText(statusView, profile.getStatus());
            checkSetText(phoneView, profile.getPhone());

            if(((UserProfile)data).getProfilePhotoBytes() != null) {
                byte[] imageBytes = ((UserProfile) data).getProfilePhotoBytes().getAsByteArray(UserProfile.PHOTO_BYTES);

                if (imageBytes != null && imageBytes.length > 0) {
                    profilePhotoView.setImageBitmap(ImageLoader.getInstance().getBitmapFromBytes(imageBytes));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkSetText(TextView view, String txt) {
        if(txt != null && txt.isEmpty() == false){
            view.setText(txt);
        }
    }

    @Override
    public void loadData() {
        ProviderService.getAccountProfileInfo(new ICallback<UserProfile>() {
            @Override
            public void onResult(UserProfile result) {
                updateViewsWithData(result);
            }

            @Override
            public void onNotify(UserProfile result) {
                //to do
                Log.d("getAccountProfileInfo", "getAccountProfileInfo : notification refresh!!!");
            }

            @Override
            public void onError(Throwable throwable) {
                if(throwable instanceof CallbackExceptionFactory.Companion.ServiceException){
                    ((UserActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_profile);

                    return;
                }

                Log.d("getAccountProfileInfo", "getAccountProfileInfo : loading exception!!!" + throwable.getMessage() );
            }
        } );

        showProgress();
        RequestParams params = new RequestParams();
        params.put(UserProfileProvider.PARAM_REQUEST_USERID, currentUserId);

        ProviderService.getPhotosByOwner(params, new ILoaderCallback<List<Photo>>() {

            @Override
            public void onResult(final List<Photo> result) {
                photosRecyclerAdapter.updateItems(result);
                hideProgress();
            }

            @Override
            public void onError(Throwable throwable) {
                hideProgress();
            }
        } );
    }

/*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        final FragmentManager fm = getActivity().getSupportFragmentManager();
        ProfileFragment ef = (ProfileFragment) fm.findFragmentByTag(ProfileFragment.FRAGMENT_TAG);
        if( ef != null ) {  // for small screens the fragment is not embedded in this activity
            final FragmentTransaction ft = fm.beginTransaction();
            ft.remove(ef);
            ft.commit();
            ef = null;
            fm.executePendingTransactions();
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View newView = inflater.inflate(R.layout.fragment_profile, null);
            ViewGroup rootView = (ViewGroup) getView();
            rootView.removeAllViews();
            rootView.addView(newView);
            //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View newView = inflater.inflate(R.layout.fragment_profile, null);
            ViewGroup rootView = (ViewGroup) getView();
            rootView.removeAllViews();
            rootView.addView(newView);
            //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_photo) {

            return true;
        } else if(id == R.id.action_refresh) {
            ProviderService.reloadProfileData();
            loadData();
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideProgress() {
        if (progressView.getVisibility() != View.GONE) {
            progressView.setVisibility(View.GONE);
        }
    }

    private void showProgress() {
        if (progressView.getVisibility() != View.VISIBLE) {
            progressView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public String getFragmentTag() {
        return FRAGMENT_TAG;
    }
}


