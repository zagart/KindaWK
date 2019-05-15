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
import com.vvsemir.kindawk.provider.NotificationsProvider;
import com.vvsemir.kindawk.provider.Photo;
import com.vvsemir.kindawk.provider.PhotosProvider;
import com.vvsemir.kindawk.provider.UserProfile;
import com.vvsemir.kindawk.provider.UserProfileProvider;
import com.vvsemir.kindawk.provider.observer.IEvent;
import com.vvsemir.kindawk.provider.observer.IEventNotifier;
import com.vvsemir.kindawk.provider.observer.IEventObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static com.vvsemir.kindawk.auth.AuthManager.PREFERENCE_NEWS_CHECK_DELAY;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;


public class ProviderService extends Service implements IEventNotifier {
    public static final String EXCEPTION_SERVICE_NOT_STARTED = "Sorry, Provider Service not started";

    private final IBinder binder = new ActivityBinder();
    private static ProviderService instance;
    private ExecutorService executorService;
    private ScheduledExecutorService scheduledService;
    private DbManager dbManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<IEventObserver> eventObservers = new ArrayList<>();

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
        scheduledService = Executors.newScheduledThreadPool(1);
        dbManager = new DbManager(AuthManager.getCurrentContext());
    }

    @Override
    public void onDestroy() {
        scheduledService.shutdownNow();
        executorService.shutdownNow();
        dbManager.onDestroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        int checkNewsDelay = AuthManager.getAppPreferences().get(PREFERENCE_NEWS_CHECK_DELAY, 1);

        final ScheduledFuture notifierHandle =
            scheduledService.scheduleAtFixedRate(new NotificationsProvider(new ICallback<Integer>() {
                    @Override
                    public void onResult(Integer result) {
                        switch(result) {
                            case IEvent.NEW_POST:
                                notifyObservers(IEvent.NEW_POST);

                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                })

            , 10, 30, SECONDS);

        scheduledService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                int watcherDelay = AuthManager.getAppPreferences().get(PREFERENCE_NEWS_CHECK_DELAY, 1);
                if(watcherDelay == 0){
                    notifierHandle.cancel(true); }
               }

        }, 1,1, MINUTES);


        return binder;
    }

    void cancelScheduledService (final ScheduledFuture notifierHandle) {
        scheduledService.schedule(new Runnable() {
            public void run() { notifierHandle.cancel(true); }
        }, 0, SECONDS);

        notifierHandle.cancel(false);
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {

        super.onRebind(intent);
    }

    public void getAccountProfileInfo(final RequestParams params, final ICallback<UserProfile> callback) {
        if (instance == null) {

            return;
        }

        UserProfileProvider dataProvider = new UserProfileProvider(callback);
        dataProvider.setRequestParams(params);
        instance.executorService.execute(dataProvider);
    }

    public void cleanProfileData(final ICallback<Integer> callback) {
        DbCleanerProvider dataProvider = new DbCleanerProvider(Arrays.asList(DbOpenHelper.DB_TABLE_PROFILE), callback);
        instance.executorService.execute(dataProvider);
    }

    public void cleanNewsWall(final ICallback<Integer> callback) {
        DbCleanerProvider dataProvider = new DbCleanerProvider(Arrays.asList(DbOpenHelper.DB_TABLE_NEWSFEED), callback);
        instance.executorService.execute(dataProvider);
    }

    public void cleanFriends(final ICallback<Integer> callback) {
        DbCleanerProvider dataProvider = new DbCleanerProvider(Arrays.asList(DbOpenHelper.DB_TABLE_FRIENDS), callback);
        instance.executorService.execute(dataProvider);
    }

    public void getWall(final RequestParams params, ICallback<NewsWall> callback) {
        if (instance == null) {
            return;
        }

        NewsWallProvider dataProvider = new NewsWallProvider(callback);
        dataProvider.setRequestParams(params);
        instance.executorService.execute(dataProvider);
    }

    public void getFriends(ICallback<FriendsList> callback) {
        if (instance == null) {
            return;
        }

        FriendsProvider dataProvider = new FriendsProvider(callback);
        dataProvider.setRequestParams(null);
        instance.executorService.execute(dataProvider);
    }

    public void getPhotosByOwner(final RequestParams params, ILoaderCallback<List<Photo>> callback ) {
        if (instance == null) {
            return;
        }

        PhotosProvider dataProvider = new PhotosProvider(callback);
        dataProvider.setRequestParams(params);
        instance.executorService.execute(dataProvider);
    }

    public void addOwnersPhoto(final  String photoPath, final RequestParams params, ILoaderCallback<List<Photo>>  callback ) {
        if (instance == null) {
            return;
        }

        //PhotosProvider dataProvider = new PhotosProvider(callback);
        //dataProvider.setRequestParams(params);
        //instance.executorService.execute(dataProvider);
        //List<Photo> photos = new ArrayList<>();
        //Photo photo = new Photo();
        //photo.setOwnerId(AuthManager.getCurrentToken().getUserId());
        //List<Photo.PhotoSize> photoSizes = new ArrayList<>();
        //photo.setPhotoSizes(photoSizes);
        //photos.add(photo);
        //callback.onResult(photos);
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

    @Override
    public void registerObserver(IEventObserver observer) {
        if (!eventObservers.contains(observer)) {
            eventObservers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(IEventObserver observer) {
        eventObservers.remove(observer);
    }

    @Override
    public void notifyObservers(@IEvent final Integer event) {
        for (final IEventObserver observer : eventObservers) {
            observer.updateOnEvent(event);
        }
    }
}
