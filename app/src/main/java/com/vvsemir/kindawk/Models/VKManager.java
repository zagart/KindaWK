package com.vvsemir.kindawk.Models;

import android.content.Context;

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


}
