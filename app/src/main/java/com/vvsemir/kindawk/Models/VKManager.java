package com.vvsemir.kindawk.Models;

import android.content.Context;

import static com.vvsemir.kindawk.Models.Constants.*;

public class VKManager {
    private static VKManager instance = new VKManager();

    private VKManager() {
    }

    public static VKManager getInstance(){

        return instance;
    }


}
