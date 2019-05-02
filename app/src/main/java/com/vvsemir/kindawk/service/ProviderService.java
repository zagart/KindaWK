package com.vvsemir.kindawk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vvsemir.kindaimageloader.ILoaderCallback;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.db.DbManager;
import com.vvsemir.kindawk.db.DbOpenHelper;
import com.vvsemir.kindawk.provider.DbCleanerProvider;
import com.vvsemir.kindawk.provider.FriendsList;
import com.vvsemir.kindawk.provider.FriendsProvider;
import com.vvsemir.kindawk.provider.NewsWall;
import com.vvsemir.kindawk.provider.NewsWallProvider;
import com.vvsemir.kindawk.provider.Photo;
import com.vvsemir.kindawk.provider.PhotosProvider;
import com.vvsemir.kindawk.provider.UserProfile;
import com.vvsemir.kindawk.provider.UserProfileProvider;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ProviderService extends Service {
    public static final String EXCEPTION_SERVICE_NOT_STARTED = "Sorry, Provider Service not started";

    private final IBinder binder = new ActivityBinder();
    private static ProviderService instance;
    private ExecutorService executorService;
    private DbManager dbManager;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
        //return START_NOT_STICKY;
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
        dbManager.onDestroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public static void getAccountProfileInfo(final RequestParams params, final ICallback<UserProfile> callback) {
        if (instance == null) {
            /*new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    callback.onError(new CallbackExceptionFactory.Companion.ServiceException(EXCEPTION_SERVICE_NOT_STARTED));
                }
            }, 300);*/
            return;
        }

        UserProfileProvider dataProvider = new UserProfileProvider(callback);
        dataProvider.setRequestParams(params);
        instance.executorService.execute(dataProvider);
    }

    public static void cleanProfileData() {
        DbCleanerProvider dataProvider = new DbCleanerProvider(Arrays.asList(DbOpenHelper.DB_TABLE_PROFILE));
        instance.executorService.execute(dataProvider);
    }

    public static void cleanNewsWall() {
        DbCleanerProvider dataProvider = new DbCleanerProvider(Arrays.asList(DbOpenHelper.DB_TABLE_NEWSFEED));
        instance.executorService.execute(dataProvider);
    }

    public static void cleanFriends() {
        DbCleanerProvider dataProvider = new DbCleanerProvider(Arrays.asList(DbOpenHelper.DB_TABLE_FRIENDS));
        instance.executorService.execute(dataProvider);
    }

    public static void getWall(final RequestParams params, ICallback<NewsWall> callback) {
        if (instance == null) {
            return;
        }

        NewsWallProvider dataProvider = new NewsWallProvider(callback);
        dataProvider.setRequestParams(params);
        instance.executorService.execute(dataProvider);
    }

    public static void getFriends(ICallback<FriendsList> callback) {
        if (instance == null) {
            return;
        }

        FriendsProvider dataProvider = new FriendsProvider(callback);
        dataProvider.setRequestParams(null);
        instance.executorService.execute(dataProvider);
    }

    public static void getPhotosByOwner(final RequestParams params, ILoaderCallback<List<Photo>> callback ) {
        if (instance == null) {
            return;
        }

        PhotosProvider dataProvider = new PhotosProvider(callback);
        dataProvider.setRequestParams(params);
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

    public class ActivityBinder extends Binder {
        public   ProviderService getService() {
            return ProviderService.this;
        }
    }

    /*
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
    }*/
}
