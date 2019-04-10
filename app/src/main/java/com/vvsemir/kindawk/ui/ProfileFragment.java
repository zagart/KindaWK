package com.vvsemir.kindawk.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.provider.UserProfile;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.service.ProviderService;

public class ProfileFragment extends ReceiverFragment {

    ImageView profilePhotoView;
    TextView userNameView;
    private String userName;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(Parcelable data, Boolean preserveProviderData) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(initBundle(data, preserveProviderData));
        return fragment;
    }

    @Override
    public void onPostCreate() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        userNameView = (TextView) view.findViewById(R.id.userName);
        profilePhotoView = (ImageView) view.findViewById(R.id.profilePhotoView);

        Bundle bundle = getArguments();

        if( bundle.containsKey(super.ARG_PARAM_PROVIDER_DATA) ) {
            updateViews(bundle.getParcelable(super.ARG_PARAM_PROVIDER_DATA));
        }

        return view;
    }


    @Override
    public void updateViews(Parcelable data) {
        if(data == null) {
            return;
        }

        try{
            userNameView.setText(( (UserProfile)data).getFirstName());
            profilePhotoView.setImageBitmap(
                    ImageLoader.getInstance().getBitmapFromFile(( (UserProfile)data).getProfilePhoto()));
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void loadData() {
        ProviderService.getAccountProfileInfo(new ICallback<UserProfile>() {
            @Override
            public void onResult(UserProfile result) {
                updateViews(result);
            }

            @Override
            public void onError(Throwable throwable) {
                //to do
                Log.d("getAccountProfileInfo", "getAccountProfileInfo : loading exception!!!" + throwable.getMessage() );
            }
        } );
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}


