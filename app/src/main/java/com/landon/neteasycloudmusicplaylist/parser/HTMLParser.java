package com.landon.neteasycloudmusicplaylist.parser;

import com.landon.neteasycloudmusicplaylist.Utils.LogUtils;
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

            Elements covers = document.select(".u-cover-1");
            Elements decs = document.select(".dec");
            Elements authors = document.select(".s-fc3");
            Elements playCounts = document.select(".nb");
            if(decs.size() == covers.size() && authors.size() >= decs.size() && playCounts.size() >= decs.size()) {
                int size = decs.size();
                for(int i = 0; i < decs.size(); ++i){
                    PlayListBean playListBean = new PlayListBean();
                    //设置URL和id
                    String url = decs.get(i).children().first().attr("href");
                    if(url != null){
                        int index = url.lastIndexOf('=');
                        if(index > 0){
                            String sID = url.substring(index + 1);
                            playListBean.setId(Long.parseLong(sID));
                        }
                        playListBean.setUrl("http://music.163.com" + url);
                    }
                    //设置歌单名
                    playListBean.setName(decs.get(i).text());
                    //设置封面图片
                    playListBean.setImage(covers.get(i).children().first().attr("src"));
                    //设置作者
                    playListBean.setAuthor(authors.get(i).attr("title"));
                    //设置播放次数
                    String strPlayCount = playCounts.get(i).text();
                    int pos = strPlayCount.lastIndexOf("万");
                    int count = 0;
                    if(pos > 0){
                        count = 10000;
                        strPlayCount = strPlayCount.substring(0,pos);
                    }
                    count += Integer.parseInt(strPlayCount);
                    playListBean.setPlayCount(count);

                    playList.add(playListBean);
                }
            }else{
                LogUtils.e("landon","爬取列表数目不对应");
            }
        }
        return playList;
    }
}
