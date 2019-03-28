package com.vvsemir.kindawk.Models;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class VkApiIntentService extends IntentService {
    private static final String ACTION_ACCOUNT_GET_PROFILE_INFO = "com.vvsemir.kindawk.Models.action.Account.GetProfileInfo";
    private static final String ACTION_FRIENDS_GET = "com.vvsemir.kindawk.Models.action.Friends.Get";

    private static final String EXTRA_REQUEST_PARAMS = "com.vvsemir.kindawk.Models.extra.Request.Params";

    public VkApiIntentService() {
        super("VkApiIntentService");
    }

    public static void getAccountProfileInfo(Context context) {
        Intent intent = new Intent(context, VkApiIntentService.class);
        intent.setAction(ACTION_ACCOUNT_GET_PROFILE_INFO);
        context.startService(intent);
    }

    public static void getFriends(Context context, RequestParams params) {
        Intent intent = new Intent(context, VkApiIntentService.class);
        intent.setAction(ACTION_FRIENDS_GET);
        intent.putExtra(EXTRA_REQUEST_PARAMS, params);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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

        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handlefriendsGet(RequestParams params) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
