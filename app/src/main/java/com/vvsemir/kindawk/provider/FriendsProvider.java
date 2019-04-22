package com.vvsemir.kindawk.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
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
import java.util.ArrayList;
import java.util.List;

public class FriendsProvider extends BaseProvider<FriendsList> {
    public static final String EXCEPTION_LOADING_FRIENDS_API = "Sorry, can not read friends from API";
    static final String ARG_PARAM_REQUEST_METHOD = "friends.get";
    static final int ARG_PARAM_REQUEST_MAX_FRIENDS = 50;
    static final String ARG_PARAM_REQUEST_ORDER  = "name";
    static final String ARG_PARAM_REQUEST_FIELDS = "bdate,city,country,status,photo_100";


    private FriendsList friendsList = new FriendsList();

    public FriendsProvider(ICallback<FriendsList> callback) {
        super(callback);
    }

    void loadData() {
        try {
            DbManager.DbResponse dbResponse = getDataFromDb();
            Log.d("FF getDatFromDb", "  response = " + dbResponse);

            if (dbResponse == DbManager.DbResponse.DB_RESPONSE_STATUS_ERROR ||
                    dbResponse == DbManager.DbResponse.DB_RESPONSE_STATUS_EMPTY_TABLE) {
                friendsList.removeAllFriends();

                List<Friend> friends = loadApiData();

                if (friends != null && friends.size() > 0) {

                    putDataInDb(friends);

                    friendsList.append(friends);
                }
            }

            ProviderService.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onResult(friendsList);
                }
            });

        } catch (Exception ex){
            ex.printStackTrace();
            ProviderService.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(CallbackExceptionFactory.Companion.createException
                            (CallbackExceptionFactory.THROWABLE_TYPE_ERROR, EXCEPTION_LOADING_FRIENDS_API));
                }
            });
        }
    }

    private List<Friend> loadApiData(){
        List<Friend> friends = null;

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

            if (httpResponse == null) {
                throw new CallbackExceptionFactory.Companion.HttpException(EXCEPTION_LOADING_FRIENDS_API);
            }

            friends = getFriendsFromHttp(httpResponse);

            if(friends != null && friends.size() > 0) {
                loadApiImages(friends);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            return friends;
        }
    }

    private void putDataInDb(List<Friend> friends) {
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        dbManager.removeAllFriends();
        dbManager.insertFriends(friends);
        Log.d("ZZZgetDataFromDb", "  putDataInDb success");
    }

    private DbManager.DbResponse getDataFromDb() {
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        friendsList.removeAllFriends();

        List<Friend> friends = dbManager.getFriends();

        if(friends == null){
            return DbManager.DbResponse.DB_RESPONSE_STATUS_ERROR;
        }

        if(friends.size() > 0){
            friendsList.append(friends);

            return DbManager.DbResponse.DB_RESPONSE_STATUS_SUCCESS;
        } else {
            return DbManager.DbResponse.DB_RESPONSE_STATUS_EMPTY_TABLE;
        }
    }

    private List<Friend> getFriendsFromHttp(final HttpResponse httpResponse){
        List<Friend> friends = null;

        try{
            Gson gson = new Gson().newBuilder().create();
            JsonObject httpObj = gson.fromJson(((HttpResponse)httpResponse).getResponseAsString(), JsonObject.class);
            JsonObject response = httpObj.getAsJsonObject("response");
            JsonArray items = response.getAsJsonArray("items");

            friends = gson.fromJson(items, new TypeToken<ArrayList<Friend>>() {}.getType());
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            return friends;
        }
    }

    private void loadApiImages(List<Friend> friends) throws Exception {
        try{
            for(int i = 0; i < friends.size(); i++) {
                Friend friend = friends.get(i);
                String photoUrl = friend.getPhotoUrl();

                if (!photoUrl.isEmpty()) {
                    byte[] imageBytes = ImageLoader.getInstance().getBytesFromFile(new URL(photoUrl));
                    if (imageBytes != null && imageBytes.length > 0) {
                        ContentValues contentPhotoBytes = new ContentValues();
                        contentPhotoBytes.put(Friend.PHOTO_BYTES, imageBytes);
                        friend.setPhotoBytes(contentPhotoBytes);
                    }
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
            throw new CallbackExceptionFactory.Companion.HttpException(EXCEPTION_LOADING_FRIENDS_API);
        }
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
