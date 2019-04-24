package com.vvsemir.kindawk.provider;

import com.vvsemir.kindawk.db.DbManager;
import com.vvsemir.kindawk.service.ProviderService;

import java.util.List;

public class DbCleanerProvider implements Runnable {
    private List<String> tablesToClean;

    public DbCleanerProvider(List<String> tables) {
        tablesToClean = tables;
    }

    @Override
    public void run() {
        DbManager dbManager = ProviderService.getInstance().getDbManager();

        for(String table : tablesToClean){
            dbManager.deleteAll(table);
        }
    }
}
