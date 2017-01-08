package com.landon.neteasycloudmusicplaylist.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.volley.Request;
import com.landon.neteasycloudmusicplaylist.Utils.LogUtils;
import com.landon.neteasycloudmusicplaylist.activity.CrawlProgress;
import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;
import com.landon.neteasycloudmusicplaylist.constant.Constant;
import com.landon.neteasycloudmusicplaylist.parser.HTMLParser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫开始类
 * Created by landon.xu on 2016/8/9.
 */
public class Crawl {

    //上下文对象
    private Context context;
    //爬虫进度通知
    private CrawlProgress crawlProgress;
    //获取总页数
    private static final int GET_PAGE_COUNT = 0;
    //爬取数据
    private static final int CRAWL_PAGE = 1;
    //获取歌单信息
    private static final int CRAWL_PLAYLIST_ALL = 2;
    //获取歌单信息
    private static final int CRAWL_PLAYLIST_PART = 3;


    //总页数
    private int totalPage = 0;
    //已经爬取的页数
    private int curPage = 0;
    //爬去失败的页数
    private int failedPage = 0;
    //已经获取的歌单数
    private int curPlayList = 0;
    //爬去失败的歌单数
    private int failedPlayList = 0;
    //爬取的数据
    private List<PlayListBean> playListData;

    private List<String> mPlayListUrls;

    private static final String UPDATE_ALL_TAG = "update_all";

    private static final String UPDATE_PARTLY_TAG = "update_partly";

    //计算页数的handler
    private static class crawlHandler extends Handler {
        private WeakReference<Crawl> mCrawl;

        public crawlHandler(Crawl crawl) {
            mCrawl = new WeakReference<Crawl>(crawl);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Crawl crawl = mCrawl.get();
            if (crawl != null) {
                NetResult result = (NetResult) msg.obj;
                switch (msg.what) {
                    case GET_PAGE_COUNT:
                        if (result.getStatus() == NetResult.STATUS_SUCCESS) {
                            crawl.totalPage = HTMLParser.getTotalPage(result.getMsg());
                            for (int i = 0; i < crawl.totalPage; ++i) {
                                crawl.crawlPage(i);
                            }
                        } else {
                            LogUtils.d("landon", "获取页数错误：" + result.getMsg());
                        }
                        break;
                    case CRAWL_PAGE:
                        crawl.curPage++;
                        if (result.getStatus() == NetResult.STATUS_SUCCESS) {
                            List<String> playlisturls = HTMLParser.getPlayListURL(result.getMsg());
                            if(playlisturls != null && playlisturls.size() > 0){
                                if(crawl.mPlayListUrls == null){
                                    crawl.mPlayListUrls = new ArrayList<>();
                                }
                                crawl.mPlayListUrls.addAll(playlisturls);
                                for(String url : playlisturls){
                                    crawl.crawlPlayList(url);
                                }
                            }
                        } else {
                            crawl.failedPage++;
                            LogUtils.d("landon", "获取第" + (crawl.curPage ) + "页错误：" + result.getMsg());
                        }
                        break;
                    case CRAWL_PLAYLIST_ALL:
                        crawl.curPlayList++;
                        if (result.getStatus() == NetResult.STATUS_SUCCESS) {
                            PlayListBean bean = HTMLParser.getPlayListBean(result.getMsg());
                            if(bean != null){
                                crawl.playListData.add(bean);
                            }
//                            LogUtils.d("xu", "抓取第" + crawl.curPlayList + "个歌单，共" + crawl.totalPlayList + "个歌单");
//                            for (int i = 0; i < crawl.totalPlayList; ++i) {
//                                if (crawlPage.playListData.get(i).getId() == result.getId()) {
//                                    HTMLParser.initCollectInfo(crawlPage.playListData.get(i), result.getMsg());
//                                    break;
//                                }
//                            }
//                            crawl.crawlProgress.crawlPlayListProgress(crawl.totalPlayList, crawl.curPlayList, crawl.failedPlayList, crawl.playListData);
                        } else {
                            crawl.failedPlayList++;
                            LogUtils.d("landon", "获取第" + (crawl.curPlayList + crawl.failedPlayList) + "个歌单错误：" + result.getMsg());
                        }
                        break;

                    case CRAWL_PLAYLIST_PART:
                        if (result.getStatus() == NetResult.STATUS_SUCCESS) {
//                            LogUtils.d("xu", "抓取第" + crawl.curPlayList + "个歌单，共" + crawl.totalPlayList + "个歌单");
//                            for (int i = 0; i < crawl.totalPlayList; ++i) {
////                                if (crawlPage.playListData.get(i).getId() == result.getId()) {
////                                    HTMLParser.initCollectInfo(crawlPage.playListData.get(i), result.getMsg());
////                                    break;
////                                }
//                            }
                        } else {
                            LogUtils.d("landon", "获取第" + (crawl.curPlayList) + "个歌单错误：" + result.getMsg());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private crawlHandler cHandler = new crawlHandler(this);

    public Crawl(Context context, CrawlProgress crawlProgress) {
        this.context = context;
        this.crawlProgress = crawlProgress;
    }


    /**
     * 开启爬虫
     */
    public void beginCrawl() {
        //首先获取总共需要爬去的页数
        NetRequest netRequest = NetRequest.getInstance(context);
        Message msg = cHandler.obtainMessage(GET_PAGE_COUNT);
        netRequest.request(Constant.getURL(0), msg, Request.Priority.HIGH, UPDATE_ALL_TAG);
        playListData = new ArrayList<>();
        mPlayListUrls= new ArrayList<>();
        curPage = 0;
        totalPage = 0;
        curPlayList = 0;
        failedPage = 0;
        failedPlayList = 0;
    }

    private void crawlPage(int page) {
        String url = Constant.getURL(page);
        NetRequest netRequest = NetRequest.getInstance(context);
        Message message = cHandler.obtainMessage(CRAWL_PAGE);
        netRequest.request(url, message , Request.Priority.HIGH, UPDATE_ALL_TAG);
    }

    private void crawlPlayList() {
        NetRequest netRequest = NetRequest.getInstance(context);
        for (PlayListBean bean : playListData) {
            Message message = cHandler.obtainMessage(CRAWL_PLAYLIST_ALL);
            netRequest.request(bean.getUrl(), message, Request.Priority.NORMAL, UPDATE_ALL_TAG);
        }
    }

    public void crawlPlayList(String url) {
        if (!TextUtils.isEmpty(url)) {
            NetRequest netRequest = NetRequest.getInstance(context);
            Message message =cHandler.obtainMessage(CRAWL_PLAYLIST_PART);
            netRequest.request(url, message, Request.Priority.LOW,UPDATE_PARTLY_TAG);
        }
    }
}
