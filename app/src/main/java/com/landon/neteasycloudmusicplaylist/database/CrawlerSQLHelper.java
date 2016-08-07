package com.landon.neteasycloudmusicplaylist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 歌单数据库
 * Created by landon on 2016/8/7.
 */
public class CrawlerSQLHelper extends SQLiteOpenHelper {
    //数据库名称
    private static String databaseName = "neteasy_playlist";
    //数据库版本
    private static int version = 1;

    public CrawlerSQLHelper(Context context) {
        super(context, databaseName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
