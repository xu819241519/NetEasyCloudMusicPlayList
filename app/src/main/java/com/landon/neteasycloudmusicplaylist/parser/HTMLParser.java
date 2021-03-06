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
    public static List<String> getPlayListURL(String html) {
        List<String> playList = null;
        if (html != null) {
            Document document = Jsoup.parse(html);
            Elements covers = document.select(".u-cover-1");
            Elements decs = document.select(".dec");
            Elements authors = document.select(".s-fc3");
            Elements playCounts = document.select(".nb");
            if (decs.size() == covers.size() && authors.size() >= decs.size() && playCounts.size() >= decs.size()) {
                int size = decs.size();
                for (int i = 0; i < decs.size(); ++i) {
                    String url = decs.get(i).children().first().attr("href");
                    if (url != null) {
                        if(playList == null){
                            playList =new ArrayList<>();
                        }
                        playList.add("http://music.163.com" + url);
                    }
                }
            }
        }
        return playList;
    }

    public static PlayListBean getPlayListBean(String html){
        if (html != null && !"".equals(html)) {
            PlayListBean bean = new PlayListBean();
            Document document = Jsoup.parse(html);
            //设置收藏数
            Elements elements = document.select(".u-btni-fav");
            if (elements != null && elements.size() > 0) {
                String collectCount = elements.first().attr("data-count");
                if (collectCount != null && !"".equals(collectCount)) {
                    bean.setCollectCount(Integer.parseInt(collectCount));
                }
            }
            //设置歌名
            elements = document.select(".f-ff2.f-brk");
            if(elements != null && elements.size() > 0){
                bean.setName(elements.first().text());
            }
            //设置url链接和id
            elements =document.select("#content-operation");
            if(elements != null && elements.size() > 0){
                String id = elements.first().attr("data-rid");
                bean.setId(Integer.parseInt(id));
                bean.setUrl("http://music.163.com/playlist?id=" + id);
            }
            //设置图片
            elements = document.select(".j-img");
            if(elements != null && elements.size() > 0 ){
                bean.setImage(elements.first().attr("data-src"));
            }
            //设置作者
            elements =document.select(".s-fc7");
            if(elements != null && elements.size() > 0) {
                bean.setAuthor(elements.first().text());
            }

            //设置播放次数
            elements =document.select("#play-count");
            if(elements !=null && elements.size() > 0){
                bean.setPlayCount(Integer.parseInt(elements.first().text()));
            }
            return bean;
        }
        return null;
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
            if (decs.size() == covers.size() && authors.size() >= decs.size() && playCounts.size() >= decs.size()) {
                int size = decs.size();
                for (int i = 0; i < decs.size(); ++i) {
                    PlayListBean playListBean = new PlayListBean();
                    //设置URL和id
                    String url = decs.get(i).children().first().attr("href");
                    if (url != null) {
                        int index = url.lastIndexOf('=');
                        if (index > 0) {
                            String sID = url.substring(index + 1);
                            playListBean.setId(Integer.parseInt(sID));
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
                    if (pos > 0) {
                        count = 10000;
                        strPlayCount = strPlayCount.substring(0, pos);
                    }
                    count += Integer.parseInt(strPlayCount);
                    playListBean.setPlayCount(count);

                    playList.add(playListBean);
                }
            } else {
                LogUtils.e("landon", "爬取列表数目不对应");
            }
        }
        return playList;
    }

    //获取收藏信息

    public static void initCollectInfo(PlayListBean bean, String html) {
        if (html != null && !"".equals(html)) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select(".u-btni-fav");
            if (elements != null && elements.size() > 0) {
                String collectCount = elements.first().attr("data-count");
                if (collectCount != null && !"".equals(collectCount)) {
                    bean.setCollectCount(Integer.parseInt(collectCount));
                }
            }
        }
    }
}
