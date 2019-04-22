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
import java.util.List;

public class NewsWallProvider extends BaseProvider<NewsWall> {
    public static final String EXCEPTION_LOADING_API = "Sorry, can not read posts from API";
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

        requestParams = request;
        addInitialParams();
    }

    @Override
    public void run() {
        loadData();
    }

    void loadData() {
        try {
            DbManager.DbResponse dbResponse = getDataFromDb();
            Log.d("WWW getDatFromDb", "  response = " + dbResponse);

            if (dbResponse == DbManager.DbResponse.DB_RESPONSE_STATUS_ERROR ||
                    dbResponse == DbManager.DbResponse.DB_RESPONSE_STATUS_EMPTY_TABLE) {
                newsWall.removeAllNews();

                if (rangeHelper.checkNextApiRequest()) {
                    List<NewsPost> posts = loadApiData();

                    if (posts != null && posts.size() > 0) {

                        putDataInDb(posts);

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

                if(posts != null && posts.size() > 0) {
                    loadApiImagesForRange(posts);
                }
            }

            Log.d("WWW loadApiData", "  loadApiData");
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

    private void putDataInDb(List<NewsPost> posts){
        DbManager dbManager = ProviderService.getInstance().getDbManager();
        dbManager.insertNewsWall(posts);
        Log.d("WWW putDataFromDb", "  putDataInDb success");
    }

    private DbManager.DbResponse getDataFromDb(){
        DbManager dbManager = ProviderService.getInstance().getDbManager();

        if(rangeHelper.startPos == 0) {
            newsWall.removeAllNews();
            Log.d("WWW getDatFromDb", " rangeHelper.startPos=" + rangeHelper.startPos +
                    " rangeHelper.endPos=" + rangeHelper.endPos );
        }

        Log.d("WWW getDatFromDb"," newsWall.hash=" + newsWall.hashCode() );

        List<NewsPost> posts = dbManager.getNewsWallRange(rangeHelper.startPos + 1, rangeHelper.endPos + 1); //rawid started witn 1 not 0

        if(posts == null){
            return DbManager.DbResponse.DB_RESPONSE_STATUS_ERROR;
        }

        if(posts.size() > 0){
            Log.d("WWW getDatFromDb", "  newsWall. old size = " + newsWall.getCount());
            newsWall.appendPosts(posts);
            Log.d("WWW getDatFromDb", "  getDataFromDb success, db posts size = " + posts.size() +
                    "  newsWall. new size = " + newsWall.getCount());

            return DbManager.DbResponse.DB_RESPONSE_STATUS_SUCCESS;
        } else {

            if(rangeHelper.startPos == 0) {
                return DbManager.DbResponse.DB_RESPONSE_STATUS_EMPTY_TABLE;
            }
            else{
                return DbManager.DbResponse.DB_RESPONSE_STATUS_EMPTY_CURSOR;
            }
        }
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
            if(nextFromChainRequest != null)
                Log.d("WWW checkNextApiRequest", " nextFromChainRequest=" + nextFromChainRequest);
            else
                Log.d("WWW checkNextApiRequest", " nextFromChainRequest= 0");

            return checkNext;
        }
    }
}
