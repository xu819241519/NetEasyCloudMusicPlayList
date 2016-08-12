package com.landon.neteasycloudmusicplaylist.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.landon.neteasycloudmusicplaylist.R;
import com.landon.neteasycloudmusicplaylist.Utils.LogUtils;
import com.landon.neteasycloudmusicplaylist.adapter.PlayListAdapter;
import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;
import com.landon.neteasycloudmusicplaylist.constant.Constant;
import com.landon.neteasycloudmusicplaylist.database.CrawlerSQLHelper;
import com.landon.neteasycloudmusicplaylist.net.Crawl;

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

    @BindView(R.id.rv_playlist)
    RecyclerView rvPlayList;
    //适配器
    private PlayListAdapter rvAdapter;

    private ProgressDialog progressDialog;

    private int curPage = 0;

    private int pageSize = 30;

    @Override
    public void crawlProgress(int total, int curPage, List<PlayListBean> datas) {
        LogUtils.d("xu","一共" + total + "页，正在抓取第" + curPage + "页");
        //完成爬取任务
        if(total == curPage){
            rvAdapter.update(datas);
            CrawlerSQLHelper crawlerSQLHelper = new CrawlerSQLHelper(this);
            crawlerSQLHelper.insert(datas);
            if(progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
        //正在爬取中
        else{
            if(progressDialog != null){
                progressDialog.setProgress(curPage * 100 / total);
                progressDialog.setMessage("正在爬取第" + curPage + "页，共有" + total + "页");
            }
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
                    activity.rvAdapter.update((List<PlayListBean>) msg.obj);
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
        rvAdapter = new PlayListAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPlayList.setLayoutManager(gridLayoutManager);
        rvPlayList.setAdapter(rvAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                curPage ++;
                queryDataBase();
            }
        });
        queryDataBase();
        //rvPlayList.addItemDecoration(new RecycleViewDivider(this,LinearLayoutManager.HORIZONTAL));
        //crawl();
    }

    /**
     * 查询数据库
     */
    private void queryDataBase() {
        CrawlerSQLHelper crawlerSQLHelper = new CrawlerSQLHelper(this);
        List<PlayListBean> beans = crawlerSQLHelper.query(curPage,pageSize);
        rvAdapter.update(beans);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void crawl(){
        Crawl crawl = new Crawl(this,this);
        crawl.beginCrawl();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在抓取数据...");
        progressDialog.show();
//        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//
//            }
//        });
    }

}
