package com.lis.androidplugin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


/**
 * 替插件的类做检测
 * Created by lis on 2020/7/20.
 */
public class ProxyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);
        Log.e("plugin", "onCreate: 我是代理的Activity");
    }
}
