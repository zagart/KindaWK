package com.vvsemir.kindawk.Models;

import java.util.HashMap;

import static com.vvsemir.kindawk.Models.Constants.*;


public class VKAuthManager {

    private VKAccessToken accessToken;
    public class VKAccessToken{
        private long usedId;
        private String accessToken;

        public VKAccessToken(HashMap<String, String> params ){
            usedId = Long.getLong(params.get(USER_ID));
            accessToken  = Long.getLong(params.get(ACCESS_TOKEN));
        }

        public boolean isValid(){
             return !accessToken.isEmpty();
        }
    }

    public boolean userIsLoggedIn(){
      return accessToken.isValid();
    }
}
