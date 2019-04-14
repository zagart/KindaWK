package com.vvsemir.kindawk.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.UserActivity;
import com.vvsemir.kindawk.provider.UserProfile;
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
        firstNameView = (TextView) view.findViewById(R.id.userName);
        loadData();

        return view;
    }

    public void updateViewsWithData(Parcelable data) {
        if(data == null) {
            return;
        }

        try{
            firstNameView.setText(( (UserProfile)data).getFirstName());
            profilePhotoView.setImageBitmap(
                ImageLoader.getInstance().
                        getBitmapFromBytes( ((UserProfile)data).getProfilePhotoBytes().getAsByteArray(UserProfile.PHOTO_BYTES) ));
        } catch (Exception ex) {
            ex.printStackTrace();
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
                //to do
                Log.d("getAccountProfileInfo", "getAccountProfileInfo : loading exception!!!" + throwable.getMessage() );
            }

            @Override
            public void onServiceNotStarted() {
                ((UserActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_profile);
            }
        } );
    }
}


