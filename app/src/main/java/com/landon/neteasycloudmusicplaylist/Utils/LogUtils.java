package com.landon.neteasycloudmusicplaylist.Utils;

import android.util.Log;

/**
 * 打印相关
 * Created by landon.xu on 2016/8/9.
 */
public class LogUtils {
    private static boolean enableLog = true;

    public static void d(String tag, String msg){
        if(enableLog && msg != null && !msg.equals("")){
            Log.d(tag,msg);
        }
    }

    public static void e(String tag, String msg){
        if(enableLog && msg != null && !msg.equals("")){
            Log.e(tag,msg);
        }
    }
}
