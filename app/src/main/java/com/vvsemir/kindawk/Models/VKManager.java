package com.vvsemir.kindawk.Models;

import android.content.Context;

public class VKManager {

    private VKAuthManager authManager;

    VKManager() {
        authManager = new VKAuthManager();
    }

    public boolean isLoggedIn(Context context){
        return authManager.userIsLoggedIn(context);
    }

    public void loadTokenFromUrl(String url, Context context)  throws Exception {
        String[] authData;
        authData = authManager.ParseUrlForToken(url);

        if(authData == null || authData.length != 2 ||
           authData[0] == null || authData[1] == null || authData[0].length() == 0 || authData[1].length()==0)
            throw new Exception("Failed to parse redirect url "+ url);
        authManager.SaveAuthPrefs(authData, context);
    }

    public VKAuthManager.VKAccessToken getAccessToken(Context context){
        return authManager.getVKAccessToken(context);
    }


    }
