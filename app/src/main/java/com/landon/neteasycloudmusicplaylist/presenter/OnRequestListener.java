package com.landon.neteasycloudmusicplaylist.presenter;

import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;

import java.util.List;

/**
 * Created by landon on 2017/1/10.
 */

public interface OnRequestListener {

    void update(List<PlayListBean> beans);
}
