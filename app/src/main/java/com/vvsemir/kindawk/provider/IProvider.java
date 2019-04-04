package com.vvsemir.kindawk.provider;

import com.vvsemir.kindawk.utils.ICallback;

public interface IProvider<T, U> {
    U loadData(final T request);
    void resetData();
}
