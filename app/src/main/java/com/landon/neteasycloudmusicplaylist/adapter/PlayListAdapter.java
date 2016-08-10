package com.landon.neteasycloudmusicplaylist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.landon.neteasycloudmusicplaylist.R;
import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by landon.xu on 2016/8/9.
 */
public class PlayListAdapter extends BaseAdapter {

    private Context context;

    public PlayListAdapter(Context context) {
        this.context = context;
    }

    private List<PlayListBean> playListBeanList = new ArrayList<>();

    @Override
    public int getCount() {
        if (playListBeanList == null || playListBeanList.size() == 0)
            return 1;
        else return playListBeanList.size();
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            if (playListBeanList == null || playListBeanList.size() == 0) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_no_playlist, null);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_playlist, null);
                viewHolder = new ViewHolder();
                viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (playListBeanList != null && playListBeanList.size() > 0) {

//            Glide.with(context).load(playListBeanList.get(position).getImage()).into(viewHolder.ivImage);
            if(playListBeanList.get(position).getName() != null && viewHolder != null && viewHolder.tvName != null)
                viewHolder.tvName.setText(playListBeanList.get(position).getName());
        }
        return convertView;
    }

    /**
     * 更新数据
     *
     * @param beans 歌单数据
     */
    public void update(List<PlayListBean> beans) {
        playListBeanList = beans;
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public ImageView ivImage;
        public TextView tvName;
    }
}
