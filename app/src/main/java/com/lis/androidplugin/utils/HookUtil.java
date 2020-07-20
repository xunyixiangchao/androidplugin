package com.lis.androidplugin.utils;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by lis on 2020/7/20.
 */
public class HookUtil {
    private static String TARGET_INTENT = "TARGET_INTENT";

    public static void hookAMS() {
        try {
            Object singleton = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //获取Singleton对象
                Class<?> aClass = Class.forName("android.app.ActivityManager");
                Field singletonField = aClass.getDeclaredField("IActivityManagerSingleton");
                singletonField.setAccessible(true);
                singleton = singletonField.get(null);
            } else {
                Class<?> aClass = Class.forName("android.app.ActivityManagerNative");
                Field singletonField = aClass.getDeclaredField("gDefault");
                singletonField.setAccessible(true);
                singleton = singletonField.get(null);
            }
            //获取 IActivityManager对象
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            final Object mInstance = mInstanceField.get(singleton);

            Class<?> iActivityManagerClass = Class.forName("android.app.IActivityManager");

            Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{iActivityManagerClass},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            //IActivityManager的方法执行的时候，都会先跑这儿
                            //替换Intent
                            if ("startActivity".equals(method.getName())) {
                                //替换Intent
                                int index = 0;
                                for (int i = 0; i < args.length; i++) {
                                    if (args[i] instanceof Intent) {
                                        index = i;
                                        break;
                                    }
                                }
                                //启动插件的Intent
                                Intent intent = (Intent) args[index];
                                Intent proxyIntent = new Intent();
                                proxyIntent.setClassName("com.lis.androidplugin",
                                        "com.lis.androidplugin.ProxyActivity");
                                //保存原来的
                                proxyIntent.putExtra(TARGET_INTENT, intent);
                                args[index] = proxyIntent;
                            }
                            //原来的方法
                            return method.invoke(mInstance, args);
                        }
                    });
            //替换系统的IActivityManager对象
            mInstanceField.set(singleton, proxyInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookHandler() {
        try {

            final Class<?> aClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = aClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            final Object activityThread = sCurrentActivityThreadField.get(null);

            Field mHField = aClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Object mH = mHField.get(activityThread);

            //new一个Callback 替换系统Handler的Callback
            Class<?> handlerClass = Class.forName("android.os.Handler");
            Field mCallbackField = handlerClass.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);

            //

            mCallbackField.set(mH, new Handler.Callback() {

                @Override
                public boolean handleMessage(Message msg) {
                    //将Intent替换回来
                    switch (msg.what) {
                        case 100:
                            //获取ActivityClientRecord中的intent对象
                            try {
                                Field intentField = msg.obj.getClass().getDeclaredField("intent");
                                intentField.setAccessible(true);
                                Intent proxyIntent = (Intent) intentField.get(msg.obj);
                                //拿到插件的Intent
                                Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                                if (intent != null) {
                                    Log.e("handler", intent.getComponent().getPackageName());
                                    Log.e("handler", intent.getComponent().getClassName());
                                    //替换回来
                                    //proxyIntent.setComponent(intent.getComponent());
                                    //2替换
                                    intentField.set(msg.obj, intent);
                                }
                            } catch (Exception e) {
                                Log.e("handler", e.getMessage());
                                e.printStackTrace();
                            }
                            break;
                        case 159:
                            try {
                                Class<?> clientTransaction = Class.forName("android.app.servertransaction.ClientTransaction");
                                Field mActivityCallbacksField = clientTransaction.getDeclaredField("mActivityCallbacks");
                                mActivityCallbacksField.setAccessible(true);
                                List activityCallbacks = (List) mActivityCallbacksField.get(msg.obj);
                                for (int i = 0; i < activityCallbacks.size(); i++) {
                                    if (activityCallbacks.get(i).getClass().getName()
                                            .equals("android.app.servertransaction.LaunchActivityItem")) {
                                        Object launchItem = activityCallbacks.get(i);
                                        Field mIntentField = launchItem.getClass().getDeclaredField("mIntent");
                                        mIntentField.setAccessible(true);
                                        Intent proxyIntent = (Intent) mIntentField.get(launchItem);
                                        //拿到插件的Intent
                                        Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                                        if (intent != null) {
                                            Log.e("handler", intent.getComponent().getPackageName());
                                            Log.e("handler", intent.getComponent().getClassName());
                                            //替换回来
                                            //proxyIntent.setComponent(intent.getComponent());
                                            //2替换
                                            mIntentField.set(launchItem, intent);
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    //不能返回true，否则下面的逻辑不再执行
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
