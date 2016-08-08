package com.landon.neteasycloudmusicplaylist;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by landon.xu on 2016/8/8.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CrashReport.initCrashReport(getApplicationContext(),"719ff75327",true);

        LeakCanary.install(this);

    }
}
