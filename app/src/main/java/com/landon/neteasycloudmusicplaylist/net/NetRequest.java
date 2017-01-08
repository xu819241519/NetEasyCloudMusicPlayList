package com.landon.neteasycloudmusicplaylist.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.landon.neteasycloudmusicplaylist.Utils.LogUtils;
import com.landon.neteasycloudmusicplaylist.constant.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求
 * Created by landon.xu on 2016/8/9.
 */
public class NetRequest {

    private static NetRequest INSTANCE;

    private RequestQueue mRequestQueue;

    private NetRequest(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    //单例模式
    public static NetRequest getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (NetRequest.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetRequest(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 请求网络
     *
     */
    public void request(String url, final Message message, final Request.Priority priority, String tag) {
        final StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(message !=null) {
                    NetResult result= new NetResult();
                    result.setStatus(NetResult.STATUS_SUCCESS);
                    result.setMsg(response);
                    message.obj = result;
                    message.sendToTarget();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d("xu",error.getMessage());
                if(message != null) {
                    NetResult result = new NetResult();
                    result.setMsg(error.getMessage());
                    result.setStatus(NetResult.STATUS_FAILED);
                    message.obj = result;
                    message.sendToTarget();
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
                return map;
            }

            @Override
            public Priority getPriority() {
                return priority;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(2*1000,1,1.0f));
        stringRequest.setTag(tag);
        mRequestQueue.add(stringRequest);
    }

    public void cancelRequest(String tag){
        mRequestQueue.cancelAll(tag);
    }

}
