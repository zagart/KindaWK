package com.vvsemir.kindawk.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.db.DbManager;
import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

import java.net.URL;

public class FriendsProvider extends BaseProvider<FriendsList> {
    static final String ARG_PARAM_REQUEST_METHOD = "friends.get";
    static final int ARG_PARAM_REQUEST_MAX_FRIENDS = 100;
    static final String ARG_PARAM_REQUEST_ORDER  = "name";
    static final String ARG_PARAM_REQUEST_FIELDS = "bdate,city,country,status,photo_100";


    private FriendsList friendsList = new FriendsList();

    public FriendsProvider(ICallback<FriendsList> callback) {
        super(callback);
    }

    void loadData() {
        if(!getDataFromDb()) {
            loadApiData();
            putDataInDb();
        }

        ProviderService.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onResult(friendsList);
            }
        });
    }

    private void loadApiData(){
        try {
            if(requestParams == null){
                requestParams = new RequestParams();
            }

            Integer userId = AuthManager.getCurrentToken().getUserId();
            requestParams.put("user_id", userId.toString());
            requestParams.put("count", ARG_PARAM_REQUEST_MAX_FRIENDS);
            requestParams.put("order", ARG_PARAM_REQUEST_ORDER);
            requestParams.put("fields", ARG_PARAM_REQUEST_FIELDS);

            HttpResponse httpResponse = new HttpRequestTask().execute(
                    new HttpRequest(ARG_PARAM_REQUEST_METHOD, false, requestParams), null);

            if (httpResponse != null) {
                friendsList.setFromHttp(httpResponse);
            }
             for(int i = 0; i < friendsList.getCount(); i++) {
                 Uri photoUri = friendsList.getItem(i).getPhoto100Url();
             }




            if (httpPhotoResponse != null) {
                profileData.setUserId(userId);
                profileData.setPhotoURLFromHttp(httpPhotoResponse);
                Uri photoUri = profileData.getProfilePhoto();

                if (photoUri != null) {
                    byte[] imageBytes = ImageLoader.getInstance().getBytesFromFile(new URL(photoUri.toString()));

                    if(imageBytes != null && imageBytes.length > 0 ){
                        ContentValues contentPhotoBytes = new ContentValues();
                        contentPhotoBytes.put(UserProfile.PHOTO_BYTES, imageBytes);
                        profileData.setProfilePhotoBytes(contentPhotoBytes);
                    }
                }
            }
            Log.d("GGGgetDataFromDb", " loadApiData  success");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void putDataInDb(){
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        dbManager.removeAllUserProfile();
        dbManager.insertUserProfile(profileData);
        Log.d("GGGgetDataFromDb", "  putDataInDb success");
    }

    private boolean getDataFromDb(){
        DbManager dbManager = ProviderService.getInstance().getDbManager();

        Cursor cursor = dbManager.getUserProfile(AuthManager.getCurrentToken().getUserId());
        while (cursor.moveToNext()) {
            profileData.setUserId(cursor.getInt(0));
            profileData.setFirstName(cursor.getString(1));
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("profilePhotoBytes"));
            ContentValues contentPhotoBytes = new ContentValues();
            contentPhotoBytes.put(UserProfile.PHOTO_BYTES, imageBytes);
            profileData.setProfilePhotoBytes(contentPhotoBytes);
            Log.d("GGGgetDataFromDb", "  getDataFromDb success");
            return true;
        }

        return false;
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
