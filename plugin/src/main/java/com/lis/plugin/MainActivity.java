package com.lis.plugin;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends BaseActivity {
    @Override
    int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("plugin", "onCreate: plugin Main");

    }


}
