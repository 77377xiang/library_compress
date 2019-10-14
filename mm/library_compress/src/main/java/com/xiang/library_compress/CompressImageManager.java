package com.xiang.library_compress;

import android.content.Context;
import android.text.TextUtils;

import com.xiang.library_compress.bean.Photo;
import com.xiang.library_compress.config.CompressConfig;
import com.xiang.library_compress.core.CompressImageUtil;
import com.xiang.library_compress.listener.CompressImage;
import com.xiang.library_compress.listener.CompressResultListener;

import java.io.File;
import java.util.ArrayList;


public class CompressImageManager implements CompressImage {

    private CompressImageUtil compressImageUtil;//压缩工具类（提炼）
    private ArrayList<Photo> images;//需要压缩的图片集合
    private CompressImage.CompressListener listener;//压缩的监听
    private CompressConfig config;//压缩的配置

    private CompressImageManager(Context context, CompressConfig compressConfig,
                                 ArrayList<Photo> images, CompressListener listener) {
        this.compressImageUtil = new CompressImageUtil(context,compressConfig);
        this.images = images;
        this.listener = listener;
        this.config = compressConfig;
    }

    public static CompressImage build(Context context, CompressConfig compressConfig,
                                      ArrayList<Photo> images, CompressListener listener) {
        return new CompressImageManager(context,compressConfig,images,listener);
    }


    @Override
    public void compress() {
        if(images==null||images.isEmpty()){
            listener.onCompressFailed(images,"有空情况。。。");
            return;
        }
        for (Photo image : images) {
            if(image==null){
                listener.onCompressFailed(images,"某张图片有情况。。。");
                return;
            }
        }
        //都没有问题了，开始从第一张开始压缩
        compress(images.get(0));
    }

    //index = 0
    private void compress(final Photo image){
        if(TextUtils.isEmpty(image.getOriginalPath())){
            continueCompress(image,false);
            return;
        }
        File file = new File(image.getOriginalPath());
        if(!file.exists()|| !file.isFile()){
            continueCompress(image,false);
            return;
        }

        if(file.length()<config.getMaxSize()){
            continueCompress(image,true);
            return;
        }
        //确实要压缩了
        compressImageUtil.compress(image.getOriginalPath(), new CompressResultListener() {
            @Override
            public void onCompressSuccess(String imgPath) {
                //压缩成功的图片路径设置到对象的属性中
                image.setCompressPath(imgPath);
                continueCompress(image,true);
            }

            @Override
            public void onCompressFailed(String imgPath, String error) {
                continueCompress(image,false,error);
            }
        });
    }

    private void continueCompress(Photo image,boolean bool,String... error){
        image.setCompressed(bool);
        //获取当前的索引
        int index = images.indexOf(image);
        //判断是否为压缩的图片最后一张
        if(index==images.size()-1){
            //不需要压缩了，告知activity界面
            callback(error);
        }else{
            //递归
            compress(images.get(index+1));
        }
    }

    private void callback(String... error) {
        if(error.length>0){
            listener.onCompressFailed(images,error[0]);
            return;
        }
        for (Photo image : images) {
            if(!image.isCompressed()){
                listener.onCompressFailed(images,"............");
                return;
            }
        }
        listener.onCompressSuccess(images);
    }
}
