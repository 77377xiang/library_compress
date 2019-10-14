package com.xiang.screensaver.bean;

import java.io.Serializable;

public class BannerBean implements Serializable {

    private String activityPic; //  图片
    private int playTime; //    时间 毫秒

    public String getActivityPic() {
        return activityPic == null ? "" : activityPic;
    }

    public void setActivityPic(String activityPic) {
        this.activityPic = activityPic;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }
}
