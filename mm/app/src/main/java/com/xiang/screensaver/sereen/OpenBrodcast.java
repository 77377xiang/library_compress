package com.xiang.screensaver.sereen;

/**
 * 解锁监听
 */

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.List;

import static android.os.PowerManager.ON_AFTER_RELEASE;
import static android.os.PowerManager.PARTIAL_WAKE_LOCK;

public class OpenBrodcast extends BroadcastReceiver {


    private static final String TAG = "OpenBrodcast";
    public static PowerManager.WakeLock mWakeLock = null;

    //判断是否运行在模拟器上
    public boolean isRunningInEmualtor() {
        boolean qemuKernel = false;
        Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("getprop ro.kernel.qemu");
            //
            os = new DataOutputStream(process.getOutputStream());//
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            // getprop ro.kernel.qemu == 1 在模拟器
            // getprop ro.product.model == "sdk" 在模拟器
            // getprop ro.build.tags == "test-keys" 在模拟器
            qemuKernel = (Integer.valueOf(in.readLine()) == 1);
            // Log.d("com.droider.checkqemu", "检测到模拟器:" + qemuKernel);
        } catch (Exception e) {
            qemuKernel = false; //
            //Log.d("com.golden.plugin","run faild" + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null)
                    process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.d("com.golden.plugin","run finally");
        }
        return qemuKernel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> serviceList = am
                .getRunningServices(Integer.MAX_VALUE);

        //判断我们的服务是否在运行
        if (isRunningInEmualtor()) {
            System.exit(0);
        } else {
            boolean isRun = false;
            //跳转到后台服务(这里执行自己要完成的事情)
            intent.setClass(context, ScreenService.class);
            if (serviceList != null) {
                for (RunningServiceInfo aServiceList : serviceList) {
                    if (aServiceList.service.getClassName().equals(
                            "OpenAndroid.MyService")) {
                        isRun = true;
                        break;
                    }
                }
            }
            //获取设备电源锁
            acquireWakeLock(context);

            if (isRun) {

            } else {
                context.startService(intent);

            }
        }
    }


    //申请设备电源锁
    @SuppressLint("InvalidWakeLockTag")
    public static void acquireWakeLock(Context context) {
        releaseWakeLock();
        if (null == mWakeLock) {//屏幕唤醒
            releaseWakeLock();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //创建电源锁对象
            mWakeLock = pm.newWakeLock(PARTIAL_WAKE_LOCK | ON_AFTER_RELEASE, "StartService");
            if (null != mWakeLock) {
                mWakeLock.acquire();//获取到锁

            }
        }

    }



    //释放设备电源锁
    public static void releaseWakeLock() {
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }


    /**
     * 屏幕常亮
     *
     * @param context
     */
    private void screenH(Context context) {

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mScreenWakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,
                this.getClass().getCanonicalName());
        if (!mScreenWakeLock.isHeld()) {
            mScreenWakeLock.acquire();
        }
    }


}