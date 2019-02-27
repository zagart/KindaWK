package com.vvsemir.kindawk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vvsemir.kindawk.Models.VKManager;

import static com.vvsemir.kindawk.Models.Constants.*;

public class MainActivity extends AppCompatActivity {

    static final String TAG="DEBUG MainActivity";
    VKManager vkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vkManager = VKManager.getInstance();
        if(vkManager.isLoggedIn(getApplicationContext())) {
            StartUserActivity();
            return;
        }
        setContentView(R.layout.activity_main);

        WebView webView = (WebView)findViewById(R.id.webViewAuth);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new VKWebViewClient());
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl(prepareUrl());
    }

    private void StartUserActivity(){
        Intent intent=new Intent(this, VKUserActivity.class);
        startActivity(intent);
        finish();
    }
    private String prepareUrl(){
        return "https://oauth.vk.com/authorize?client_id=" + APP_VKCLIENT_ID +
                "&scope=" +  APP_VKCLIENT_SCOPE +
                "&redirect_uri=https://oauth.vk.com/blank.html" +
                "&display=mobile" +
                "&v=" + APP_VKCLIENT_API_V +
                "&response_type=token";
    }
    class VKWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            parseUrl(url);
        }

        private void parseUrl(String url) {
            try {
                if (url == null)
                    return;
                if (url.startsWith("https://oauth.vk.com/blank.html")) {
                    if (!url.contains("error=")) {

                        vkManager.loadTokenFromUrl(url, getApplicationContext());
                        //intent.putExtra("token", auth[0]);
                        //intent.putExtra("user_id", Long.parseLong(auth[1]));
                        StartUserActivity();
                    }
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
