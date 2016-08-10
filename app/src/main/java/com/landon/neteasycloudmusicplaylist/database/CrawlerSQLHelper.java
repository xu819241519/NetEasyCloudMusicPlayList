package com.landon.neteasycloudmusicplaylist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Message;

import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;
import com.landon.neteasycloudmusicplaylist.constant.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 歌单数据库
 * Created by landon on 2016/8/7.
 */
public class CrawlerSQLHelper extends SQLiteOpenHelper {
    //数据库名称
    private static String databaseName = "neteasy_playlist";
    //数据库版本
    private static int version = 1;
    //数据库名字
    private String dataBaseName = "playlist";

    public CrawlerSQLHelper(Context context) {
        super(context, databaseName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + databaseName + " (id integer primary key,name varchar(500), author varchar(500), url varchar(500), image varchar(500))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 插入一个歌单
     *
     * @param bean 歌单bean
     * @return 成功插入返回true
     */
    public boolean insert(PlayListBean bean) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", bean.getName());
        cv.put("id", bean.getId());
        cv.put("author", bean.getAuthor());
        cv.put("image", bean.getImage());
        cv.put("url", bean.getUrl());
        boolean result = db.insert(databaseName, null, cv) != -1;
//        db.close();
        return result;
    }

    /**
     * 插入歌单
     *
     * @param beans
     * @return
     */
    public boolean insert(List<PlayListBean> beans) {
        if (beans != null && beans.size() > 0) {
            for (int i = 0; i < beans.size(); ++i) {
                insert(beans.get(i));
            }
        }
        return true;
    }

    /**
     * 删除数据中所有的歌单
     *
     * @return 删除成功返回true
     */
    public boolean deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(databaseName, null, null);
//        db.close();
        return true;
    }

    /**
     * 更新指定歌单
     *
     * @param bean 要更新的歌单
     * @return 更新成功返回true
     */
    public boolean update(PlayListBean bean) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", bean.getName());
        cv.put("author", bean.getAuthor());
        cv.put("image", bean.getImage());
        cv.put("url", bean.getUrl());
        boolean result = db.update(databaseName, cv, "id = ?", new String[]{String.format(Locale.CHINA, "%d", bean.getId())}) > 0;
//        db.close();
        return result;
    }

    /**
     * 删除歌单
     *
     * @param bean 要删除的歌单
     * @return 删除成功返回true
     */
    public boolean delete(PlayListBean bean) {
        SQLiteDatabase db = getWritableDatabase();
        boolean result = db.delete(databaseName, "id = ?", new String[]{String.format(Locale.CHINA, "%d", bean.getId())}) > 0;
//        db.close();
        return result;
    }

    /**
     * 查询歌单
     *
     * @param id
     * @return
     */
    public PlayListBean query(long id) {
        PlayListBean bean = new PlayListBean();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(databaseName, null, "id = ?", new String[]{String.format(Locale.CHINA, "%d", id)}, null, null, null);
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
        }
        cursor.close();
//        db.close();
        return bean;
    }

    /**
     * 查询全部歌单
     *
     * @return
     */
    public List<PlayListBean> query() {
        List<PlayListBean> beans = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(databaseName, null, null, null, null, null, null);
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
    }


    public void close() {
        getWritableDatabase().close();
        getReadableDatabase().close();
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
                Message msg = handler.obtainMessage(Constant.ASYNC_QUERY);
                msg.obj = result;
                msg.sendToTarget();
            }
        }).start();
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    public List<PlayListBean> query(int page, int pageSize) {
        List<PlayListBean> beans = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseName + " order by id limit " +  String.format(Locale.CHINA, "%d", page * pageSize) + "," + String.format(Locale.CHINA, "%d", pageSize),null);
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
    }
}
