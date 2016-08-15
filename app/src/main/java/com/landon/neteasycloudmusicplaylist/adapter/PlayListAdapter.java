package com.landon.neteasycloudmusicplaylist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.landon.neteasycloudmusicplaylist.R;
import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by landon.xu on 2016/8/9.
 */
public class PlayListAdapter extends LRecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    private Context context;

    public PlayListAdapter(Context context) {
        this.context = context;
    }

    private List<PlayListBean> playListBeanList = new ArrayList<>();


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, null);
        return new ViewHolder(view);
//        if (convertView == null) {
//            if (playListBeanList == null || playListBeanList.size() == 0) {
//                convertView =
//            } else {
//                convertView = LayoutInflater.from(context).inflate(R.layout.item_playlist, null);
//                viewHolder = new ViewHolder();
//                viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
//                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
//                convertView.setTag(viewHolder);
//            }
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        if (playListBeanList != null && playListBeanList.size() > 0) {
//
////            Glide.with(context).load(playListBeanList.get(position).getImage()).into(viewHolder.ivImage);
//            if(playListBeanList.get(position).getName() != null && viewHolder != null && viewHolder.tvName != null)
//                viewHolder.tvName.setText(playListBeanList.get(position).getName());
//        }
//        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvName.setText(playListBeanList.get(position).getName());
        Glide.with(context).load(playListBeanList.get(position).getImage()).into(holder.ivImage);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return playListBeanList.size() == 0 ? 0 : playListBeanList.size();
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

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        public TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
