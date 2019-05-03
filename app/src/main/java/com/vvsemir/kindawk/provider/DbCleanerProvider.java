package com.vvsemir.kindawk.provider;

import com.vvsemir.kindawk.db.DbManager;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;

import java.util.List;

public class DbCleanerProvider implements Runnable {
    private List<String> tablesToClean;
    ICallback<Integer> callback;

    public DbCleanerProvider(List<String> tables, final ICallback<Integer> callback) {
        tablesToClean = tables;
        this.callback = callback;
    }

    @Override
    public void run() {
        DbManager dbManager = ProviderService.getInstance().getDbManager();

        for(String table : tablesToClean){
            dbManager.deleteAll(table);
        }

        ProviderService.getInstance().getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onResult(0);
            }
        });
    }
}
