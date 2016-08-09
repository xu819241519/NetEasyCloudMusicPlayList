package com.landon.neteasycloudmusicplaylist.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 吐司相关显示
 * Created by landon.xu on 2016/8/9.
 */
public class ToastUtils {

    public static void show(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
