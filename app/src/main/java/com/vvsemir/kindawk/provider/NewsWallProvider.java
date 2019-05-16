package com.vvsemir.kindawk.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.db.DbManager;
import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.service.CallbackExceptionFactory;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewsWallProvider extends BaseProvider<NewsWall> {
    public static final String EXCEPTION_LOADING_API = "Sorry, can not read posts from API";
    public static final String EXCEPTION_READING_DB = "Sorry, can not read posts from DB";

    static final String ARG_PARAM_REQUEST_METHOD = "newsfeed.get";
    static final String ARG_PARAM_REQUEST_FILTERS = "post,photo,photo_tag, wall_photo";
    static final String ARG_PARAM_REQUEST_FIELDS = "photo_100";
    static final int ARG_PARAM_REQUEST_MAX_PHOTOS = 1;
    public static final int PAGE_SIZE = 10;
    public static final int MAX_TOTAL_POSTS = 100;

    public static final String PARAM_REQUEST_RANGE_START = "range.start";
    public static final String PARAM_REQUEST_RANGE_END = "range.end";

    private RangeHelper rangeHelper  = new RangeHelper();
    private NewsWall newsWall = new NewsWall();

    public NewsWallProvider(ICallback<NewsWall> callback) {
        super(callback);
    }

    private void addInitialParams(){
        if(requestParams == null) {
            requestParams = new RequestParams();
        }

        requestParams.put("owner_id", AuthManager.getCurrentToken().getUserId());
        //requestParams.put("count", rangeHelper.endPos - rangeHelper.startPos + 1);
        //requestParams.put("count", 100);
        requestParams.put("start_time", getStartTimeFromPrefs());
        requestParams.put("max_photos",ARG_PARAM_REQUEST_MAX_PHOTOS);
        requestParams.put("filters",ARG_PARAM_REQUEST_FILTERS);
        requestParams.put("fields",ARG_PARAM_REQUEST_FIELDS);

        if(rangeHelper.startPos > 0 && rangeHelper.nextFromChainRequest != null &&
                !rangeHelper.nextFromChainRequest.isEmpty()){
            requestParams.put("start_from", rangeHelper.nextFromChainRequest);
        }
    }



    @Override
    public void setRequestParams(RequestParams request) {
        rangeHelper.setRange(0, PAGE_SIZE);

        if (request.contains(PARAM_REQUEST_RANGE_START) && request.contains(PARAM_REQUEST_RANGE_END)) {
            rangeHelper.setRange(Integer.parseInt(request.getParam(PARAM_REQUEST_RANGE_START)),
                    Integer.parseInt(request.getParam(PARAM_REQUEST_RANGE_END)));
            request.removeParam(PARAM_REQUEST_RANGE_START);
            request.removeParam(PARAM_REQUEST_RANGE_END);
        }

        requestParams = request;
        addInitialParams();
    }

    @Override
    public void run() {
        loadData();
    }

    synchronized void loadData() {
        try {
            DbManager.DbResponse dbResponse = getDataFromDb();
            List<NewsPost> posts = null;

            if ( dbResponse == DbManager.DbResponse.DB_RESPONSE_STATUS_EMPTY_TABLE ) {

                if (rangeHelper.checkNextApiRequest()) {
                    posts = loadApiData();

                    if (posts != null && posts.size() > 0) {
                        newsWall.appendPosts(posts);
                    }
                }
            }

            ProviderService.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onResult(newsWall);
                }
            });

            if (posts != null && posts.size() > 0) {
                putDataInDb(posts);
            }

        } catch (Exception ex){
            ex.printStackTrace();
            ProviderService.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(CallbackExceptionFactory.Companion.createException
                            (CallbackExceptionFactory.THROWABLE_TYPE_ERROR, EXCEPTION_LOADING_API));
                }
            });
        }
    }

    List<NewsPost> loadApiData() {
        List<NewsPost> posts = null;

        try {
            HttpResponse httpResponse = new HttpRequestTask().execute(
                    new HttpRequest(ARG_PARAM_REQUEST_METHOD, false, requestParams), null);

            if (httpResponse != null) {
                posts = NewsWallGsonHelper.createInstance(this).getPostsFromHttp(httpResponse);

                //if(posts != null && posts.size() > 0) {
                //    loadApiImagesForRange(posts);
                //}
            }
        } catch (Exception ex){
            ex.printStackTrace();
            ProviderService.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(CallbackExceptionFactory.Companion.createException
                        (CallbackExceptionFactory.THROWABLE_TYPE_EXCEPTION_HTTP, EXCEPTION_LOADING_API));
                }
            });
        } finally {
            return posts;
        }
    }

    void loadApiImagesForRange( List<NewsPost> posts ){
        try {
            for(NewsPost post : posts){
                String postPhotoUrl = post.getPostPhotoUrl();
                String sourcePhotoUrl = post.getSourcePhotoUrl();

                if (postPhotoUrl != null && !postPhotoUrl.isEmpty()) {
                    byte[] imageBytes = ImageLoader.getBytesFromNetworkFile(new URL(postPhotoUrl));
                    if (imageBytes != null && imageBytes.length > 0) {
                        ContentValues contentPhotoBytes = new ContentValues();
                        contentPhotoBytes.put(NewsPost.PHOTO_BYTES, imageBytes);
                        post.setPostPhoto(contentPhotoBytes);
                    }
                }

                if (sourcePhotoUrl != null && !sourcePhotoUrl.isEmpty()) {
                    byte[] imageBytes = ImageLoader.getBytesFromNetworkFile(new URL(sourcePhotoUrl));
                    if (imageBytes != null && imageBytes.length > 0) {
                        ContentValues contentPhotoBytes = new ContentValues();
                        contentPhotoBytes.put(NewsPost.PHOTO_BYTES, imageBytes);
                        post.setSourcePhoto(contentPhotoBytes);
                    }
                }
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void putDataInDb(List<NewsPost> posts){
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        dbManager.insertNewsWall(posts);
    }

    private DbManager.DbResponse getDataFromDb() throws Exception {
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        newsWall.removeAllNews();

        List<NewsPost> posts = dbManager.getNewsWallRange(rangeHelper.startPos + 1, rangeHelper.endPos + 1); //rawid started witn 1 not 0

        if(posts == null){
            throw new Exception(EXCEPTION_READING_DB);
        }

        if(posts != null && posts.size() > 0){
            newsWall.appendPosts(posts);
            Log.d("WWW getDatFromDb", "  getDataFromDb success, db posts size = " + posts.size() +
                    "  newsWall. new size = " + newsWall.getCount() + " rangeHelper.startPos=" + rangeHelper.startPos +
                    " rangeHelper.endPos=" + rangeHelper.endPos );

            return DbManager.DbResponse.DB_RESPONSE_STATUS_SUCCESS;
        } else {
            return DbManager.DbResponse.DB_RESPONSE_STATUS_EMPTY_TABLE;
        }
    }

    public void setNextFromChainRequest(String nextFrom) {
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        dbManager.insertNewsWallOffset(nextFrom);
        rangeHelper.nextFromChainRequest = nextFrom;
    }

    private long getStartTimeFromPrefs() {
        int newsDatePostedIndx = AuthManager.getAppPreferences().getInt(AuthManager.PREFERENCE_NEWS_DATE_POSTED, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        switch (newsDatePostedIndx) {
            case DatePostedRange.YESTERDAY:
                calendar.add(Calendar.DATE, -1);

                break;
            case DatePostedRange.WEEK:
                calendar.add(Calendar.DATE, -7);

                break;
            case DatePostedRange.MONTH:
                calendar.add(Calendar.DATE, -30);

                break;

        }

        return calendar.getTimeInMillis()/1000;
    }

    interface DatePostedRange {
        int TODAY = 0;
        int YESTERDAY = 1;
        int WEEK = 2;
        int MONTH= 3;
    }

    private class RangeHelper {
        int startPos = 0;
        int endPos = 0;
        String nextFromChainRequest;

        public RangeHelper() {
            DbManager dbManager = ProviderService.getInstance().getDbManager();
            nextFromChainRequest = dbManager.getNewsWallOffset();
        }

        void setRange(int startPos, int endPos) {
            this.startPos = startPos;
            this.endPos = endPos;;
        }

        boolean checkNextApiRequest() {
            boolean checkNext = false;

            if(startPos == 0 || (nextFromChainRequest != null && !nextFromChainRequest.isEmpty()) ) {
                checkNext = true;
            }

            return checkNext;
        }
    }
}
