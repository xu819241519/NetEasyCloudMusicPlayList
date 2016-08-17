package com.landon.neteasycloudmusicplaylist.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.landon.neteasycloudmusicplaylist.R;
import com.landon.neteasycloudmusicplaylist.Utils.FileUtils;
import com.landon.neteasycloudmusicplaylist.constant.Constant;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * 引导页
 */
public class SplashActivity extends Activity {

    //请求读写权限
    private static int WRITE_EXTERNAL_PERMISSION = 0;

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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            moveDB();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_EXTERNAL_PERMISSION);
        }
    }

    private void moveDB(){
        try {
            Constant.existDBFile = FileUtils.CopySqliteFileFromRawToDatabases(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        },2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == WRITE_EXTERNAL_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                moveDB();
            }else{
                splash();
            }

        }
    }
}
