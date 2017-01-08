package com.landon.neteasycloudmusicplaylist.net;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by landon.xu on 2016/8/9.
 */
public class NetResult implements Parcelable {

    public static final int STATUS_SUCCESS = 0;

    public static final int STATUS_FAILED = 1;

    private int status;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.msg);
    }

    public NetResult() {
    }

    protected NetResult(Parcel in) {
        this.status = in.readInt();
        this.msg = in.readString();
    }

    public static final Parcelable.Creator<NetResult> CREATOR = new Parcelable.Creator<NetResult>() {
        @Override
        public NetResult createFromParcel(Parcel source) {
            return new NetResult(source);
        }

        @Override
        public NetResult[] newArray(int size) {
            return new NetResult[size];
        }
    };
}
