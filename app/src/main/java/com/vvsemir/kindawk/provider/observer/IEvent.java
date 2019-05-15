package com.vvsemir.kindawk.provider.observer;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({IEvent.NONE, IEvent.NEW_POST})
@Retention(RetentionPolicy.SOURCE)
public @interface IEvent {
    int NONE = 0;
    int NEW_POST = 1;
}




