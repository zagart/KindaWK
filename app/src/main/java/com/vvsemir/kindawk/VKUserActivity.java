package com.vvsemir.kindawk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.vvsemir.kindawk.Models.VKAuthManager;
import com.vvsemir.kindawk.Models.VKManager;

public class VKUserActivity extends AppCompatActivity {

    static final String TAG="DEBUG VKUserActivity";

    VKManager vkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vkuser);

        VKAuthManager.VKAccessToken vkToken = vkManager.getAccessToken(getApplicationContext());
        Log.i(TAG, "access_token = " + vkToken.accessToken + "user_id = " + vkToken.userId);


    }
}
