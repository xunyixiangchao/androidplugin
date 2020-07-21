package com.lis.androidplugin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.
                // DexClassLoader dexClassLoader = new DexClassLoader("/data/data/com.lis.androidplugin/dexfile/test.dex",
                //         MainActivity.this.getCacheDir().getAbsolutePath(), null, getClassLoader());
                //2
//                PathClassLoader dexClassLoader =new PathClassLoader("/data/data/com.lis.androidplugin/dexfile/test.dex",getClassLoader());
//                try {
////                  1.  Class<?> aClass = getClassLoader().loadClass("com.lis.plugin.Test");
//                    //2.
//                    Class<?> aClass = Class.forName("com.lis.plugin.Test");
//                    Method print = aClass.getMethod("print");
//                    print.invoke(null);
//                } catch (Exception e) {
//                }
                //启动插件的类
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.lis.plugin",
                        "com.lis.plugin.MainActivity"));
                startActivity(intent);

            }
        });
    }
}






















