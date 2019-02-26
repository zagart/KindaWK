package com.vvsemir.kindawk.Models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.CookieManager;

import java.util.HashMap;

import static com.vvsemir.kindawk.Models.Constants.*;


public class VKAuthManager {

    static final String TAG="DEBUG VKAuthManager";

    private VKAccessToken vkAccessToken;

    public class VKAccessToken {
        public long userId;
        public String accessToken;

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
            if(accessToken.isEmpty() && getPreferences(context).contains(ACCESS_TOKEN))
                accessToken = getPreferences(context).getString(ACCESS_TOKEN, "");
            return !accessToken.isEmpty();
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

     String[] ParseUrlForToken(String url){
         String token   = Utilits.extractPattern(url, "_token=(.*?)&");
         String user_id = Utilits.extractPattern(url, "user_id=(\\d*)");

         Log.i(TAG, "access_token = " + token + "user_id = " + user_id);
         return new String[]{token, user_id};
      }

    public void SaveAuthPrefs(String[] authData, Context context)
    {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(ACCESS_TOKEN, authData[0]);
        editor.putString(USER_ID, authData[1]);
        editor.apply();
        vkAccessToken.Set(authData[0], Long.getLong(authData[1]));
    }

    public  VKAccessToken getVKAccessToken(Context context){
        return  vkAccessToken;
    }

    public SharedPreferences getPreferences(Context context) {
            return  context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
