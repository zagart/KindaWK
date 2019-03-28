package com.vvsemir.kindawk;

import android.app.Application;
import android.content.Context;

import com.vvsemir.kindawk.auth.AuthManager;

public class KindawkApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AuthManager.Companion.getInstance(getApplicationContext());
    }
}
