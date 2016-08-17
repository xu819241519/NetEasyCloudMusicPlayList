package com.landon.neteasycloudmusicplaylist.Utils;

import android.content.Context;

import com.landon.neteasycloudmusicplaylist.MyApplication;
import com.landon.neteasycloudmusicplaylist.database.CrawlerSQLHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by landon.xu on 2016/8/17.
 */
public class FileUtils  {

    // 复制和加载区域数据库中的数据
    public static boolean  CopySqliteFileFromRawToDatabases(Context context) throws IOException {

        // 第一次运行应用程序时，加载数据库到data/data/当前包的名称/database/<db_name>

        File dir = new File(CrawlerSQLHelper.databasePath);
        LogUtils.d("landon","!dir.exists()=" + !dir.exists());
        LogUtils.d("landon","!dir.isDirectory()=" + !dir.isDirectory());

        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        File file= new File(dir, CrawlerSQLHelper.databaseName);
        if(file.exists())
            return true;
        InputStream inputStream = null;
        OutputStream outputStream =null;

        //通过IO流的方式，将assets目录下的数据库文件，写入到SD卡中。
        if (!file.exists()) {
            try {
                file.createNewFile();

                inputStream = context.getClass().getClassLoader().getResourceAsStream("assets/" + CrawlerSQLHelper.databaseName);
                outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int len ;

                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer,0,len);
                }


            } catch (IOException e) {
                e.printStackTrace();

            }

            finally {

                if (outputStream != null) {

                    outputStream.flush();
                    outputStream.close();

                }
                if (inputStream != null) {
                    inputStream.close();
                }

            }

        }
        return file.exists();
    }
}
