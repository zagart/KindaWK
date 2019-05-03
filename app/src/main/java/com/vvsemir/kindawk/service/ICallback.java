package com.vvsemir.kindawk.service;

public interface ICallback <T> {
    void onResult(final T result);
    void onError(Throwable throwable);
}
