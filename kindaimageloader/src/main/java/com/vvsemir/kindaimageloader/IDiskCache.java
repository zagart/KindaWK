package com.vvsemir.kindaimageloader;

import android.support.annotation.WorkerThread;

public interface IDiskCache<K, V> {

    @WorkerThread
    boolean save(final K pKey, final V pValue);

    @WorkerThread
    V load(final K pKey);
}
