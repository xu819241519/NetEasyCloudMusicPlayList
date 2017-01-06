package com.landon.neteasycloudmusicplaylist.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.landon.neteasycloudmusicplaylist.R;
import com.landon.neteasycloudmusicplaylist.Utils.LogUtils;
import com.landon.neteasycloudmusicplaylist.Utils.NetworkUtils;
import com.landon.neteasycloudmusicplaylist.adapter.PlayListAdapter;
import com.landon.neteasycloudmusicplaylist.bean.PlayListBean;
import com.landon.neteasycloudmusicplaylist.constant.Constant;
import com.landon.neteasycloudmusicplaylist.database.CrawlerSQLHelper;
import com.landon.neteasycloudmusicplaylist.net.Crawl;
import com.landon.neteasycloudmusicplaylist.net.NetRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.zip.GZIPInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 主页面
 * <p/>
 * <p/>
 * //glide、volley集成
 * //dagger
 * swiperefresh、recyclerview support包中
 * 爬取网络的时候界面卡
 */
public class MainActivityback extends BaseActivity implements CrawlProgress {

    @BindView(R.id.rv_playlist)
    LRecyclerView rvPlayList;
    //适配器
    private PlayListAdapter rvAdapter;
    //适配器
    private LRecyclerViewAdapter lrvAdapter;

    private ProgressDialog progressDialog;
    //当前第几页
    private int curPage = 0;
    //每一页的数量
    private int pageSize = 30;
    //是否还有数据
    private boolean hasData = true;
    //获取page进度
    private static final int PAGE_PROGRESS_UPDATE = 0;
    //获取歌单进度
    private static final int PLAYLIST_PROGRESS_UPDATE = 1;
    //获取数据完成
    private static final int PROGRESS_COMPLETE = 2;


    //默认是收藏数排序
    private int sortType = Constant.SORT_COLLECT_COUNT;
    //请求读写权限
    private static int WRITE_EXTERNAL_PERMISSION = 0;
    //请求读写权限，清除之前的数据
    private static int WRITE_EXTERNAL_PERMISSION_CLEAR = 1;
    //请求网络后的读写权限
    private static int WRITE_EXTERNAL_PERMISSION_INTERNET = 2;

    private Crawl crawl;

    @Override
    public void crawlPageProgress(int total, int curPage, int failedCount) {
        LogUtils.d("xu", "一共" + total + "页，正在获取第" + curPage + "页");
        Message msg = handler.obtainMessage(PAGE_PROGRESS_UPDATE);
        msg.arg1 = total;
        msg.arg2 = curPage;
        msg.sendToTarget();

    }

    @Override
    public void crawlPlayListProgress(int total, int curlist,int failedCount, final List<PlayListBean> beans) {
        //完成爬取任务
        if (total == curlist) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d("MainActivity","update data");
                    CrawlerSQLHelper crawlerSQLHelper = new CrawlerSQLHelper(MainActivityback.this);
                    crawlerSQLHelper.insert(beans);
                    Message message = handler.obtainMessage(PROGRESS_COMPLETE);
                    message.sendToTarget();
                }
            }).start();
        } else {
            Message msg = handler.obtainMessage(PLAYLIST_PROGRESS_UPDATE);
            msg.arg1 = total;
            msg.arg2 = curlist;
            msg.sendToTarget();
        }
    }

    private static class MainHandler extends Handler {
        private WeakReference<MainActivityback> mActivity;

        public MainHandler(MainActivityback activity) {
            mActivity = new WeakReference<MainActivityback>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivityback activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case PAGE_PROGRESS_UPDATE:
                        if (activity.progressDialog != null) {
                            activity.progressDialog.setProgress((int) msg.arg2 * 100 / (int) msg.arg1);
                            activity.progressDialog.setMessage("正在获取第" + (int) msg.arg2 + "页，共有" + (int) msg.arg1 + "页");
                        }
                        break;
                    case PLAYLIST_PROGRESS_UPDATE:
                        if (activity.progressDialog != null) {
                            activity.progressDialog.setProgress((int) msg.arg2 * 100 / (int) msg.arg1);
                            activity.progressDialog.setMessage("正在获取第" + (int) msg.arg2 + "歌单，共有" + (int) msg.arg1 + "歌单");
                        }
                        break;
                    case PROGRESS_COMPLETE:
                        if (activity.progressDialog != null) {
                            activity.progressDialog.dismiss();
                            activity.progressDialog = null;
                        }
                        activity.queryDataBase(false);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private MainHandler handler = new MainHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
        testNetEasy();
    }

    private void testNetEasy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();

                String url = "http://music.163.com/eapi/batch";


                FormBody.Builder formEncodingBuilder = new FormBody.Builder();
                formEncodingBuilder.add("params", "0BD8BB39A78692F1744DEFF63EBC30F7889FA0D28FD18C56783C7BF3AADA4C516E269DCEF72717031B0D0797563D21D74A80931032E90A0DBF772B7B86DAB7B29C47227066BA6859EF81B2BDC94960501592EFDBED2FA4BB612DD34C3BE69C1CB997189A2D14BE23FACD2D81694F87D7D86DD3F48F213C035A89EDEE2F6336478BEBA964633B3DB2A074EA2662FE8AEC18A167403EA0D465ED99F6E0BF1B58D64E2F6FAB87BFB382901FB3F8D753ABABE5361DD03E8767F3CC5BE299EDCBF8CEA82126579A7E11CD9A6B7A95AEB41CEC237356031206C2C94443360BB430F44D4CE1F78FE98FDF4468B40977A33CD3A7AD9A9F926C5E1B3979139277DBCDF27E7EB4BFC0C4996CD069835883475527C7D296034459225E90FC0FD45F259EDAD79318B200CCC01B51E4571EFD93F7E7EFE09D1169A86936C7C3D1E0EAAFE6955D2A72808C6F340B4388E57F4443C22DCB267E6BA157E3256F2924B9A2DD0B1F4C001E848DC9F85F05DE82FCCA50763549329EF9DF1BC9746B9CFB7308D72159C5A5DC242B76960F7E62827FD52B8F4BCF7A667EBDAD93E5D34CB68D92ECBCD7FEE9265DD359457ED508F38B088041E5BBFDB949F891FA490B48B24C2C754762F31DC4C0F0C8E3930D08A628D82D10C6CADDEA0BBDF8D9FF405C9FE9B2E5622BD99757F50109BF2BBE0B6804606EB5EF23E3D772D023013244905739680AC5801E039D02D768DDB47BE085BE698DFA91C29B13F34AFEC3DA8E69251F8EB21D1A11B85F89B6383089FEF4713C1C21972D09E2433FEDADBAB3B6ED239935E06E76AACA3A66B3F11E51EFD0F5AD0CE6A32783");

                RequestBody requestBody = formEncodingBuilder.build();

                builder.addHeader("Accept", "*/*");
                builder.addHeader("Accept-Encoding", "gzip,deflate,sdch");
                builder.addHeader("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
                builder.addHeader("Connection", "keep-alive");
                builder.addHeader("Host", "music.163.com");
                builder.addHeader("Referer", "http://music.163.com/search/");

                builder.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36");
                builder.addHeader("Cookie", "appver=1.5.2");

                builder.post(requestBody);
                builder.url(url);

                Call call = okHttpClient.newCall(builder.build());
                Response response = null;
                try {
                    response = call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                GZIPInputStream gzipInputStream = null;
                try {
                    gzipInputStream = new GZIPInputStream(response.body().byteStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int length = -1;

                byte[] cache = new byte[1024];

                try {
                    while ((length = gzipInputStream.read(cache)) != -1) {
                        byteArrayOutputStream.write(cache, 0, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LogUtils.d("MainActivity",new String(byteArrayOutputStream.toByteArray()));
            }
        }).start();

    }

    //通过scheme启动网页云音乐
    private void postScheme(int playlistID) {
        Uri uri = Uri.parse(Constant.SCHEME + playlistID);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void initViews() {
        //设置空view
        View emptyView = findViewById(R.id.layout_empty);
        Button button = (Button) emptyView.findViewById(R.id.bt_crawl);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadUpdate();
            }
        });
        rvPlayList.setEmptyView(emptyView);
        //初始化lrecyclerview
        rvAdapter = new PlayListAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        lrvAdapter = new LRecyclerViewAdapter(rvAdapter);
        rvPlayList.setLayoutManager(gridLayoutManager);
        rvPlayList.setAdapter(lrvAdapter);
        rvPlayList.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (hasData) {
                    curPage++;
                    queryDataBase(false);
                }
            }
        });
        rvPlayList.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                curPage = 0;
                queryDataBase(true);
            }
        });
//        rvPlayList.setLScrollListener(new LRecyclerView.LScrollListener() {
//            @Override
//            public void onRefresh() {
//                curPage = 0;
//                queryDataBase(true);
//            }
//
//            @Override
//            public void onScrollUp() {
//
//            }
//
//            @Override
//            public void onScrollDown() {
//
//            }
//
//            @Override
//            public void onBottom() {
//                if (hasData) {
//                    curPage++;
//                    queryDataBase(false);
//                }
//            }
//
//            @Override
//            public void onScrolled(int distanceX, int distanceY) {
//
//            }
//
//            @Override
//            public void onScrollStateChanged(int state) {
//
//            }
//        });
        lrvAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                int id = rvAdapter.getPlayListID(i);
                if (id != -1)
                    postScheme(id);
            }
        });

        queryDataBase(false);
        //rvPlayList.addItemDecoration(new RecycleViewDivider(this,LinearLayoutManager.HORIZONTAL));
        //crawl();
    }

    /**
     * 查询数据库
     *
     * @param clear 是否清除以前的数据
     */
    private void queryDataBase(boolean clear) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            CrawlerSQLHelper crawlerSQLHelper = new CrawlerSQLHelper(this);
            if (clear) {
                rvAdapter.clearData();
            }
            List<PlayListBean> beans = crawlerSQLHelper.query(curPage, pageSize, sortType);
            if (beans != null && beans.size() == pageSize) {
                rvAdapter.addData(beans, sortType);
                rvPlayList.refreshComplete();
                lrvAdapter.notifyDataSetChanged();
                hasData = true;
            } else hasData = false;
            LogUtils.d("landon_empty_count", "" + rvAdapter.getItemCount());
            LogUtils.d("landon_empty_count_2", "" + lrvAdapter.getItemCount());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, clear ? WRITE_EXTERNAL_PERMISSION_CLEAR : WRITE_EXTERNAL_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_PERMISSION || requestCode == WRITE_EXTERNAL_PERMISSION_CLEAR) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                queryDataBase(requestCode != WRITE_EXTERNAL_PERMISSION);
            }
        } else if (requestCode == WRITE_EXTERNAL_PERMISSION_INTERNET) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downLoadUpdate();
            }
        }
    }

    private void crawl() {
        crawl = new Crawl(this, this);
        crawl.beginCrawl();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在获取数据...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(MainActivityback.this, "已取消更新", Toast.LENGTH_SHORT).show();
                NetRequest.getInstance(MainActivityback.this).stopAllRequest();

            }
        });
        progressDialog.show();
//        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                downLoadUpdate();
                break;
            case R.id.action_setting:
                showPopupMenu();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //从网络上获取数据
    private void downLoadUpdate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (NetworkUtils.isConnected(this)) {
                new AlertDialog.Builder(this).setMessage("从网络获取最新数据，因为歌单有1000多，如果用的是流量，可能会花费较多流量的，时间可能会占用3到5分钟，是否获取？").setPositiveButton("获取", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        crawl();
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            } else {
                Toast.makeText(this, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_INTERNET);
        }
    }


    //显示popmenu
    private void showPopupMenu() {
        final PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.action_setting));
        getMenuInflater().inflate(R.menu.popmenu, popupMenu.getMenu());
        if (sortType == Constant.SORT_COLLECT_COUNT)
            popupMenu.getMenu().findItem(R.id.sort_collect).setCheckable(true).setChecked(true);
        else if (sortType == Constant.SORT_PLAY_COUNT)
            popupMenu.getMenu().findItem(R.id.sort_play).setCheckable(true).setChecked(true);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sort_collect:
                        popupMenu.getMenu().findItem(R.id.sort_collect).setCheckable(true).setChecked(true);
                        if (sortType != Constant.SORT_COLLECT_COUNT) {
                            sortType = Constant.SORT_COLLECT_COUNT;
                            curPage = 0;
                            queryDataBase(true);
                        }
                        break;
                    case R.id.sort_play:
                        popupMenu.getMenu().findItem(R.id.sort_play).setCheckable(true).setChecked(true);
                        if (sortType != Constant.SORT_PLAY_COUNT) {
                            sortType = Constant.SORT_PLAY_COUNT;
                            curPage = 0;
                            queryDataBase(true);
                        }
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
