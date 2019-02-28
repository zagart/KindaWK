package com.vvsemir.kindawk.Models;

import android.content.Context;

import static com.vvsemir.kindawk.Models.Constants.*;

public class VKManager {
    private VKAuthManager authManager;


    private static VKManager instance = new VKManager();
    private VKManager() {
        authManager = new VKAuthManager();
    }
    public static VKManager getInstance(){
        return instance;
    }

    public boolean isLoggedIn(Context context){
        return authManager.userIsLoggedIn(context);
    }

    public void loadTokenFromUrl(String url, Context context) throws Exception{
        try {
            authManager.SaveAuthPrefs(authManager.ParseUrlForToken(url), context);
        }catch (Exception e) {
            throw e;
        }
    }

    public VKAuthManager.VKAccessToken getAccessToken(Context context){
        return authManager.getVKAccessToken(context);
    }

    public String getVKAuthUrl(){
        return "https://oauth.vk.com/authorize?client_id=" + APP_VKCLIENT_ID +
                "&scope=" +  APP_VKCLIENT_SCOPE +
                "&redirect_uri=https://oauth.vk.com/blank.html" +
                "&display=mobile" +
                "&v=" + APP_VKCLIENT_API_V +
                "&response_type=token";
    }

    public AccountData getAccountInfo(){
        AccountData data = new AccountData().load();
    }

}
