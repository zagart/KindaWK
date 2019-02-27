package com.vvsemir.kindawk.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.CookieManager;

import java.util.HashMap;

import static com.vvsemir.kindawk.Models.Constants.*;


public class VKAuthManager {

    static final String TAG="DEBUG VKAuthManager";

    private VKAccessToken vkAccessToken;

    public VKAuthManager() {
        vkAccessToken = new VKAccessToken();
    }

    public class VKAccessToken {
        public long userId;
        public String accessToken;

        public VKAccessToken(){

        }
        public VKAccessToken(HashMap<String, String> params) {
            userId = Long.getLong(params.get(USER_ID));
            accessToken = params.get(ACCESS_TOKEN);
        }

        public void clear(){
            userId = 0;
            accessToken = "";
        }

        public boolean isValid(Context context)
        {
            if((accessToken == null || accessToken.isEmpty()) && getPreferences(context).contains(ACCESS_TOKEN))
                accessToken = getPreferences(context).getString(ACCESS_TOKEN, "");
            if(userId == 0 && getPreferences(context).contains(USER_ID))
                userId = Long.parseLong(getPreferences(context).getString(USER_ID, ""));


            //TODO check token expity date later
            if(accessToken == null || accessToken.isEmpty() || userId == 0)
                return false;
            return true;
        }

        void Set(String token, long id)
        {
            userId = id;
            accessToken = token;
        }

    }

    public boolean userIsLoggedIn(Context context) {
        return vkAccessToken.isValid(context);
    }

    public void userLogout(Context context) {
        getPreferences(context).edit().clear().apply();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }
    public void clear(Context context) {
        getPreferences(context).edit().clear().apply();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        vkAccessToken.clear();
     }

     public String[] ParseUrlForToken(String url){
         String token   = Utilits.extractPattern(url, "_token=(.*?)&");
         String user_id = Utilits.extractPattern(url, "user_id=(\\d*)");

         Log.i(TAG, "access_token = " + token + "user_id = " + user_id);
         return new String[]{token, user_id};
      }

    public void SaveAuthPrefs(String[] authData, Context context)   throws Exception
    {
        if(authData == null || authData.length != 2 ||
           authData[0] == null || authData[1] == null ||
           authData[0].length() == 0 || authData[1].length()==0)
            throw new Exception("Failed to parse url to get AUTH token");

        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(ACCESS_TOKEN, authData[0]);
        editor.putString(USER_ID, authData[1]);
        editor.apply();
        vkAccessToken.Set(authData[0], Long.parseLong(authData[1]));
    }

    public  VKAccessToken getVKAccessToken(Context context){
        return  vkAccessToken;
    }

    public SharedPreferences getPreferences(Context context) {
            return  context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
