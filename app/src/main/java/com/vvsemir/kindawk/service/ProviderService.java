package com.vvsemir.kindawk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.db.DbManager;
import com.vvsemir.kindawk.provider.BaseProvider;
import com.vvsemir.kindawk.provider.FriendsList;
import com.vvsemir.kindawk.provider.NewsWall;
import com.vvsemir.kindawk.provider.NewsWallProvider;
import com.vvsemir.kindawk.provider.UserProfile;
import com.vvsemir.kindawk.provider.UserProfileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProviderService extends Service {
    public static final String ACTION_ACCOUNT_GET_PROFILE_INFO = "action.Account.GetProfileInfo";
    public static final String ACTION_FRIENDS_GET = "action.Friends.Get";
    public static final String ACTION_WALL_GET = "action.Wall.Get";
    private static final String EXTRA_REQUEST_PARAMS = "Provider.Request.Params";
    public static final String EXTRA_RESPONSE_DATA = "Provider.Response.Data";
    private static ProviderService instance;

    private ExecutorService executorService;
    private DbManager dbManager;

    private Handler handler = new Handler(Looper.getMainLooper());
    private List<BaseProvider> providers  = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = ProviderService.this;
        executorService = Executors.newCachedThreadPool();
        dbManager = new DbManager(AuthManager.getCurrentContext());
    }

    @Override
    public void onDestroy() {
        executorService.shutdownNow();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void getAccountProfileInfo(ICallback<UserProfile> callback) {
        UserProfileProvider dataProvider = (UserProfileProvider)instance.getProviderFromList(UserProfileProvider.class);

        if(dataProvider == null){
            dataProvider = new UserProfileProvider(callback);
        }

        dataProvider.setRequestParams(null);
        instance.executorService.execute(dataProvider);
    }

    public static void getWall(ICallback<NewsWall> callback, final RequestParams params) {
        NewsWallProvider dataProvider = (NewsWallProvider)instance.getProviderFromList(NewsWallProvider.class);

        if(dataProvider == null){
            dataProvider = new NewsWallProvider(callback);
        }

        dataProvider.setRequestParams(params);
        instance.executorService.execute(dataProvider);
    }

    public static void getFriends(ICallback<FriendsList> callback) {
        FriendsProvider dataProvider = (FriendsProvider)instance.getProviderFromList(FriendsProvider.class);

        if(dataProvider == null){
            dataProvider = new FriendsProvider(callback);
        }

        dataProvider.setRequestParams(null);
        instance.executorService.execute(dataProvider);
    }

    public static ProviderService getInstance() {
        return instance;
    }

    public Handler getHandler() {
        return handler;
    }

    public DbManager getDbManager() {
        return dbManager;
    }

    private BaseProvider getProviderFromList(Class providerClass){
        for(BaseProvider provider : providers){
            if(providerClass.isInstance(provider)){
                return provider;
            }
        }

        return null;
    }

    public static void deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        file.delete();
    }

}
