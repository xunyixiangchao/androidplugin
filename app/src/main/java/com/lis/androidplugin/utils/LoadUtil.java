package com.lis.androidplugin.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by lis on 2020/7/20.
 */
public class LoadUtil {
    private final static String apkPath = "/data/data/com.lis.androidplugin/dex/plugin-debug.apk";

    // private final static String apkPath="/sdcard/plugin.apk";//指向/storage/self/primary
    public static void loadClass(Context context) {
        try {
            Class<?> baseDexClassLoaderClass = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathList = baseDexClassLoaderClass.getDeclaredField("pathList");
            pathList.setAccessible(true);
            Class<?> dexPathList = Class.forName("dalvik.system.DexPathList");
            Field dexElements = dexPathList.getDeclaredField("dexElements");
            dexElements.setAccessible(true);

            /**
             * 插件
             */
            //创建插件的classloader类加载器，然后通过反射获取插件的elements
            //需要拿到DexPathList，需要拿到BaseDexClassLoader对象（创建或获取）
            //插件类加载器
            //null是so库路径
            DexClassLoader dexClassLoader = new DexClassLoader(apkPath,
                    context.getCacheDir().getAbsolutePath(), null, context.getClassLoader());

            Object pluginPathList = pathList.get(dexClassLoader);
            //拿到了插件的dexElements
            Object[] pluginDexElements = (Object[]) dexElements.get(pluginPathList);
            Log.e("LoadUtil", "pluginDexElements: " + pluginDexElements.length);
            /**
             * 宿主
             */
            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
            Object hostPathList = pathList.get(pathClassLoader);
            //拿到宿主的dexElements
            Object[] hostDexElements = (Object[]) dexElements.get(hostPathList);

            /**
             * 创建数组
             */
            Object[] elements = (Object[]) Array.newInstance(hostDexElements.getClass().getComponentType(), hostDexElements.length + pluginDexElements.length);
            System.arraycopy(hostDexElements, 0, elements, 0, hostDexElements.length);
            System.arraycopy(pluginDexElements, 0, elements, hostDexElements.length, pluginDexElements.length);


            dexElements.set(hostPathList, elements);
            Object[] hostDexElements2 = (Object[]) dexElements.get(hostPathList);
            Log.e("LoadUtil", "pluginDexElements: " + hostDexElements2.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
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








































































