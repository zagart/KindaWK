package com.vvsemir.kindawk.provider;

import android.content.Context;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.service.RequestParams;
import com.vvsemir.kindawk.utils.ICallback;

public class NewsWallProvider implements IProvider <RequestParams, NewsWall> {
    //static final String ARG_PARAM_REQUEST_METHOD = "wall.get";
    static final String ARG_PARAM_REQUEST_METHOD = "newsfeed.get";
    public static final String ARG_PARAM_REQUEST_RANGE_START = "wall.get.range.start";
    public static final String ARG_PARAM_REQUEST_RANGE_END = "wall.get.range.end";

    private Context context;
    private NewsWall news = new NewsWall();
    private RequestParams requestParams;

    public NewsWallProvider() {
        requestParams = new RequestParams();
        //requestParams.put("owner_id", AuthManager.getCurrentToken().getUserId());
        //requestParams.put("count", 100);
        //requestParams.put("filter","all");
        requestParams.put("filters","post,photo,photo_tag,wall_photo");
    }


    @Override
    public NewsWall loadData(RequestParams request) {
        int rangeStart = 0, rangeEnd = 0;

        if(request.contains(ARG_PARAM_REQUEST_RANGE_START) && request.contains(ARG_PARAM_REQUEST_RANGE_END)){
            rangeStart = Integer.parseInt(request.getParam(ARG_PARAM_REQUEST_RANGE_START));
            rangeEnd = Integer.parseInt(request.getParam(ARG_PARAM_REQUEST_RANGE_END));
            request.removeParam(ARG_PARAM_REQUEST_RANGE_START);
            request.removeParam(ARG_PARAM_REQUEST_RANGE_END);
        }
        if (news.getCount() == 0) {
            HttpResponse httpResponse = new HttpRequestTask().execute(
                    new HttpRequest(ARG_PARAM_REQUEST_METHOD, false, requestParams), null);
            if(httpResponse != null){
                news.setFromHttp(httpResponse);
            }
        }
        if(rangeEnd != 0) {
            return news.getNewsRange(rangeStart, rangeEnd);
        }

        return news;
    }

    @Override
    public void resetData() {
        news.removeAllNews();
    }
}
