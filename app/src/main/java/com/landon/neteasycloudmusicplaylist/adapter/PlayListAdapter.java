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
import com.landon.neteasycloudmusicplaylist.constant.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by landon.xu on 2016/8/9.
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

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
        holder.tvPlay.setText(playListBeanList.get(position).getPlayCount() + "播放");
        holder.tvCollect.setText(playListBeanList.get(position).getCollectCount() + "收藏");
    }

    //获取歌单id
    public int getPlayListID(int position){
        if(playListBeanList != null && playListBeanList.size() > position){
            return playListBeanList.get(position).getId();
        }
        return -1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return playListBeanList.size();
    }

    /**
     * 更新数据
     *
     * @param beans 歌单数据
     */
    public void update(List<PlayListBean> beans, int sortType) {
        playListBeanList = beans;
        sort(sortType);
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     * @param beans
     */
    public void addData(List<PlayListBean> beans, int sortType){
        if(beans != null && beans.size() > 0){
            if(playListBeanList == null)
                playListBeanList = new ArrayList<>();
            playListBeanList.addAll(beans);
        }
        sort(sortType);
        notifyDataSetChanged();
    }

    //清除数据
    public void clearData(){
        if(playListBeanList == null ){
            playListBeanList = new ArrayList<>();
            return ;
        }
        if(playListBeanList.size() > 0)
            playListBeanList.clear();
    }

    //排序
    public void sort(int sortType){
        ListComparator listComparator = new ListComparator(sortType);
        Collections.sort(playListBeanList,listComparator);
    }

    //排序规则
    private class ListComparator implements Comparator<PlayListBean>{
        private int sortType = -1;

        public ListComparator(int sortType){
            this.sortType = sortType;
        }
        @Override
        public int compare(PlayListBean lhs, PlayListBean rhs) {
            if(sortType == Constant.SORT_COLLECT_COUNT){
                if(lhs.getCollectCount() > rhs.getCollectCount()){
                    return -1;
                }else if(lhs.getCollectCount() == rhs.getCollectCount()){
                    return 0;
                }else {
                    return 1;
                }
            }else if(sortType == Constant.SORT_PLAY_COUNT){
                if(lhs.getPlayCount() > rhs.getPlayCount()){
                    return -1;
                }else if(lhs.getPlayCount() == rhs.getPlayCount()){
                    return 0;
                }else {
                    return 1;
                }
            }else{
                if(lhs.getId() > rhs.getId()){
                    return -1;
                }else if(lhs.getId() == rhs.getId()){
                    return 0;
                }else {
                    return 1;
                }
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        public TextView tvName;
        public TextView tvCollect;
        public TextView tvPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvCollect = (TextView) itemView.findViewById(R.id.tv_collect);
            tvPlay = (TextView) itemView.findViewById(R.id.tv_play);
        }
    }
}
