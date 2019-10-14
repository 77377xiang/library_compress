package com.xiang.library_compress.picture;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;

import com.xiang.library_compress.utils.CachePathUtils;
import com.xiang.library_compress.utils.CommonUtils;
import com.xiang.library_compress.utils.Constants;
import com.xiang.library_compress.utils.UriParseUtils;

import java.io.File;


// 获取图片类
public class GetPicture {

    /**
     * 打开相册  自定义标识码
     *
     * @param activity 当前界面
     * @param code     标识码
     */
    public static void openAlbum(Activity activity, int code) {
        if (code < 0) {
            CommonUtils.openAlbum(activity, Constants.ALBUM_CODE);
        } else {
            openAlbum(activity);
        }
    }


    /**
     * 打开相册  有默认标识码 1002
     *
     * @param activity
     */
    public static void openAlbum(Activity activity) {
        CommonUtils.openAlbum(activity, Constants.ALBUM_CODE);
    }


    /**
     * 拍照
     *
     * @param activity
     * @return 返回照片路径
     */
    public static String camear(Activity activity) {
        String camearPath = "";
        Uri outPutUri; // 输出路径
        File file = CachePathUtils.getCameraCacheFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            outPutUri = UriParseUtils.getCameraOutPutUri(activity, file);
        } else {
            outPutUri = Uri.fromFile(file);
        }
        camearPath = file.getAbsolutePath();
        //  Log.d(TAG, "路径：" + file.getPath());
        // 启动拍照
        CommonUtils.hasCamera(activity, CommonUtils.getCameraIntent(outPutUri), Constants.CAMERA_CODE);
        return camearPath;
    }




}
