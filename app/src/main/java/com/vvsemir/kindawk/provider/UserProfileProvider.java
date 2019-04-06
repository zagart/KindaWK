package com.vvsemir.kindawk.provider;

import android.content.Context;

import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.service.RequestParams;
import com.vvsemir.kindawk.utils.ICallback;

public class UserProfileProvider implements IProvider <RequestParams, UserProfile> {
    static final String ARG_PARAM_REQUEST_METHOD = "account.getProfileInfo";

    private Context context;
    private UserProfile profileData = new UserProfile();
    private RequestParams requestParams;

    public UserProfileProvider() {
        requestParams = null;
    }


    @Override
    public UserProfile loadData(RequestParams request) {
        HttpResponse httpResponse = new HttpRequestTask().execute(
                new HttpRequest(ARG_PARAM_REQUEST_METHOD, false, requestParams), null);
        if(httpResponse != null){
            profileData.setFromHttp(httpResponse);
        }

        return profileData;
    }

    @Override
    public void resetData() {
        //profileData.clean();
    }
}
