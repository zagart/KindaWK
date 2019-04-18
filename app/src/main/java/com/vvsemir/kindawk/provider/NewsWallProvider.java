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
import java.util.Date;

public class NewsWallProvider extends BaseProvider<NewsWall> {
    static final String EXCEPTION_LOADING_API = "Sorry, can not read posts from API";
    static final String ARG_PARAM_REQUEST_METHOD = "newsfeed.get";
    static final String ARG_PARAM_REQUEST_FILTERS = "post";
    static final String ARG_PARAM_REQUEST_FIELDS = "photo_100";
    static final int ARG_PARAM_REQUEST_MAX_PHOTOS = 1;
    public static final int PAGE_SIZE = 10;
    public static final int MAX_TOTAL_POSTS = 100;

    public static final String PARAM_REQUEST_RANGE_START = "wall.get.range.start";
    public static final String PARAM_REQUEST_RANGE_END = "wall.get.range.end";

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
        requestParams.put("count", rangeHelper.endPos - rangeHelper.startPos + 1);
        requestParams.put("max_photos",ARG_PARAM_REQUEST_MAX_PHOTOS);
        requestParams.put("filters",ARG_PARAM_REQUEST_FILTERS);
        requestParams.put("fields",ARG_PARAM_REQUEST_FIELDS);

        if(rangeHelper.startPos > 0 && rangeHelper.nextFromChainRequest != null &&
                !rangeHelper.nextFromChainRequest.isEmpty()){
            requestParams.put("start_from", rangeHelper.nextFromChainRequest);
            Log.d("WWW NEXT CHAIN", "  NEXT CHAIN");
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

        super.setRequestParams(request);
        addInitialParams();
    }

    @Override
    public void run() {
        loadData();
    }

    void loadData() {

        if(!getDataFromDb()) {

            if (rangeHelper.checkNextApiRequest()) {
                loadApiData();
                putDataInDb();
            }
        }

        ProviderService.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onResult(newsWall);
            }
        });
    }

    void loadApiData() {
        try {
            HttpResponse httpResponse = new HttpRequestTask().execute(
                    new HttpRequest(ARG_PARAM_REQUEST_METHOD, false, requestParams), null);

            if (httpResponse != null) {
                NewsWallGsonHelper.createInstance(newsWall, this).setFromHttp(httpResponse);
            }

            loadApiImagesForRange(rangeHelper.startPos, rangeHelper.endPos);

            Log.d("WWW loadApiData", "  loadApiData");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    void loadApiImagesForRange(int startPos, int endPos){
        try {
            //if(endPos >= news.getCount()){
            //    Log.d("EXEX loadApiImages ", " endPos = " + endPos  + "news.getCount()) = " + news.getCount());
            //    throw new CallbackExceptionFactory.Companion.HttpException(EXCEPTION_LOADING_API);
            //}

            if(endPos > newsWall.getCount()){
                endPos = newsWall.getCount();
            }

            for (int i = startPos; i < endPos; i++) {
                NewsPost post = newsWall.getItem(i);
                String postPhotoUrl = post.getPostPhotoUrl();
                String sourcePhotoUrl = post.getSourcePhotoUrl();

                if (postPhotoUrl != null && !postPhotoUrl.isEmpty()) {
                    byte[] imageBytes = ImageLoader.getInstance().getBytesFromFile(new URL(postPhotoUrl));
                    if (imageBytes != null && imageBytes.length > 0) {
                        ContentValues contentPhotoBytes = new ContentValues();
                        contentPhotoBytes.put(NewsPost.PHOTO_BYTES, imageBytes);
                        post.setPostPhoto(contentPhotoBytes);
                    }
                }

                if (sourcePhotoUrl != null && !sourcePhotoUrl.isEmpty()) {
                    byte[] imageBytes = ImageLoader.getInstance().getBytesFromFile(new URL(sourcePhotoUrl));
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

    private void putDataInDb(){
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        dbManager.removeAllNews();
        dbManager.insertNewsWall(newsWall);
        Log.d("WWW putDataFromDb", "  putDataInDb success");
    }

    private boolean getDataFromDb(){
        DbManager dbManager = ProviderService.getInstance().getDbManager();

        if(rangeHelper.startPos == 0) {
            newsWall.removeAllNews();
        }

        Cursor cursor = dbManager.getNewsWall(rangeHelper.startPos + 1, rangeHelper.endPos + 1);
        boolean success = cursor.getCount() > 0;

        while (cursor.moveToNext()) {
            NewsPost post = new NewsPost();

            post.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
            post.setSourceId(cursor.getInt(cursor.getColumnIndexOrThrow("source_id")));
            post.setDateUnixTime(new Date(cursor.getLong(cursor.getColumnIndexOrThrow("date"))) );
            post.setPostId(cursor.getInt(cursor.getColumnIndexOrThrow("post_id")));
            post.setPostText(cursor.getString(cursor.getColumnIndexOrThrow("post_text")));
            post.setSourceName(cursor.getString(cursor.getColumnIndexOrThrow("source_name")));
            post.setSourcePhotoUrl(cursor.getString(cursor.getColumnIndexOrThrow("source_photo_url")));
            post.setPostPhotoUrl(cursor.getString(cursor.getColumnIndexOrThrow("post_photo_url")));

            {
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("source_photo_bytes"));
                ContentValues contentPhotoBytes = new ContentValues();
                contentPhotoBytes.put(NewsPost.PHOTO_BYTES, imageBytes);
                post.setSourcePhoto(contentPhotoBytes);
            }

            {
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("post_photo_bytes"));
                ContentValues contentPhotoBytes = new ContentValues();
                contentPhotoBytes.put(NewsPost.PHOTO_BYTES, imageBytes);
                post.setPostPhoto(contentPhotoBytes);
            }

            Log.d("WWW getDatFromDb", "  getDataFromDb success");
            newsWall.addPost(post);
        }

        cursor.close();
        return success;
    }


    @Override
    public void resetData() {
        newsWall.removeAllNews();
    }

    public void setNextFromChainRequest(String nextFrom) {
        rangeHelper.nextFromChainRequest = nextFrom;
    }

    private class RangeHelper {
        int startPos = 0;
        int endPos = 0;
        String nextFromChainRequest;

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
