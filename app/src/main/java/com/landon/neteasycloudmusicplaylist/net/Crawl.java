package com.landon.neteasycloudmusicplaylist.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

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
    private static int GET_PAGE_COUNT = 0;
    //爬取数据
    private static int CRAWL_PAGE = 1;
    //获取收藏信息
    private static int CRAWL_COLLECT = 2;


    //总页数
    private int totalPage = 0;
    //总歌单数
    private int totalPlayList = 0;
    //已经爬取的页数
    private int curPage = 0;
    //已经获取的歌单数
    private int curPlyaList = 0;
    //爬取的数据
    private List<PlayListBean> playListData;

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
                if (result != null) {
                    if (result.getType() == GET_PAGE_COUNT) {
                        crawl.totalPage = HTMLParser.getTotalPage(result.getMsg());
                        for (int i = 0; i < crawl.totalPage; ++i) {
                            crawl.crawl(i);
                        }
                    } else if (result.getType() == CRAWL_PAGE) {
                        List<PlayListBean> beans = HTMLParser.getPlayList(result.getMsg());
                        if (beans != null) {
                            crawl.playListData.addAll(beans);
                            crawl.curPage++;
                            if (crawl.curPage == crawl.totalPage) {
                                crawl.totalPlayList = crawl.playListData.size();
                                crawl.crawlCollectInfo();
                            }
                            LogUtils.d("xu", "抓取第" + crawl.curPage + "页");
                            crawl.crawlProgress.crawlPageProgress(crawl.totalPage, crawl.curPage);
                        }
                    } else if (result.getType() == CRAWL_COLLECT) {
                        LogUtils.d("xu", "抓取第" + crawl.curPlyaList + "个歌单，共" + crawl.totalPlayList + "个歌单");
                        crawl.curPlyaList++;
                        for (int i = 0; i < crawl.totalPlayList; ++i) {
                            if (crawl.playListData.get(i).getId() == result.getId()) {
                                HTMLParser.initCollectInfo(crawl.playListData.get(i), result.getMsg());
                                break;
                            }
                        }
                        crawl.crawlProgress.crawlPlayListProgress(crawl.totalPlayList, crawl.curPlyaList, crawl.playListData);

                    }
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
        netRequest.request(Constant.getURL(0), cHandler, GET_PAGE_COUNT, -1);
        playListData = new ArrayList<>();
        curPage = 0;
        totalPage = 0;
        curPlyaList = 0;
        totalPlayList = 0;
    }

    private void crawl(int page) {
        String url = Constant.getURL(page);
        NetRequest netRequest = NetRequest.getInstance(context);
        netRequest.request(url, cHandler, CRAWL_PAGE, -1);
    }

    private void crawlCollectInfo() {
        for (PlayListBean bean : playListData) {
            NetRequest netRequest = NetRequest.getInstance(context);
            netRequest.request(bean.getUrl(), cHandler, CRAWL_COLLECT, bean.getId());
        }
    }
}
