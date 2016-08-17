package com.landon.neteasycloudmusicplaylist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;

import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;
import com.landon.neteasycloudmusicplaylist.constant.Constant;
import com.landon.neteasycloudmusicplaylist.net.Crawl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 歌单数据库
 * Created by landon on 2016/8/7.
 */
public class CrawlerSQLHelper extends SQLiteOpenHelper {

    //private static CrawlerSQLHelper INSTANCE;

    //数据库名称
    public static String databasePath = "/sdcard/Android/data/com.landon.neteasycloudmusicplaylist/database/";
    //数据库版本
    private static int version = 5;
    //数据库名字
    public static String databaseName = "playlist.db";
    //表名
    private static String tableName = "playlist";

    public CrawlerSQLHelper(Context context) {
        super(context,databasePath + databaseName, null, version);
    }

//    //单例模式
//    public static CrawlerSQLHelper getInstance(Context context){
//        if(INSTANCE == null){
//            synchronized (CrawlerSQLHelper.class){
//                if(INSTANCE == null){
//                    File file = new File(databasePath);
//                    if(!file.exists()){
//                        file.mkdirs();
//                    }
//                    file = new File(databasePath + databaseName);
//                    if(!file.exists()){
//                        try {
//                            file.createNewFile();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    INSTANCE = new CrawlerSQLHelper(context);
//                }
//            }
//        }
//        return INSTANCE;
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (id integer primary key,name varchar(500), author varchar(500), url varchar(500), image varchar(500), play_count integer, collect_count integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) {
            db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN play_count integer default 0");
            db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN collect_count integer default 0");
        }
    }

    /**
     * 插入一个歌单
     *
     * @param bean 歌单bean
     * @return 成功插入返回true
     */
    public boolean insert(PlayListBean bean) {
        if(Constant.existDBFile) {
            SQLiteDatabase db = getWritableDatabase();
            Cursor cur = db.query(tableName, null, "id = ?", new String[]{String.format(Locale.CHINA, "%d", bean.getId())}, null, null, null);
            if (cur != null && cur.getCount() > 0) {
                cur.close();
                return update(bean);
            } else {
                if (cur != null)
                    cur.close();
                ContentValues cv = new ContentValues();
                cv.put("name", bean.getName());
                cv.put("id", bean.getId());
                cv.put("author", bean.getAuthor());
                cv.put("image", bean.getImage());
                cv.put("url", bean.getUrl());
                cv.put("play_count", bean.getPlayCount());
                cv.put("collect_count", bean.getCollectCount());
                boolean result = db.insert(tableName, null, cv) != -1;
//        db.close();
                return result;
            }
        }else{
            return false;
        }
    }

    /**
     * 插入歌单
     *
     * @param beans
     * @return
     */
    public boolean insert(List<PlayListBean> beans) {
        if(Constant.existDBFile) {
            if (beans != null && beans.size() > 0) {
                for (int i = 0; i < beans.size(); ++i) {
                    insert(beans.get(i));
                }
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * 删除数据中所有的歌单
     *
     * @return 删除成功返回true
     */
    public boolean deleteAll() {
        if(Constant.existDBFile) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(tableName, null, null);
//        db.close();
            return true;
        }else {
            return false;
        }
    }

    /**
     * 更新指定歌单
     *
     * @param bean 要更新的歌单
     * @return 更新成功返回true
     */
    public boolean update(PlayListBean bean) {
        if(Constant.existDBFile) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("name", bean.getName());
            cv.put("author", bean.getAuthor());
            cv.put("image", bean.getImage());
            cv.put("url", bean.getUrl());
            cv.put("play_count", bean.getPlayCount());
            cv.put("collect_count", bean.getCollectCount());
            boolean result = db.update(tableName, cv, "id = ?", new String[]{String.format(Locale.CHINA, "%d", bean.getId())}) > 0;
//        db.close();
            return result;
        }else {
            return false;
        }
    }

    /**
     * 删除歌单
     *
     * @param bean 要删除的歌单
     * @return 删除成功返回true
     */
    public boolean delete(PlayListBean bean) {
        if(Constant.existDBFile) {
            SQLiteDatabase db = getWritableDatabase();
            boolean result = db.delete(tableName, "id = ?", new String[]{String.format(Locale.CHINA, "%d", bean.getId())}) > 0;
//        db.close();
            return result;
        }else{
            return false;
        }
    }

    /**
     * 查询歌单
     *
     * @param id
     * @return
     */
    public PlayListBean query(long id) {
        if(Constant.existDBFile) {
            PlayListBean bean = new PlayListBean();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(tableName, null, "id = ?", new String[]{String.format(Locale.CHINA, "%d", id)}, null, null, null);
            if (cursor == null) {
                return bean;
            }
            if (cursor.getCount() == 0) {
                cursor.close();
                return bean;
            }
            if (cursor.moveToNext()) {
                bean.setId(cursor.getInt(0));
                bean.setName(cursor.getString(1));
                bean.setAuthor(cursor.getString(2));
                bean.setUrl(cursor.getString(3));
                bean.setImage(cursor.getString(4));
                bean.setPlayCount(cursor.getInt(5));
                bean.setCollectCount(cursor.getInt(6));
            }
            cursor.close();
//        db.close();
            return bean;
        }else{
            return null;
        }
    }

    /**
     * 查询全部歌单
     *
     * @return
     */
    public List<PlayListBean> query() {
        if(Constant.existDBFile) {
            List<PlayListBean> beans = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(tableName, null, null, null, null, null, null);
            if (cursor == null) {
                return beans;
            }
            if (cursor.getCount() == 0) {
                cursor.close();
                return beans;
            }
            while (cursor.moveToNext()) {
                PlayListBean bean = new PlayListBean();
                bean.setId(cursor.getInt(0));
                bean.setName(cursor.getString(1));
                bean.setAuthor(cursor.getString(2));
                bean.setUrl(cursor.getString(3));
                bean.setImage(cursor.getString(4));
                beans.add(bean);
            }
            cursor.close();
//        db.close();
            return beans;
        }else {
            return null;
        }
    }


    public void close() {
        if(Constant.existDBFile) {
            getWritableDatabase().close();
            getReadableDatabase().close();
        }
    }

    /**
     * 异步查询
     *
     * @param handler
     */
    public void queryAsync(final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PlayListBean> result = query();
                if(result != null && result.size() > 0) {
                    Message msg = handler.obtainMessage(Constant.ASYNC_QUERY);
                    msg.obj = result;
                    msg.sendToTarget();
                }
            }
        }).start();
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param sortType
     * @return
     */
    public List<PlayListBean> query(int page, int pageSize, int sortType) {
        if(Constant.existDBFile) {
            List<PlayListBean> beans = new ArrayList<>();
            SQLiteDatabase db = getReadableDatabase();
            String orderStr = "id";
            if (sortType == Constant.SORT_COLLECT_COUNT) {
                orderStr = "collect_count";
            } else if (sortType == Constant.SORT_PLAY_COUNT) {
                orderStr = "play_count";
            }
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " order by " + orderStr + " desc limit " + String.format(Locale.CHINA, "%d", page * pageSize) + "," + String.format(Locale.CHINA, "%d", pageSize), null);
            if (cursor == null) {
                return beans;
            }
            if (cursor.getCount() == 0) {
                cursor.close();
                return beans;
            }
            while (cursor.moveToNext()) {
                PlayListBean bean = new PlayListBean();
                bean.setId(cursor.getInt(0));
                bean.setName(cursor.getString(1));
                bean.setAuthor(cursor.getString(2));
                bean.setUrl(cursor.getString(3));
                bean.setImage(cursor.getString(4));
                bean.setPlayCount(cursor.getInt(5));
                bean.setCollectCount(cursor.getInt(6));
                beans.add(bean);
            }
            cursor.close();
//        db.close();
            return beans;
        }else{
            return null;
        }
    }
}
