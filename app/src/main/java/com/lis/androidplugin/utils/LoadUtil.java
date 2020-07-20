package com.lis.androidplugin.utils;

import android.content.Context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by lis on 2020/7/20.
 */
public class LoadUtil {
    private final static String apkPath = "/data/data/com.lis.androidplugin/dexfile/plugin-debug.apk";

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


            dexElements.set(hostPathList,elements);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}








































































