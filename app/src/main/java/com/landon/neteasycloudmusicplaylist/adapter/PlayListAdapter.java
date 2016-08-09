package com.landon.neteasycloudmusicplaylist.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by landon.xu on 2016/8/9.
 */
public class PlayListAdapter extends BaseAdapter {

    private List<PlayListBean> playListBeanList = new ArrayList<>();

    @Override
    public int getCount() {
        return playListBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return playListBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    /**
     * 更新数据
     * @param beans 歌单数据
     */
    public void update(List<PlayListBean> beans){
        playListBeanList = beans;
        notifyDataSetChanged();
    }
}
