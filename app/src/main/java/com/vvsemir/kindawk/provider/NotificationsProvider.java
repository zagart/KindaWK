package com.vvsemir.kindawk.provider;

import com.vvsemir.kindawk.provider.observer.IEvent;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

public class NotificationsProvider extends BaseProvider<Integer> {

    public NotificationsProvider(ICallback<Integer> callback) {
        super(callback);
    }

    @Override
    public void run() {
        @IEvent final Integer event = IEvent.NEW_POST;

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
}
