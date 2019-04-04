package com.vvsemir.kindawk;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.vvsemir.kindawk.auth.AuthManager;

import static com.vvsemir.kindawk.service.Constants.URL_ERROR;
import static com.vvsemir.kindawk.auth.AuthManager.APP_VKCLIENT_AUTH_REDIRECT;

public class LoginActivity extends AppCompatActivity {
    static final String TAG="DEBUG LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AuthManager.isUserLoggedIn()) {
        //if(true) {
            StartUserActivity();

            return;
        }

        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);


        WebView webView = (WebView)findViewById(R.id.webViewAuth);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new VkWebViewClient());
        webView.setVerticalScrollBarEnabled(false);
        webView.loadUrl(prepareUrl());
    }

    private void StartUserActivity(){
        Intent intent=new Intent(this, UserActivity.class);
        startActivity(intent);
        finish();
    }
    private String prepareUrl(){
        return AuthManager.getAuthUrl();
    }

    class VkWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            parseUrl(url);
        }

        private void parseUrl(String url) {
            try {
                if (url == null)
                    return;
                if (url.startsWith(APP_VKCLIENT_AUTH_REDIRECT)) {
                    if (!url.contains(URL_ERROR)) {

                        AuthManager.saveTokenAfterLogin(url);
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
