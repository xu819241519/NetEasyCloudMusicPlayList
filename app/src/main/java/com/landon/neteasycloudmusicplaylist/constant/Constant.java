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
}
