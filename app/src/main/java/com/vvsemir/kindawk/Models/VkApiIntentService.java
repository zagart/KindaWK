package com.vvsemir.kindawk.Models;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.utils.ICallback;

public class VkApiIntentService extends IntentService {
    public static final String ACTION_ACCOUNT_GET_PROFILE_INFO = "action.Account.GetProfileInfo";
    public static final String ACTION_ACCOUNT_GET_PROFILE_INFO_RESPONSE = "action.Account.GetProfileInfo.Response";
    public static final String EXTRA_PROFILE_KEY_OUT = "Key.Account.GetProfileInfo.ResponseKey";

    public static final String ACTION_FRIENDS_GET = "com.vvsemir.kindawk.Models.action.Friends.Get";

    private static final String EXTRA_REQUEST_PARAMS = "com.vvsemir.kindawk.Models.extra.Request.Params";


    public VkApiIntentService(String name) {
        super(name);
    }

    public VkApiIntentService() {
        super("VkApiIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void getAccountProfileInfo(Context context) {
        Intent intent = new Intent(context, VkApiIntentService.class);
        intent.setAction(ACTION_ACCOUNT_GET_PROFILE_INFO);
        context.startService(intent);
    }

    public void getFriends(Context context, RequestParams params) {
        Intent intent = new Intent(context, VkApiIntentService.class);
        intent.setAction(ACTION_FRIENDS_GET);
        intent.putExtra(EXTRA_REQUEST_PARAMS, params);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("INTENT_SERVICE", "onHandleIntent Called");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ACCOUNT_GET_PROFILE_INFO.equals(action)) {
                handleAccountGetProfileInfo();
            } else if (ACTION_FRIENDS_GET.equals(action)) {
                final RequestParams params = intent.getParcelableExtra(EXTRA_REQUEST_PARAMS);
                handlefriendsGet(params);
            }
        }
    }

    private void handleAccountGetProfileInfo() {
        new HttpRequestTask().execute(
                new HttpRequest("account.getProfileInfo", false, null),
                new ICallback<HttpResponse>() {
                    @Override
                    public void onResult(HttpResponse result) {
                        Log.d("INTENT_SERVICE", "onResult comes");
                        Intent responseIntent = new Intent();
                        responseIntent.setAction(ACTION_ACCOUNT_GET_PROFILE_INFO_RESPONSE);
                        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        responseIntent.putExtra(EXTRA_PROFILE_KEY_OUT, result);
                        Log.d("INTENT_SERVICE", "onResult comes : " + result.getResponseAsString());
                        sendBroadcast(responseIntent);
                    }
                });
        /*

        isLoading = true;
        mAdapter.setShowLastViewAsLoading(true);

        WebService.HttpRequestTask(request, new ICallback<HttpResponse>() {

            @Override
            public void onResult(HttpResponse result) {
                mAdapter.addItems(result);
                isLoading = false;
            }
        });
        */

        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handlefriendsGet(RequestParams params) {

        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
