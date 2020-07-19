package com.lis.androidplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

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
                PathClassLoader dexClassLoader =new PathClassLoader("/data/data/com.lis.androidplugin/dexfile/test.dex",getClassLoader());
                try {
                    Class<?> aClass = dexClassLoader.loadClass("com.lis.plugin.Test");
                    Method print = aClass.getMethod("print");
                    print.invoke(null);
                } catch (Exception e) {


                }
            }
        });
    }
}
