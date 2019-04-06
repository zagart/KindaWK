package com.vvsemir.kindawk.provider;

import java.util.ArrayList;
import java.util.List;

public class ProviderManager implements IProviderManager <IProvider> {
    public static final ProviderManager instance = new ProviderManager();

    private List<IProvider> providers = new ArrayList<>();

    private ProviderManager() {
    }
    /*
    public static synchronized ProviderManager getInstance(){
        if(instance == null) {
            instance = new ProviderManager();
        }
        return instance;
    }*/

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
