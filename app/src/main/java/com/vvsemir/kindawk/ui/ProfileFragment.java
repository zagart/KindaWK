package com.vvsemir.kindawk.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vvsemir.kindawk.service.ProviderIntentService;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.http.HttpResponse;

public class ProfileFragment extends ReceiverFragment {

    TextView userNameView;
    private String userName;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String response, Parcelable data, Boolean preserveProviderData) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(initBundle(response, data, preserveProviderData));
        return fragment;
    }

    @Override
    public void onPostCreate() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        userNameView = (TextView) view.findViewById(R.id.userName);

        updateViews(getArguments().getParcelable(super.ARG_PARAM_PROVIDER_RESPONSE));

        return view;
    }


    @Override
    public void updateViews(Parcelable data) {
        if(data == null) {
            return;
        }

        try{
            userNameView.setText( ((HttpResponse)data).GetResponseAsJSON().getJSONObject("response").getString("first_name"));

            //userNameView.setText(getArguments().getString(""));

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void loadData() {
        ProviderIntentService.getAccountProfileInfo(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}


