package com.vvsemir.kindawk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.vvsemir.kindawk.Models.VKManager;

public class MainActivity extends AppCompatActivity {

    VKManager vkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(vkManager.isLoggedIn())
            StartUserActivity();

        WebView wbWebView = (WebView)findViewById(R.id.webViewAuth);
        wbWebView.getSettings().setJavaScriptEnabled(true);
        wbWebView.clearCache(true);
    }

    private void StartUserActivity(){
        Intent intent=new Intent(this, VKUserActivity.class);
        startActivity(intent);
        finish();
    }
}
