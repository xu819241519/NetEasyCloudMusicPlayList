package com.landon.neteasycloudmusicplaylist.activity;

import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;

import java.util.List;

/**
 * 爬取进度接口
 * Created by landon.xu on 2016/8/9.
 */
public interface CrawlProgressListener {

    /**
     * 爬取进度通知
     * @param total 总歌单数
     * @param curIndex 当前获取到的歌单数
     * @param failedCount 当前获取失败的歌单数
     */
    public void crawlProgress(int total, int curIndex, int failedCount, List<PlayListBean> playListBeens);

}
