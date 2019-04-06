package com.vvsemir.kindawk.service;

import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.provider.NewsWall;
import com.vvsemir.kindawk.provider.NewsWallProvider;
import com.vvsemir.kindawk.provider.ProviderManager;
import com.vvsemir.kindawk.provider.UserProfile;
import com.vvsemir.kindawk.provider.UserProfileProvider;
import com.vvsemir.kindawk.utils.ICallback;

public class ProviderIntentService extends StickyIntentService {
    public static final String PROVIDER_INTENT_SERVICE = "ProviderIntentService";

    public static final String ACTION_ACCOUNT_GET_PROFILE_INFO = "action.Account.GetProfileInfo";
    public static final String ACTION_ACCOUNT_GET_PROFILE_INFO_RESPONSE = "action.Account.GetProfileInfo.Response";
    public static final String ACTION_FRIENDS_GET = "action.Friends.Get";
    public static final String ACTION_FRIENDS_GET_RESPONSE = "action.Friends.Get.Response";
    public static final String ACTION_WALL_GET = "action.Wall.Get";
    public static final String ACTION_WALL_GET_RESPONSE = "action.Wall.Get.Response";

    private static final String EXTRA_REQUEST_PARAMS = "Provider.Request.Params";
    public static final String EXTRA_RESPONSE_DATA = "Provider.Response.Data";

    public ProviderIntentService(String name) {
        super(name);
    }

    public ProviderIntentService() {
        super(PROVIDER_INTENT_SERVICE);
        ProviderManager.instance.createProvider(new NewsWallProvider());
        ProviderManager.instance.createProvider(new UserProfileProvider());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void getAccountProfileInfo(final Context context) {
        startService(context, ACTION_ACCOUNT_GET_PROFILE_INFO, null);
    }

    public static void getFriends(final Context context, final RequestParams params) {
        startService(context, ACTION_FRIENDS_GET, params);
    }

    public static void getWall(final Context context, final RequestParams params) {
        startService(context, ACTION_WALL_GET, params);
    }

    private static void startService(final Context context, final String action, final RequestParams params) {
        Intent intent = new Intent(context, ProviderIntentService.class);
        intent.setAction(action);
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
            } else if (ACTION_WALL_GET.equals(action)) {
                final RequestParams params = intent.getParcelableExtra(EXTRA_REQUEST_PARAMS);
                handleWallGet(params);
            }
        }
    }

    private void handleAccountGetProfileInfo() {
        UserProfileProvider dataProvider = (UserProfileProvider)ProviderManager.instance.getProvider(UserProfileProvider.class);

        UserProfile profileData = dataProvider.loadData(null);

        Intent responseIntent = new Intent();
        responseIntent.setAction(ACTION_ACCOUNT_GET_PROFILE_INFO_RESPONSE);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtra(EXTRA_RESPONSE_DATA, profileData);
        Log.d("INTENT_SERVICE", "onResult comes profile name: " + ((UserProfile) profileData).getHomeTown());
        sendBroadcast(responseIntent);
    }

    private void handlefriendsGet(RequestParams params) {

        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleWallGet(RequestParams params) {
        NewsWallProvider dataProvider = (NewsWallProvider)ProviderManager.instance.getProvider(NewsWallProvider.class);

        NewsWall news = dataProvider.loadData(params);

        Intent responseIntent = new Intent();
        responseIntent.setAction(ACTION_WALL_GET_RESPONSE);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtra(EXTRA_RESPONSE_DATA, news);
        Log.d("INTENT_SERVICE", "onResult comes news count: " + news.getCount());
        sendBroadcast(responseIntent);
    }

}
