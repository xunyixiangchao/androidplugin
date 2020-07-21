package com.lis.plugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.Method;

/**
 * Created by lis on 2020/7/21.
 */
public class PluginLoadUtil {

    private final static String apkPath = "/data/data/com.lis.androidplugin/dex/plugin-debug.apk";

    private volatile static Resources mResources;

    public static Resources getResource(Context context) {
        if (mResources == null) {
            mResources = loadResource(context);
        }
        return mResources;
    }


    /**
     * 加载插件资源
     *
     * @param context
     * @return
     */
    public static Resources loadResource(Context context) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            //参数就是插件的资源路径
            addAssetPathMethod.invoke(assetManager, apkPath);
            Resources resources = context.getResources();
            return new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
