package com.vvsemir.kindawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vvsemir.kindawk.Models.VkApiIntentService;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.http.HttpResponse;

public class ProfileFragment extends ReceiverFragment {

    TextView userNameView;
    private String userName;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String response, Parcelable data) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(initBundle(response, data));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        userNameView = (TextView) view.findViewById(R.id.userName);

        updateViews(getArguments().getParcelable(super.ARG_PARAM_INTENT_RESPONSE));

        return view;
    }


    @Override
    public void updateViews(Parcelable data) {
        try{
            userNameView.setText( ((HttpResponse)data).GetResponseAsJSON().getJSONObject("response").getString("first_name"));

            //userNameView.setText(getArguments().getString(""));

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void startLoadDataService() {
        VkApiIntentService.getAccountProfileInfo(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}


