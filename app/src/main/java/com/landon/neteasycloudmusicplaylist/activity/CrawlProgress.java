package com.landon.neteasycloudmusicplaylist.activity;

import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;

import java.util.List;

/**
 * 爬取进度接口
 * Created by landon.xu on 2016/8/9.
 */
public interface CrawlProgress {

    /**
     * 爬取进度通知
     * @param total 总页数
     * @param curPage 当前页数
     */
    public void crawlPageProgress(int total, int curPage);

    public void crawlPlayListProgress(int total, int curlist, List<PlayListBean> beans);

}
