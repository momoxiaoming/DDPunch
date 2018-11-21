package com.al.autoleve;

import android.app.Application;

/**
 * Created by zhangxiaoming on 2018/11/2.
 */

public class AutoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        startService(new Intent(this, AutoService.class));
    }
}
