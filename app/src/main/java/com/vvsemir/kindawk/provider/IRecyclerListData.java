package com.vvsemir.kindawk.provider;

public interface IRecyclerListData<T>{
    T getItem(int index);
    int getCount();
}
