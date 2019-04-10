package com.vvsemir.kindawk.http;

import com.vvsemir.kindawk.service.ICallback;

public interface IHttpRequestTask <T extends HttpRequest, U extends HttpResponse, V> {
    U execute(final T request, ICallback<V> callbackOnResult);
}
