package com.vvsemir.kindawk.ui;

public interface IReceiverFragment <T>{
    void loadData();
    void updateViews(T data);
    void onPostCreate();
}
