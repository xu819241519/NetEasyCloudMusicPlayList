package com.landon.neteasycloudmusicplaylist.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.landon.neteasycloudmusicplaylist.activity.IView;
import com.landon.neteasycloudmusicplaylist.activity.MainActivity;
import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;
import com.landon.neteasycloudmusicplaylist.constant.Constant;
import com.landon.neteasycloudmusicplaylist.database.CrawlerSQLHelper;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by landon on 2017/1/5.
 */

public class MainPresenter {

    //当前第几页
    private int curPage = 0;
    //每一页的数量
    private int pageSize = 30;

    //默认是收藏数排序
    private int sortType = Constant.SORT_COLLECT_COUNT;

    private IView mView;

    private Context mContext;

    //请求读写权限
    private static int WRITE_EXTERNAL_PERMISSION = 0;

    public MainPresenter(Context context, IView view) {
        this.mView = view;
        this.mContext = context;
    }


    /**
     * 查询数据库
     */
    public void loadData() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            performLoadData();
        } else {
            ActivityCompat.requestPermissions((MainActivity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION);
        }
    }

    private void performLoadData() {

        Observable.create(new Observable.OnSubscribe<List<PlayListBean>>() {
            @Override
            public void call(Subscriber<? super List<PlayListBean>> subscriber) {
                CrawlerSQLHelper crawlerSQLHelper = new CrawlerSQLHelper(mContext);
                List<PlayListBean> beans = crawlerSQLHelper.query(curPage, pageSize, sortType);
                subscriber.onNext(beans);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Subscriber<List<PlayListBean>>() {
                            @Override
                            public void onCompleted() {
                                Log.d("onCompleted", "onCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("onError", e.getMessage());
                            }

                            @Override
                            public void onNext(List<PlayListBean> playListBeen) {
                                if (mView != null) {
                                    mView.addList(playListBeen);
                                }
                            }
                        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadData();
            }
        }
    }

    public void setSortType(int sortType) {
        if (this.sortType != sortType) {
            this.sortType = sortType;
            refresh();
        }
    }

    public int getSortType() {
        return this.sortType;
    }

    public void refresh() {
        curPage = 0;
        mView.clearList();
        loadData();
    }

    public void loadNextPage() {
        curPage++;
        loadData();
    }

    public void updateDataFromNet() {

    }


    //通过scheme启动网页云音乐
    public void postScheme(int playlistID) {
        Uri uri = Uri.parse(Constant.SCHEME + playlistID);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(intent);
    }
}
