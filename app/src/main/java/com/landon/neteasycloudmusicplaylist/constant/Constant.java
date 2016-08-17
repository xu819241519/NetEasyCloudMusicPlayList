package com.landon.neteasycloudmusicplaylist.constant;

/**
 * Created by landon.xu on 2016/8/9.
 */
public class Constant {
    /**
     * net request结果
     */
    //网络错误
    public static int NET_ERROR = 0;
    //请求成功
    public static int NET_SUCCESS = 1;

    /**
     * 网络请求地址
     */
    private static String baseURL = "http://music.163.com/discover/playlist/";

    public static String getURL(int page){
        if(page <= 0){
            return baseURL;
        }else{
            return  baseURL + "?order=hot&cat=%E5%85%A8%E9%83%A8&limit=35&offset=" + (35 * page);
        }
    }

    //异步查询
    public static int ASYNC_QUERY = 0;
    //按播放数排序
    public static final int SORT_PLAY_COUNT = 0;
    //按收藏数排序
    public static final int SORT_COLLECT_COUNT = 1;
    //启动scheme
    public static final String SCHEME = "orpheus://playlist/";

    //是否存在数据库文件
    public static boolean existDBFile = false;
}
