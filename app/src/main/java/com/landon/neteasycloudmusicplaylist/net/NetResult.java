package com.landon.neteasycloudmusicplaylist.net;

/**
 * Created by landon.xu on 2016/8/9.
 */
public class NetResult {
    private int type;
    private long id;
    private String msg;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
