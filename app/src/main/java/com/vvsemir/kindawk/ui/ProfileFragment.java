package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvsemir.kindaimageloader.ILoaderCallback;
import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.UserActivity;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.provider.Friend;
import com.vvsemir.kindawk.provider.Photo;
import com.vvsemir.kindawk.provider.PhotosProvider;
import com.vvsemir.kindawk.provider.UserProfile;
import com.vvsemir.kindawk.provider.UserProfileProvider;
import com.vvsemir.kindawk.service.CallbackExceptionFactory;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends KindaFragment  {
    public static final String FRAGMENT_TAG = "ProfileFragmentTag";
    private static final String CURRENT_USER = "CurrentUser";


    ImageView profilePhotoView;
    TextView firstNameView;
    TextView lastNameView;
    TextView countryView;
    TextView cityView;
    TextView statusView;
    TextView phoneView;
    RecyclerView recyclerView;
    View progressView;
    int currentUserId = 0;
    Friend friend = null;

    private LinearLayoutManager layoutManager;
    PhotosRecyclerAdapter photosRecyclerAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_USER)) {
            Parcelable data = savedInstanceState.getParcelable(CURRENT_USER);
             if(data instanceof Friend) {
                 friend = (Friend)data;
                 currentUserId = friend.getUserId();
             }
        }

        if(currentUserId == 0) {
            currentUserId = AuthManager.getCurrentToken().getUserId();
            friend = null;
        }

        photosRecyclerAdapter = new PhotosRecyclerAdapter(getActivity());
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
        cityView = view.findViewById(R.id.profCityView);
        countryView = view.findViewById(R.id.profCountryView);
        statusView  = view.findViewById(R.id.profStatusView);
        phoneView  = view.findViewById(R.id.profPhoneView);
        recyclerView = view.findViewById(R.id.profPhotoList);
        progressView = view.findViewById(R.id.profPhotoProgress);

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        } else {
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photosRecyclerAdapter);

        int[] resIds = {R.id.selectPhotoBox};
        recyclerView.addOnItemTouchListener(new PhotoRecyclerViewItemTouchListener(recyclerView,
                resIds, new PhotoRecyclerViewItemTouchListener.SelectionClickListener() {
            @Override
            public void onSelectClick(@NotNull View view, int position) {
                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.selectPhotoBox);
                checkBox.setChecked(!checkBox.isChecked());
            }

            @Override
            public void onClick(@NotNull View view, int position) {
                ((UserActivity)getActivity()).loadPhotoFragment(photosRecyclerAdapter.getItemUriScreen(position));
            }

            @Override
            public void onLongClick(@NotNull View view, int position) {

            }
        }));


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
            checkSetText(cityView, profile.getCity().getTitle());
            checkSetText(countryView, profile.getCountry().getTitle());
            checkSetText(statusView, profile.getStatus());
            checkSetText(phoneView, profile.getPhone());

            if(((UserProfile)data).getProfilePhotoBytes() != null) {
                byte[] imageBytes = ((UserProfile) data).getProfilePhotoBytes().getAsByteArray(UserProfile.PHOTO_BYTES);

                if (imageBytes != null && imageBytes.length > 0) {
                    profilePhotoView.setImageBitmap(ImageLoader.getBitmapFromBytes(imageBytes));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateViewsWithFriendData(Friend friend) {
        checkSetText(firstNameView, friend.getFirstName());
        checkSetText(lastNameView, friend.getLastName());
        checkSetText(cityView, friend.getCity().getTitle());
        checkSetText(countryView, friend.getCountry().getTitle());
        checkSetText(statusView, friend.getStatus());

        if(friend.getPhotoBytes() != null) {
            byte[] imageBytes = friend.getPhotoBytes().getAsByteArray(Friend.PHOTO_BYTES);

            if (imageBytes != null && imageBytes.length > 0) {
                profilePhotoView.setImageBitmap(ImageLoader.getBitmapFromBytes(imageBytes));
            }
        }
    }

    private void checkSetText(TextView view, String txt) {
        if(txt != null && txt.isEmpty() == false){
            view.setText(txt);
        }
    }

    @Override
    public void loadData() {
        if(currentUserId == AuthManager.getCurrentToken().getUserId()) {
            RequestParams params = null;
            ProviderService.getAccountProfileInfo(params, new ICallback<UserProfile>() {
                @Override
                public void onResult(UserProfile result) {
                    updateViewsWithData(result);
                }

                @Override
                public void onError(Throwable throwable) {
                    if (throwable instanceof CallbackExceptionFactory.Companion.ServiceException) {
                        ((UserActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_profile);

                        return;
                    }

                    Log.d("getAccountProfileInfo", "getAccountProfileInfo : loading exception!!!" + throwable.getMessage());
                }
            });

            showProgress();
        } else if(currentUserId != 0 && friend != null) {
            updateViewsWithFriendData(friend);
        }

        RequestParams photoParams = new RequestParams();
        photoParams.put(PhotosProvider.PARAM_REQUEST_OWNERID, currentUserId);

        ProviderService.getPhotosByOwner(photoParams, new ILoaderCallback<List<Photo>>() {

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_photo) {

            return true;
        } else if(id == R.id.action_refresh) {
            ProviderService.cleanProfileData(new ICallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    loadData();
                }

                @Override
                public void onError(Throwable throwable) {

                }
            });
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

    public void setFriendProfile(Friend friend) {
        currentUserId = friend.getUserId();
        this.friend = friend;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURRENT_USER, friend);
    }

}


