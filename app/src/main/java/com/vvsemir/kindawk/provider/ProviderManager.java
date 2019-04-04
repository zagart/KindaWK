package com.vvsemir.kindawk.provider;

import java.util.List;

public class ProviderManager implements IProviderManager <IProvider> {
    private static ProviderManager instance = new ProviderManager();

    private List<IProvider> providers;

    private ProviderManager() {
    }

    public static ProviderManager getInstance(){
        return instance;
    }

    @Override
    public void createProvider(IProvider provider) {
        if(getProvider(provider.getClass()) != null){
            return;
        }

        providers.add(provider);
    }

    @Override
    public IProvider getProvider(Class providerClass) {
        for(IProvider provider : providers) {
            if( providerClass.isInstance(provider) ) {
                return provider;
            }
        }

        return null;
    }
}
