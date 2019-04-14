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
    static final int ARG_PARAM_REQUEST_MAX_FRIENDS = 50;
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
                Friend friend = friendsList.getItem(i);
                String photoUrl = friendsList.getItem(i).getPhoto100Url();

                if(!photoUrl.isEmpty()){
                    byte[] imageBytes = ImageLoader.getInstance().getBytesFromFile(new URL(photoUrl));
                    if(imageBytes != null && imageBytes.length > 0 ){
                        ContentValues contentPhotoBytes = new ContentValues();
                        contentPhotoBytes.put(Friend.PHOTO_BYTES, imageBytes);
                        friend.setPhoto100Bytes(contentPhotoBytes);
                    }
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void putDataInDb(){
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        dbManager.removeAllFriends();
        dbManager.insertFriends(friendsList);
        Log.d("ZZZgetDataFromDb", "  putDataInDb success");
    }

    private boolean getDataFromDb(){
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        friendsList.removeAllFriends();

        Cursor cursor = dbManager.getFriends();
        while (cursor.moveToNext()) {
            Friend friend = new Friend();
            friend.setUid(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            friend.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
            friend.setLastName(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
            friend.setBirthDate(cursor.getString(cursor.getColumnIndexOrThrow("bdate")));
            friend.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
            DataIdTitle country = new DataIdTitle();
            country.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("country")));
            friend.setCountry(country);

            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("photobytes"));
            ContentValues contentPhotoBytes = new ContentValues();
            contentPhotoBytes.put(friend.PHOTO_BYTES, imageBytes);
            friend.setPhoto100Bytes(contentPhotoBytes);
            Log.d("ZZZgetDataFromDb", "  getDataFromDb success");
            friendsList.addFriend(friend);
        }

        //return friendsList.getCount() > 0;
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
