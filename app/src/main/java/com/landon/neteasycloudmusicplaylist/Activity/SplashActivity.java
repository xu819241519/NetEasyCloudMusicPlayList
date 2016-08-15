package com.landon.neteasycloudmusicplaylist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.landon.neteasycloudmusicplaylist.R;

import java.lang.ref.WeakReference;

/**
 * 引导页
 */
public class SplashActivity extends Activity {

    private MyHandler handler;

    private static class MyHandler extends Handler{

        private WeakReference<SplashActivity> mActivity;

        public MyHandler(SplashActivity activity){
            mActivity = new WeakReference<SplashActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivity activity = mActivity.get();
            if(activity != null){
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash();
    }

    //等待3秒再跳转主页
    private void splash() {
        handler = new MyHandler(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                msg.sendToTarget();
            }
        },3000);
    }
}
