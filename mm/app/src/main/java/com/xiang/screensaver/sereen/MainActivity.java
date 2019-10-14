package com.xiang.screensaver.sereen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.xiang.library_compress.CompressImageManager;
import com.xiang.library_compress.bean.Photo;
import com.xiang.library_compress.config.CompressConfig;
import com.xiang.library_compress.listener.CompressImage;
import com.xiang.library_compress.picture.GetPicture;
import com.xiang.library_compress.utils.Constants;
import com.xiang.library_compress.utils.FileUtil;
import com.xiang.library_compress.utils.UriParseUtils;
import com.xiang.screensaver.R;

import java.io.File;
import java.util.ArrayList;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //  压缩后的 存储目标路径
    //  String mCancheDir = Constants.BASE_CACHE_PATH + getPackageName() + "/cache/" + Constants.COMPRESS_CACHE;
    private static final String TAG = "MainActivity";
    private String camearPath;  //  拍照图片路径

    private CompressConfig compressConfig; // 压缩配置

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        String mCancheDir = Constants.BASE_CACHE_PATH + getPackageName() + "/Test/" + "xiang";
        // 检测文件夹在 不在创建 返回ture  表示成功；
        boolean isFile = FileUtil.checkFolder(mCancheDir);
        Log.d(TAG, "文件建立?:" + isFile);


        //  getPermission();
        // 初始化 默认
        // compressConfig = CompressConfig.getDefaultConfig();
        //  自定义初始化
        compressConfig = CompressConfig.builder()
                .setUnCompressMinPixel(1000)  //  最小压缩像素 默认1000
                .setUnCompressNormalPixel(2000) //  标准像素不压缩 2000
                .setMaxPixel(1200) //  长或者宽 不超过最大像素 默认1200
                .setMaxSize(80 * 1024) // 压缩到最大大小  默认200*1024  = 200kb
                .enablePixelCompress(true) // 是否启用像素压缩 默认  true
                .enableQualityCompress(true) // 是否启用质量压缩  true
                .enableReserveRaw(true) // 是否 保留原文件
                .setCacheDir("") // 压缩后缓存路径   //  不设置默认
                .setShowCompressDialog(true) // 是否显示进度条
                .create();

    }

    private void findViews() {
        findViewById(R.id.pBtn).setOnClickListener(this);
        findViewById(R.id.xBtn).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pBtn:
                camear();
                break;
            case R.id.xBtn:
                album();
                break;
        }
    }


    //  点击拍照
    private void camear() {
        camearPath = GetPicture.camear(this);
    }


    //  点击打开相册
    private void album() {
        GetPicture.openAlbum(this);
    }


    //  获取运行时权限
    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(perms[1]) == PackageManager.PERMISSION_DENIED
                    ) {
                requestPermissions(perms, 200);
            }
        }
    }


    // 鲁班 压缩测试
    //  缺点： 1 当没有 指定压缩路径时候 ， 抛出异常  不闪退
    //        2 源码中 压缩比率 固定值60  无法更改
    //        3 压缩配置 参数不太适应项目
    //        4 不能指定大小 比如100kb 以内
    private void testLuBan(String path) {

        String mCancheDir = Constants.BASE_CACHE_PATH + getPackageName() + "/cache/" + Constants.COMPRESS_CACHE;
        Log.d(TAG, "缓存路径：" + mCancheDir);
        Luban.with(this)
                .load(path) // 需要压缩的图片路径
                .ignoreBy(100) // 忽略100 kb 以下的
                .setTargetDir(mCancheDir) // 输出路径
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path)) || path.toLowerCase().endsWith(".gif");
                    }
                }) // 过滤 gif
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "onStart");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.d(TAG, "onSuccess");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");

                    }
                }).launch();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CAMERA_CODE && resultCode == RESULT_OK) {
            //  拍照返回
            Log.d(TAG, "拍照返回：" + camearPath);

            File file = new File(camearPath);

            String size = FileUtil.getFileMemorySize(file);
            Log.d(TAG, "拍照文件大小 ：" + size);
            preCompress(camearPath);
        }


        if (requestCode == Constants.ALBUM_CODE && resultCode == RESULT_OK) {
            // 相册返回
            if (data != null) {
                Uri uri = data.getData();
                String path = UriParseUtils.getPath(this, uri);
                Log.d(TAG, "相册返回：" + path);
                File file = new File(path);
                String size = FileUtil.getFileMemorySize(file);
                Log.d(TAG, "相册文件大小 ：" + size);
                preCompress(path);
            }
        }

    }


    /**
     * 准备压缩
     *
     * @param photoPath
     */
    private void preCompress(String photoPath) {
        ArrayList<Photo> photos = new ArrayList<>();
        photos.add(new Photo(photoPath));
        if (!photos.isEmpty()) {
            compress(photos);
        }
    }

    /**
     * 压缩  可以单张 可以多张
     *
     * @param photoList
     */
    private void compress(ArrayList<Photo> photoList) {
        // 压缩库使用
        CompressImageManager.build(this, compressConfig, photoList, new CompressImage.CompressListener() {
            @Override
            public void onCompressSuccess(ArrayList<Photo> images) {
                Log.d(TAG, "onCompressSuccess：" + images.get(0).getCompressPath());
                File file = new File(images.get(0).getCompressPath());
                String size = FileUtil.getFileMemorySize(file);
                Log.d(TAG, "压缩后大小 ：" + size);
            }

            @Override
            public void onCompressFailed(ArrayList<Photo> images, String error) {
                Log.d(TAG, "onCompressFailed：" + images.get(0).getOriginalPath());
            }
        }).compress();
    }

}
