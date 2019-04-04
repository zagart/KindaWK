package com.vvsemir.kindawk.provider;

public interface IProviderManager<T> {
    void createProvider(T provider);
    T getProvider(Class providerClass);
}
