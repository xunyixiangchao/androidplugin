package com.lis.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by lis on 2020/7/21.
 */
abstract class BaseActivity extends Activity {
    Context mContext;
    boolean isPlugin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = new ContextThemeWrapper(getBaseContext(), 0);
        if (isPlugin) {
            Resources resource = PluginLoadUtil.getResource(getApplication());
            //替换这个context的resource为我们自己的resource
            Class<? extends Context> aClass = mContext.getClass();
            try {
                Field mResourceField = aClass.getDeclaredField("mResources");
                mResourceField.setAccessible(true);
                mResourceField.set(mContext, resource);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        View view = LayoutInflater.from(mContext).inflate(setLayoutId(), null);
        setContentView(view);
    }

    abstract int setLayoutId();

    //    @Override
//    public Resources getResources() {
//        if (getApplication() != null && getApplication().getResources() != null) {
//            //这个实际返回的是我们自己创建的resource
//            return getApplication().getResources();
//        }
//        return super.getResources();
//    }
}








