package com.bobomee.android.rxjavaexample;

import com.bobomee.android.common.app.BaseApplication;
import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.Logger;

/**
 * Created by bobomee on 16/4/17.
 */
public class RxApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger
                .init("_RX_JAVA_")                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
//                .hideThreadInfo()               // default shown
//                .logLevel(LogLevel.NONE)        // default LogLevel.FULL
                .methodOffset(2)                // default 0
                .logTool(new AndroidLogTool()); // custom log tool, optional
    }
}
