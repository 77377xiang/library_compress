package com.xiang.screensaver.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.xiang.screensaver.R;


/**
 * ImageView创建工厂
 */
public class ViewFactory {

    /**
     * 获取ImageView视图的同时加载显示url
     *
     * @param url
     * @return
     */
    public static ImageView getImageView(Context context, String url) {
        ImageView imageView = (ImageView)LayoutInflater.from(context).inflate(
                R.layout.public_layout_view_banner, null);
       // ImageLoaderUtils.loadDefault( context,  url,imageView);
        loadBase64( context,  url,imageView);
        return imageView;
    }



    /**  加载base 64  图片
     * @param context
     * @param uil
     * @param view
     */

    public static void loadBase64(Context context, String uil, ImageView view) {
        byte[] decodedString = Base64.decode(uil.split(",")[1], Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        view.setImageBitmap(decodedByte);

    }





}