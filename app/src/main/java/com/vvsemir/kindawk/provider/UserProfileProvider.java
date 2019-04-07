package com.vvsemir.kindawk.provider;

import android.content.Context;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.service.RequestParams;
import com.vvsemir.kindawk.utils.ICallback;

import java.net.URL;

public class UserProfileProvider implements IProvider <RequestParams, UserProfile> {
    static final String ARG_PARAM_REQUEST_METHOD = "account.getProfileInfo";
    static final String ARG_PARAM_REQUEST_PHOTO_METHOD = "getProfiles";

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

        RequestParams requestPhotoParams = new RequestParams();
        requestPhotoParams.put("uids", AuthManager.getCurrentToken().getUserId().toString());
        requestPhotoParams.put("fields", "photo_medium");
        HttpResponse httpPhotoResponse = new HttpRequestTask().execute(
                new HttpRequest(ARG_PARAM_REQUEST_PHOTO_METHOD, false, requestPhotoParams), null);

        if(httpPhotoResponse != null){
            URL url = profileData.getPhotoURLFromHttp(httpPhotoResponse);

            if(url != null) {
                profileData.setPhotoUri(ImageLoader.getInstance().createTempPhotoFile(url));
            }
        }


        return profileData;
    }

    @Override
    public void resetData() {
        //profileData.clean();
    }
}
