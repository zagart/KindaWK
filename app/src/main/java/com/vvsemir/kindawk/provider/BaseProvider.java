package com.vvsemir.kindawk.provider;

import android.os.Parcelable;

import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.RequestParams;

public abstract class BaseProvider<T> implements IProvider<RequestParams>, Runnable {
    ICallback<T> callback;
    RequestParams requestParams;

    public BaseProvider() {
    }

    public BaseProvider(ICallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void setRequestParams(RequestParams request) {
        requestParams = request;
    }
}
