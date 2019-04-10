package com.vvsemir.kindawk.provider;

import android.content.Context;

import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

public class NewsWallProvider extends BaseProvider<NewsWall> {
    //static final String ARG_PARAM_REQUEST_METHOD = "wall.get";
    static final String ARG_PARAM_REQUEST_METHOD = "newsfeed.get";
    public static final String ARG_PARAM_REQUEST_RANGE_START = "wall.get.range.start";
    public static final String ARG_PARAM_REQUEST_RANGE_END = "wall.get.range.end";

    private Context context;
    private NewsWall news = new NewsWall();

    public NewsWallProvider(ICallback<NewsWall> callback) {
        super(callback);
    }

    private void setInitialParams(){
        if(requestParams != null){
            requestParams.put("owner_id", AuthManager.getCurrentToken().getUserId());
            requestParams.put("count", 100);
            requestParams.put("filter","all");
            requestParams.put("filters","post,photo,photo_tag,wall_photo");
        }
    }



    @Override
    public void setRequestParams(RequestParams request) {
        super.setRequestParams(request);
        setInitialParams();
    }

    @Override
    public void run() {
        loadData();
    }

    void loadData() {
        int rangeStart = 0, rangeEnd = 0;

        if(requestParams.contains(ARG_PARAM_REQUEST_RANGE_START) && requestParams.contains(ARG_PARAM_REQUEST_RANGE_END)){
            rangeStart = Integer.parseInt(requestParams.getParam(ARG_PARAM_REQUEST_RANGE_START));
            rangeEnd = Integer.parseInt(requestParams.getParam(ARG_PARAM_REQUEST_RANGE_END));
            requestParams.removeParam(ARG_PARAM_REQUEST_RANGE_START);
            requestParams.removeParam(ARG_PARAM_REQUEST_RANGE_END);
        }
        if (news.getCount() == 0) {
            HttpResponse httpResponse = new HttpRequestTask().execute(
                    new HttpRequest(ARG_PARAM_REQUEST_METHOD, false, requestParams), null);
            if(httpResponse != null){
                news.setFromHttp(httpResponse);
            }
        }
        if(rangeEnd != 0) {
            news.getNewsRange(rangeStart, rangeEnd);
        }
        ProviderService.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onResult(news);
            }
        });
    }

    @Override
    public void resetData() {
        news.removeAllNews();
    }
}
