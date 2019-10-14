package com.xiang.screensaver.sereen;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;


import java.util.List;

import static com.xiang.screensaver.sereen.OpenBrodcast.mWakeLock;


public class ScreenService extends Service {

    private static final String TAG = "ScreenServic2";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.e("ScreenService", "IBinder");
        return null;
    }

    @Override
    public void onCreate() {
        //Log.e("ScreenService","onCreate()");
        // TODO Auto-generated method stub
        // startScreenService();
        super.onCreate();
        Log.e("ScreenService", "onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // Log.e("ScreenService","onStart");
        // TODO Auto-generated method stub
        Log.e("ScreenService", "onStart");
        startScreenService();
    }

    @SuppressLint("MissingPermission")
    private void startScreenService() {
        Log.e("ScreenService", "startScreenService");

        onClick();

    }

    @Override
    public void onDestroy() {
        Log.e("ScreenService", "onDestroy()");
        super.onDestroy();
    }


    boolean isFirst;

    private void onClick() {

        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.d(TAG, "onReceive");
                String action = intent.getAction();

                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    // 亮屏幕

                    if (isBackground(context)) {
                        Log.d(TAG, "screen on 前台");
                    } else {
                        Log.d(TAG, "screen on 后台");
                        screenH(context);
                    }

                    // Intent intent2 = new Intent(MainActivity.this,NewDome.class);
                    //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // startActivity(intent2);
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    // 关屏幕
                    Log.d(TAG, "screen off");//
                    isFirst = true;
                    screenH(context);
                    // showTime();

                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    // 解锁
                    Log.d(TAG, "screen unlock");

                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    Log.i(TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");

                }
            }
        };
        Log.d(TAG, "registerReceiver");
        registerReceiver(mBatInfoReceiver, filter);

    }


    //释放设备电源锁
    public static void releaseWakeLock() {
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }


    //申请设备电源锁
    @SuppressLint("InvalidWakeLockTag")
    public static void acquireWakeLock(Context context) {
        // releaseWakeLock();
        // if (null == mWakeLock) {//屏幕唤醒
        //  releaseWakeLock();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //创建电源锁对象
        // mWakeLock = pm.newWakeLock(PARTIAL_WAKE_LOCK | ON_AFTER_RELEASE, "StartService");
        // 强制拉起屏幕 让屏幕不灭
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "StartService");

        if (null != mWakeLock) {

            mWakeLock.acquire();//获取到锁
            int a = getScreenBrightness(context);
        }

    }


    public void startSever() {
        Intent intent = new Intent(this, BannerViewActivity.class);
        startActivity(intent );
    }



    /**
     * 获取屏幕亮度
     *
     * @param context
     * @return
     */
    public static int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }


    /**
     * 屏幕常亮
     *
     * @param context
     */
    @SuppressLint("MissingPermission")
    private void screenH(Context context) {

     /*   PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mScreenWakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,
                this.getClass().getCanonicalName());
        if (!mScreenWakeLock.isHeld()) {
            mScreenWakeLock.acquire();
        }
 */


        // 屏蔽系统锁屏
        KeyguardManager manager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = manager.newKeyguardLock("KeyguardLock");
        lock.disableKeyguard();

        acquireWakeLock(context);

        startSever();


    }

    //  设置屏幕时间
    private void showTime() {
        // float result = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        //  Log.i(TAG, "result = " + result);
        //  Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 6 * 1000);


        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);

        Uri uri = Settings.System.getUriFor("screen_brightness");
        getContentResolver().notifyChange(uri, null);
    }


    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i("zjh", "处于后台" + appProcess.processName);
                    return true;
                } else {
                    Log.i("zjh", "处于前台" + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }


}