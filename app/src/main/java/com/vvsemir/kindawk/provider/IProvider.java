package com.vvsemir.kindawk.provider;

public interface IProvider<T> {
    void setRequestParams(final T request);
}
