package com.vvsemir.kindawk.Models;

public class VKManager {

    private VKAuthManager authManager;

    public VKManager() {
        authManager = new VKAuthManager();
    }

    public boolean isLoggedIn(){
        return authManager.userIsLoggedIn();
    }

}
