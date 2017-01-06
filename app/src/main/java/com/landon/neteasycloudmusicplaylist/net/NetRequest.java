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
     * @param url     请求url
     * @param handler
     * @param id      请求id
     */
    public void request(String url, final Handler handler, final int type, final long id, final Request.Priority priority) {
        final StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Message msg = handler.obtainMessage(Constant.NET_SUCCESS);
                NetResult result = new NetResult();
                if(id != -1)
                    result.setId(id);
                result.setType(type);
                result.setMsg(response);
                msg.obj = result;
                //LogUtils.d("xu",response);
                LogUtils.d("xu","id" + id);
                msg.sendToTarget();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d("xu",error.getMessage());
                Message msg = handler.obtainMessage(Constant.NET_ERROR);
                NetResult result = new NetResult();
                if(id != -1)
                    result.setId(id);
                result.setType(type);
                result.setMsg(error.getMessage());
                msg.obj = result;
                msg.sendToTarget();

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
        stringRequest.setTag("net");

        mRequestQueue.add(stringRequest);
    }

    public void stopAllRequest(){
        mRequestQueue.cancelAll("net");
    }
}
