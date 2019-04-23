package com.vvsemir.kindaimageloader;

public interface ILoaderCallback<T> {
    void onResult(final T result);
    void onError(Throwable throwable);
}
