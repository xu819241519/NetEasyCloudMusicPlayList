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
import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;
import com.landon.neteasycloudmusicplaylist.constant.Constant;
import com.landon.neteasycloudmusicplaylist.database.CrawlerSQLHelper;
import com.landon.neteasycloudmusicplaylist.net.Crawl;
import com.landon.neteasycloudmusicplaylist.net.NetRequest;

import java.lang.ref.WeakReference;
import java.util.List;

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
public class MainActivity extends BaseActivity implements CrawlProgress{
    //下拉刷新控件
    @BindView(R.id.sr_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.gv_playlist)
    GridView gvPlayList;
    //适配器
    private PlayListAdapter gvAdapter;

    @Override
    public void crawlProgress(int total, int curPage, List<PlayListBean> datas) {
        LogUtils.d("xu","一共" + total + "页，正在抓取第" + curPage + "页");
        //完成爬取任务
        if(total == curPage){
            gvAdapter.update(datas);
            CrawlerSQLHelper crawlerSQLHelper = new CrawlerSQLHelper(this);
            crawlerSQLHelper.insert(datas);
        }
        //正在爬取中
        else{

        }
    }

    private static class MyHandler extends Handler{
        private WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity){
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if(activity != null) {
                if (Constant.ASYNC_QUERY == msg.what) {
                    activity.gvAdapter.update((List<PlayListBean>) msg.obj);
                }
            }

        }
    }

    private MyHandler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        gvAdapter = new PlayListAdapter(this);
        gvPlayList.setAdapter(gvAdapter);
        initDataBase();
    }

    /**
     * 初始化数据库
     */
    private void initDataBase() {
        CrawlerSQLHelper crawlerSQLHelper = new CrawlerSQLHelper(this);
//        crawlerSQLHelper.queryAsync(handler);
        List<PlayListBean> beans = crawlerSQLHelper.query(0,30);
        gvAdapter.update(beans);
    }

}
