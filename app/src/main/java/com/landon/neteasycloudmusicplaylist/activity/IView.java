package com.landon.neteasycloudmusicplaylist.activity;

import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;

import java.util.List;

/**
 * Created by landon on 2017/1/5.
 */

public interface IView {

    void showProgressDialog(String msg);

    void hideProgressDialog();

    void addList(List<PlayListBean> beans);

    void clearList();

}
