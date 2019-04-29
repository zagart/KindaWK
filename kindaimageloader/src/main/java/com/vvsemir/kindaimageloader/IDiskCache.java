package com.vvsemir.kindaimageloader;

import android.support.annotation.WorkerThread;

public interface IDiskCache<K, V, U> {

    @WorkerThread
    boolean save(final K key, final U data);

    @WorkerThread
    V load(final K key);
}
