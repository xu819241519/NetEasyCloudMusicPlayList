package com.landon.neteasycloudmusicplaylist.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.landon.neteasycloudmusicplaylist.R;
import com.landon.neteasycloudmusicplaylist.Utils.LogUtils;
import com.landon.neteasycloudmusicplaylist.adapter.PlayListAdapter;
import com.landon.neteasycloudmusicplaylist.constant.Constant;
import com.landon.neteasycloudmusicplaylist.net.NetRequest;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页面
 *
 *
 //glide、volley集成
 //dagger
 swiperefresh、recyclerview support包中
 */
public class MainActivity extends BaseActivity {
    //下拉刷新控件
    @BindView(R.id.sr_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.gv_playlist)
    GridView gvPlayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        PlayListAdapter gvAdapter = new PlayListAdapter();
        gvPlayList.setAdapter(gvAdapter);
    }

    /**
     * 初始化数据库
     */
    private void initDataBase() {

    }

}
