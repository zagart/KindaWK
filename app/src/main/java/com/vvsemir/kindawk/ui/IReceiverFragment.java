package com.vvsemir.kindawk.ui;

public interface IReceiverFragment <T>{
    void startLoadDataService();
    void updateViews(T data);
}
