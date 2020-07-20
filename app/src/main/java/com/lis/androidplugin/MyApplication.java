package com.lis.androidplugin;

import android.app.Application;

import com.lis.androidplugin.utils.LoadUtil;

/**
 * Created by lis on 2020/7/20.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LoadUtil.loadClass(this);
    }
}
