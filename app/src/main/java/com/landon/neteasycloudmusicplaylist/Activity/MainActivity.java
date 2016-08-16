package com.landon.neteasycloudmusicplaylist.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.landon.neteasycloudmusicplaylist.R;
import com.landon.neteasycloudmusicplaylist.Utils.LogUtils;
import com.landon.neteasycloudmusicplaylist.adapter.PlayListAdapter;
import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;
import com.landon.neteasycloudmusicplaylist.constant.Constant;
import com.landon.neteasycloudmusicplaylist.database.CrawlerSQLHelper;
import com.landon.neteasycloudmusicplaylist.net.Crawl;
import com.squareup.haha.perflib.Main;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页面
 * <p/>
 * <p/>
 * //glide、volley集成
 * //dagger
 * swiperefresh、recyclerview support包中
 * 爬取网络的时候界面卡
 */
public class MainActivity extends BaseActivity implements CrawlProgress {

    @BindView(R.id.rv_playlist)
    LRecyclerView rvPlayList;
    //适配器
    private PlayListAdapter rvAdapter;
    //适配器
    private LRecyclerViewAdapter lrvAdapter;

    private ProgressDialog progressDialog;
    //当前第几页
    private int curPage = 0;
    //每一页的数量
    private int pageSize = 30;
    //是否还有数据
    private boolean hasData = true;

    private static final int PROGRESS_UPDATE = 0;

    private static final int PROGRESS_COMPLETE = 1;
    //默认是收藏数排序
    private int sortType = Constant.SORT_COLLECT_COUNT;

    @Override
    public void crawlProgress(int total, int curPage, List<PlayListBean> datas) {
        LogUtils.d("xu", "一共" + total + "页，正在抓取第" + curPage + "页");
        //完成爬取任务
        if (total == curPage) {
            Message message = handler.obtainMessage(PROGRESS_COMPLETE);
            message.sendToTarget();
            CrawlerSQLHelper crawlerSQLHelper = new CrawlerSQLHelper(this);
            crawlerSQLHelper.insert(datas);
        }
        //正在爬取中
        else {
            Message msg = handler.obtainMessage(PROGRESS_UPDATE);
            msg.arg1 = total;
            msg.arg2 = curPage;
            msg.sendToTarget();
        }
    }

    private static class MainHandler extends Handler{
        private WeakReference<MainActivity> mActivity;
        public MainHandler(MainActivity activity){
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if(activity != null){
                switch (msg.what){
                    case PROGRESS_UPDATE:
                        if (activity.progressDialog != null) {
                            activity.progressDialog.setProgress((int)msg.arg2 * 100 / (int)msg.arg1);
                            activity.progressDialog.setMessage("正在爬取第" + (int)msg.arg2 + "页，共有" + (int)msg.arg1 + "页");
                        }
                        break;
                    case PROGRESS_COMPLETE:
                        if (activity.progressDialog != null) {
                            activity.progressDialog.dismiss();
                            activity.progressDialog = null;
                        }
                        activity.queryDataBase(false);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private MainHandler handler = new MainHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
        initScheme();
    }

    private void initScheme() {
//        Uri uri = Uri.parse("orpheus://playlist/401324837");
//        Intent intent  = new Intent(Intent.ACTION_VIEW,uri);
//        startActivity(intent);
    }

    private void initViews() {
        rvAdapter = new PlayListAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        lrvAdapter = new LRecyclerViewAdapter(this, rvAdapter);
        rvPlayList.setLayoutManager(gridLayoutManager);
        rvPlayList.setAdapter(lrvAdapter);
        rvPlayList.setLScrollListener(new LRecyclerView.LScrollListener() {
            @Override
            public void onRefresh() {
                curPage = 0;
                queryDataBase(true);
            }

            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onBottom() {
                if (hasData) {
                    curPage++;
                    queryDataBase(false);
                }
            }

            @Override
            public void onScrolled(int distanceX, int distanceY) {

            }
        });
        queryDataBase(false);
        //rvPlayList.addItemDecoration(new RecycleViewDivider(this,LinearLayoutManager.HORIZONTAL));
        //crawl();
    }

    /**
     * 查询数据库
     *
     * @param clear 是否清除以前的数据
     */
    private void queryDataBase(boolean clear) {
        CrawlerSQLHelper crawlerSQLHelper = new CrawlerSQLHelper(this);
        if (clear) {
            rvAdapter.clearData();
        }
        List<PlayListBean> beans = crawlerSQLHelper.query(curPage, pageSize, sortType);
        if (beans != null && beans.size() == pageSize) {
            rvAdapter.addData(beans,sortType);
            rvPlayList.refreshComplete();
            lrvAdapter.notifyDataSetChanged();
            hasData = true;
        } else hasData = false;
    }

    private void crawl() {
        Crawl crawl = new Crawl(this, this);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                downLoadUpdate();
                break;
            case R.id.action_setting:
                showPopupMenu();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //从网络上获取数据
    private void downLoadUpdate() {
        new AlertDialog.Builder(this).setMessage("从网络获取最新数据，如果用的是流量，可能会花费几M的，是否获取？").setPositiveButton("获取", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                crawl();
                dialog.dismiss();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }


    //显示popmenu
    private void showPopupMenu() {
        final PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.action_setting));
        getMenuInflater().inflate(R.menu.popmenu, popupMenu.getMenu());
        if(sortType == Constant.SORT_COLLECT_COUNT)
            popupMenu.getMenu().findItem(R.id.sort_collect).setCheckable(true).setChecked(true);
        else if(sortType == Constant.SORT_PLAY_COUNT)
            popupMenu.getMenu().findItem(R.id.sort_play).setCheckable(true).setChecked(true);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.sort_collect:
                        popupMenu.getMenu().findItem(R.id.sort_collect).setCheckable(true).setChecked(true);
                        if(sortType != Constant.SORT_COLLECT_COUNT){
                            sortType = Constant.SORT_COLLECT_COUNT;
                            queryDataBase(true);
                        }
                        break;
                    case R.id.sort_play:
                        popupMenu.getMenu().findItem(R.id.sort_play).setCheckable(true).setChecked(true);
                        if(sortType != Constant.SORT_PLAY_COUNT){
                            sortType = Constant.SORT_PLAY_COUNT;
                            queryDataBase(true);
                        }
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
