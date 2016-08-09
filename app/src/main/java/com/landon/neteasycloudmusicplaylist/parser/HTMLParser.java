package com.landon.neteasycloudmusicplaylist.parser;

import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * html解析
 * Created by landon.xu on 2016/8/9.
 */
public class HTMLParser {

    private static long id = 0;

    /**
     * 获取总共需要爬取的页数
     *
     * @param html html字符串
     * @return
     */
    public static int getTotalPage(String html) {
        int result = 0;
        if (html != null) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select(".zpgi");
            if (elements != null && elements.size() > 0) {
                String page = elements.last().text();
                result = Integer.parseInt(page);
            }
        }
        return result;
    }

    /**
     * 解析获得歌单
     *
     * @param html
     * @return 歌单列表
     */
    public static List<PlayListBean> getPlayList(String html) {
        List<PlayListBean> playList = new ArrayList<>();
        if (html != null) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select(".dec");
            if (elements != null && elements.size() > 0) {
                for (int i = 0; i < elements.size(); ++i) {
                    PlayListBean playListBean = new PlayListBean();
                    playListBean.setId(id++);
                    playListBean.setUrl(elements.get(i).attr("href"));
                    playListBean.setName(elements.get(i).text());
                    playList.add(playListBean);
                }
            }
        }
        return playList;
    }
}
