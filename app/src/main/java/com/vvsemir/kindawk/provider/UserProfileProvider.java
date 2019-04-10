package com.vvsemir.kindawk.provider;

import android.content.Context;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

import java.net.URL;

public class UserProfileProvider extends BaseProvider<UserProfile> {
    static final String ARG_PARAM_REQUEST_METHOD = "account.getProfileInfo";
    static final String ARG_PARAM_REQUEST_PHOTO_METHOD = "getProfiles";

    private Context context;
    private UserProfile profileData = new UserProfile();

    public UserProfileProvider(ICallback<UserProfile> callback) {
        super(callback);
    }

    void loadData() {
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

        ProviderService.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onResult(profileData);
            }
        });

    }

    @Override
    public void resetData() {
        //profileData.clean();
    }

    @Override
    public void run() {
        loadData();
    }
}
