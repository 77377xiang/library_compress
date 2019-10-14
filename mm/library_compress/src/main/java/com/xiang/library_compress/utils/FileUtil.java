package com.xiang.library_compress.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author tjiang
 */
public class FileUtil {

    //获取外置存储设备（如SD卡）的根目录
    public static String getExternalStoragePath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    //删除外置存储设备（如SD卡）上指定路径的文件
    public static void deleteFileExternal(String path) {
        if (null == path || path.length() <= 0) {
            return;
        }

        //判断外置存储设备（如SD卡）是否存在，如果不存在，返回s
        if (!isExternalStorageExist()) {
            return;
        }

        File file = new File(path);
        if (!file.isFile()) {
            return;
        }

        file.delete();
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExist(String path) {
        if (null == path || path.length() <= 0) {
            return false;
        }

        File file = new File(path);
        try {
            return file.exists();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //判断外置存储设备（如SD卡）是否存在
    public static boolean isExternalStorageExist() {
        String status = Environment.getExternalStorageState();
        if (null == status || status.length() <= 0) {
            return false;
        }

        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }

        return false;
    }

    /**
     * 检验文件夹是否存在，如果不存在则新建文件夹，并返回是否新建成功
     *
     * @param dir String
     * @return 检验值 boolean
     */
    public static boolean checkFolder(String dir) {
        File folder = new File(dir);
        boolean isFolderExist = folder.exists();
        if (!isFolderExist) {
            if (!folder.mkdirs()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 清理文件夹
     */
    public static void cleanDir(String dirPath) {
        //判断SD卡是否存在，如果不存在，返回s
        if (!FileUtil.isExternalStorageExist()) {
            return;
        }

        File file = new File(dirPath);
        if (!file.exists()) {
            return;
        }

        if (!file.isDirectory()) {
            return;
        }

        String[] fileList = file.list();
        if (null == fileList) {
            return;
        }

//		for(String fileName:fileList){
//			String filePath = dirPath + fileName;
//			if(null != filePath && filePath.length() > 0){
//				deleteFileExternal(filePath);
//			}
//		}

        for (String fileName : fileList) {
            String filePath = dirPath + fileName;
            if (null != filePath && filePath.length() > 0) {
//				deleteFileExternal(filePath);
                File f = new File(filePath);
                if (!f.isFile()) {
                    //此时说明f是文件夹，所以路径结尾需要加"/"
                    cleanDir(filePath + "/");
                    //删除完文件夹里面的东西后删除文件夹
                    f.delete();
                } else {
                    f.delete();
                }
            }
        }
    }


    //  获取文件大小
    public static long getAllFileSize(List<File> files) {
        long total = 0;
        for (File file : files) {
            total += getFileSize(file);
        }

        return total;
    }


    /**
     * 获取指定文件大小(单位：字节)
     *
     * @param file
     * @return 返回字节
     * @throws Exception
     */
    public static long getFileSize(File file) {
        if (file == null) {
            return 0;
        }
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;

                fis = new FileInputStream(file);

                size = fis.available();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return size;
    }


    /**
     * 获取图片 大小
     *
     * @param bitmap
     * @return 返回具体大小
     */
    public static String getBitmapMemorySize(Bitmap bitmap) {

        long size = getBitmapSize(bitmap);
        String fileSize = "0KB";
        if (size > 0) {
            fileSize = fileSizeByteToM(size);
        }
        return fileSize;

    }


    /**
     * 获取文件大小
     *
     * @param file
     * @return 返回 具体大小
     */
    public static String getFileMemorySize(File file) {
        long fileByte = getFileSize(file);
        String fileSize = "0KB";
        if (fileByte > 0) {
            fileSize = fileSizeByteToM(fileByte);
        }
        return fileSize;
    }


    /**
     * 将文件大小由Byte转为MB或者KB
     *
     * @return 返回具体大小
     */
    public static String fileSizeByteToM(Long size) {

        BigDecimal fileSize = new BigDecimal(size);
        BigDecimal param = new BigDecimal(1024);
        int count = 0;
        while (fileSize.compareTo(param) > 0 && count < 5) {
            fileSize = fileSize.divide(param);
            count++;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        String result = df.format(fileSize) + "";
        switch (count) {
            case 0:
                result += "B";
                break;
            case 1:
                result += "KB";
                break;
            case 2:
                result += "MB";
                break;
            case 3:
                result += "GB";
                break;
            case 4:
                result += "TB";
                break;
            case 5:
                result += "PB";
                break;
        }
        return result;
    }


    /**
     * 获取bitmap的大小 Bytes 大小
     */
    public static long getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }


    /**
     * @param res          Resource
     * @param resId        资源id
     * @param targetWidth  目标图片的宽，单位px
     * @param targetHeight 目标图片的高，单位px
     * @return 返回压缩后的图片的Bitmap
     */
    public static Bitmap compressBitmap(Resources res, int resId, int targetWidth, int targetHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//设为true，节约内存
        BitmapFactory.decodeResource(res, resId, options);//返回null
        int height = options.outHeight;//得到源图片height，单位px
        int width = options.outWidth;//得到源图片的width，单位px
        //计算inSampleSize
        options.inSampleSize = calculateInSampleSize(width, height, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;//设为false，可以返回Bitmap
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 计算压缩比例
     *
     * @param width        源图片的宽
     * @param height       源图片的高
     * @param targetWidth  目标图片的宽
     * @param targetHeight 目标图片的高
     * @return inSampleSize 压缩比例
     */
    public static int calculateInSampleSize(int width, int height, int targetWidth, int targetHeight) {
        int inSampleSize = 1;
        if (height > targetHeight || width > targetWidth) {
            //计算图片实际的宽高和目标图片宽高的比率
            final int heightRate = Math.round((float) height / (float) targetHeight);
            final int widthRate = Math.round((float) width / (float) targetWidth);
            //选取最小的比率作为inSampleSize
            inSampleSize = heightRate < widthRate ? heightRate : widthRate;
        }
        return inSampleSize;
    }


}
