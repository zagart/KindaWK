package com.vvsemir.kindawk.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.db.DbManager;
import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.service.CallbackExceptionFactory;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

import java.net.URL;

public class UserProfileProvider extends BaseProvider<UserProfile> {
    static final String EXCEPTION_LOADING_USERPROFILE_API = "Sorry, can not read profile from API";
    static final String ARG_PARAM_REQUEST_METHOD = "account.getProfileInfo";
    static final String ARG_PARAM_REQUEST_PHOTO_METHOD = "getProfiles";
    static final String PARAM_REQUEST_PROFILE_PHOTO_SIZE = "photo_big";

    public static final String PARAM_REQUEST_USERID = "userId";

    private UserProfile userProfile = null;

    public UserProfileProvider(ICallback<UserProfile> callback) {
        super(callback);
    }

    void loadData() {
        try {
            DbManager.DbResponse dbResponse = getDataFromDb();
            boolean saveToDb = false;

            if (dbResponse != DbManager.DbResponse.DB_RESPONSE_STATUS_SUCCESS) {
                loadApiData();

                if (userProfile != null) {
                    saveToDb = true;
                } else {
                    throw new CallbackExceptionFactory.Companion.HttpException(EXCEPTION_LOADING_USERPROFILE_API);
                }

            }

            ProviderService.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onResult(userProfile);
                }
            });

            if(saveToDb) {
                putDataInDb();
            }

        } catch (Exception ex){
            ex.printStackTrace();
            final Throwable throwable;

            if(ex instanceof CallbackExceptionFactory.Companion.NetworkException) {
                throwable = ex;
            } else {
                throwable = CallbackExceptionFactory.Companion.createException
                        (CallbackExceptionFactory.THROWABLE_TYPE_ERROR, EXCEPTION_LOADING_USERPROFILE_API);
            }

            ProviderService.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(throwable);
                }
            });
        }
    }

    private void loadApiData() throws Exception {
        try {
            HttpResponse httpResponse = new HttpRequestTask().execute(
                    new HttpRequest(ARG_PARAM_REQUEST_METHOD, false, requestParams), null);

            if (httpResponse == null) {
                throw new CallbackExceptionFactory.Companion.HttpException(EXCEPTION_LOADING_USERPROFILE_API);
            }

            userProfile = getProfileFromHttp(httpResponse);

            if(userProfile == null){
                return;
            }

            Integer userId = getUserId();
            userProfile.setUserId(userId);
            RequestParams requestPhotoParams = new RequestParams();
            requestPhotoParams.put("uids", userId.toString());
            requestPhotoParams.put("fields", PARAM_REQUEST_PROFILE_PHOTO_SIZE);

            HttpResponse httpPhotoResponse = new HttpRequestTask().execute(
                    new HttpRequest(ARG_PARAM_REQUEST_PHOTO_METHOD, false, requestPhotoParams), null);

            if (httpPhotoResponse != null) {
                String photoUrl = getPhotoURLFromHttp(httpPhotoResponse);

                if (photoUrl != null) {
                    userProfile.setProfilePhoto(photoUrl);
                    byte[] imageBytes = ImageLoader.getBytesFromNetworkFile(new URL(photoUrl));

                    if(imageBytes != null && imageBytes.length > 0 ){
                        ContentValues contentPhotoBytes = new ContentValues();
                        contentPhotoBytes.put(UserProfile.PHOTO_BYTES, imageBytes);
                        userProfile.setProfilePhotoBytes(contentPhotoBytes);
                    }
                }
            }
        } catch (CallbackExceptionFactory.Companion.NetworkException ex){
            throw ex;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new CallbackExceptionFactory.Companion.HttpException(EXCEPTION_LOADING_USERPROFILE_API);
        }
    }

    private void putDataInDb(){
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        dbManager.removeAllUserProfile();
        dbManager.insertUserProfile(userProfile);
    }

    private DbManager.DbResponse getDataFromDb(){
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        int userId = getUserId();
        userProfile = dbManager.getUserProfile(userId);

        if(userProfile == null){
            return DbManager.DbResponse.DB_RESPONSE_STATUS_ERROR;
        }

        return DbManager.DbResponse.DB_RESPONSE_STATUS_SUCCESS;
    }

    private int getUserId() {
        int userId = 0;
        if ( requestParams != null && requestParams.contains(PARAM_REQUEST_USERID) ) {
            userId = Integer.parseInt(requestParams.getParam(PARAM_REQUEST_USERID));
        }

        if(userId == 0){
            userId = AuthManager.getCurrentToken().getUserId();
        }

        return userId;
    }

    private UserProfile getProfileFromHttp(final HttpResponse httpResponse)  throws  Exception {
        try {
            Gson gson = new Gson().newBuilder().create();;
            JsonObject root = gson.fromJson(((HttpResponse)httpResponse).getResponseAsString(), JsonObject.class);
            JsonObject response = root.getAsJsonObject("response");
            userProfile = gson.fromJson(response, UserProfile.class);

            return userProfile;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CallbackExceptionFactory.Companion.HttpException(EXCEPTION_LOADING_USERPROFILE_API);
        }
    }

    private String getPhotoURLFromHttp(final HttpResponse httpResponse){
        try {
            Gson gson = new Gson().newBuilder().create();;
            JsonObject httpObj = gson.fromJson(((HttpResponse)httpResponse).getResponseAsString(), JsonObject.class);
            JsonArray response = httpObj.getAsJsonArray("response");
            String url =  (response).get(0).getAsJsonObject().get(PARAM_REQUEST_PROFILE_PHOTO_SIZE).getAsString();

            return url;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public void run() {
        loadData();
    }


}
