package com.vvsemir.kindawk.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.UserActivity;
import com.vvsemir.kindawk.provider.UserProfile;
import com.vvsemir.kindawk.service.CallbackExceptionFactory;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.service.ProviderService;

public class ProfileFragment extends Fragment {
    ImageView profilePhotoView;
    TextView firstNameView;
    TextView lastNameView;
    TextView countryView;
    TextView birthDateView;
    TextView statusView;
    TextView homeTownView;
    TextView phoneView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_photo) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
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
        profilePhotoView = (ImageView) view.findViewById(R.id.profilePhotoView);
        firstNameView = (TextView) view.findViewById(R.id.profFirstNameView);
        lastNameView = (TextView) view.findViewById(R.id.profLastNameView);
        countryView = (TextView) view.findViewById(R.id.profCountryView);
        statusView  = (TextView) view.findViewById(R.id.profStatusView);;
        phoneView  = (TextView) view.findViewById(R.id.profPhoneView);;

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
    }
}


