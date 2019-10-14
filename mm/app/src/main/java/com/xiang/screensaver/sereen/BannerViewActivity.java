package com.xiang.screensaver.sereen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiang.screensaver.R;
import com.xiang.screensaver.bean.BannerBean;
import com.xiang.screensaver.view.BannerViewPager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.xiang.screensaver.sereen.OpenBrodcast.releaseWakeLock;


public class BannerViewActivity extends Activity implements BannerViewPager.BannerGetTime {


    BannerViewPager bannerView;
    List<BannerBean> mData;
    private List<String> imageList = new ArrayList<>();
    int mPostion = 0;
    private static final int CHANGE_TEXT = 1;
    private static final int CHANGE_TEXT_2 = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullscreen(true);
        setContentView(R.layout.activiy_banner);
        creatData();
        bannerView = findViewById(R.id.bannerView);
        bannerView.initBannerTime(this);
        // 在加载数据前设置是否循环

        //开始轮播
        bannerView.setWheel(true);
        // 设置轮播时间，默认3000ms
        // bannerView.setScrollTime(2000);
        //设置圆点指示图标组居中显示，默认靠右
        bannerView.setIndicatorCenter();
        bannerView.setVisibility(View.VISIBLE);
        setUi();


    }







    private void creatData() {

        // getSeverData();

        if (mData == null || mData.size() < 1) {
          String    data = getJson("dynamic.json", this);
            Type listType = new TypeToken<LinkedList<BannerBean>>() {
            }.getType();
            Gson gson = new Gson();
            // LinkedList<BannerBean> mData = gson.fromJson(jsonStr, listType);
            mData = gson.fromJson(data, listType);
            for (int i = 0; i < mData.size(); i++) {
                imageList.add(mData.get(i).getActivityPic());
            }
        }


    }


    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Override
    public int getBannerTime() {


        Log.d("getBannerTime", "getBannerTime ：" + mData.get(0).getPlayTime());

        if (mData == null || mData.size() < 1) {
            return 1000;
        }
        if (mPostion == 0) {
           // Log.d("SSSSSSSSS", "getBannerTime ：" + mData.get(0).getPlayTime());
            return mData.get(0).getPlayTime();
        } else if (mPostion == mData.size() - 1) {
           // Log.d("SSSSSSSSS", "getBannerTime ：" + mData.get(mData.size() - 1).getPlayTime());
            return mData.get(mData.size() - 1).getPlayTime();
        } else {
           // Log.d("SSSSSSSSS", "getBannerTime ：" + mData.get(mPostion).getPlayTime());
            return mData.get(mPostion).getPlayTime();
        }

        // return 5000;
    }


    //  设置ui 数据
    private void setUi() {

        if (imageList == null || imageList.size() < 1) {
            return;
        }
        bannerView.setData(imageList, new BannerViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int postion) {
                finish();
                releaseWakeLock();
            }
        });

        bannerView.setBannerScrollListener(new BannerViewPager.OnBannerScrollListener() {
            @Override
            public void onCurrentClick(int postion) {
                Log.d("SSSSSSSSS", "位置：" + postion);
                mPostion = postion;
            }
        });
    }



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGE_TEXT:
                    setUi();
                    break;
                case CHANGE_TEXT_2:
                    Toast.makeText(BannerViewActivity.this, "网络数据为空 加载本地数据", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };


    private void fullscreen(boolean enable) {

        if (enable) { //显示状态栏

            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;

            getWindow().setAttributes(lp);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        } else { //隐藏状态栏

            WindowManager.LayoutParams lp = getWindow().getAttributes();

            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);

            getWindow().setAttributes(lp);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }


}

