package com.vvsemir.kindawk.http;

import com.vvsemir.kindawk.utils.ICallback;

public interface IHttpRequestTask <T, U> {
    void execute(final T request, final ICallback<U> callbackOnResult);
}
