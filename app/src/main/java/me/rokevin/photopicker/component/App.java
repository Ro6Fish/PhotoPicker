package me.rokevin.photopicker.component;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import me.rokevin.lib.photopicker.util.SDUtil;
import me.rokevin.photopicker.component.activity.SDCardUtil;

/**
 * Created by luokaiwen on 16/8/12.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化Fresco
        Fresco.initialize(this);

        SDCardUtil.initSDCardDir();
    }
}
