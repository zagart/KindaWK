package com.vvsemir.kindawk.provider;

import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.provider.gson.GsonHelper;
import com.vvsemir.kindawk.provider.observer.IEvent;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

import java.util.Calendar;
import java.util.List;

import static com.vvsemir.kindawk.provider.NewsWallProvider.ARG_PARAM_REQUEST_FIELDS;
import static com.vvsemir.kindawk.provider.NewsWallProvider.ARG_PARAM_REQUEST_FILTERS;
import static com.vvsemir.kindawk.provider.NewsWallProvider.ARG_PARAM_REQUEST_METHOD;

public class NotificationsProvider extends BaseProvider<Integer> {
    public static final int NOTIFIER_PREFS_MINUTES = 5;

    public NotificationsProvider(ICallback<Integer> callback) {
        super(callback);
    }

    @Override
    public void run() {
        @IEvent final Integer event;

        int newsCount = loadApiData();

        if(newsCount == 0) {
            event = IEvent.NONE;
        } else {
            event = IEvent.NEW_POST;
        }

        ProviderService.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onResult(event);
            }
        });
    }

    @Override
    public void setRequestParams(RequestParams request) {
        super.setRequestParams(request);
    }

    int loadApiData() {
        int newsCount = 0;

        try {
            RequestParams requestParams = new RequestParams();
            requestParams.put("owner_id", AuthManager.getCurrentToken().getUserId());
            requestParams.put("count", 1);
            requestParams.put("max_photos",1);
            requestParams.put("filters",ARG_PARAM_REQUEST_FILTERS);
            requestParams.put("fields",ARG_PARAM_REQUEST_FIELDS);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, (-1) *  NOTIFIER_PREFS_MINUTES);
            requestParams.put("start_time", calendar.getTimeInMillis()/1000);

            HttpResponse httpResponse = new HttpRequestTask().execute(
                    new HttpRequest(ARG_PARAM_REQUEST_METHOD, false, requestParams), null);

            if (httpResponse != null) {
                newsCount = new GsonHelper().getNewsWallCount(httpResponse.getResponseAsString());
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            return newsCount;
        }
    }
}
