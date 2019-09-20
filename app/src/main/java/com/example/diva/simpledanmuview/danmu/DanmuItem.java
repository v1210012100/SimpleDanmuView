package com.example.diva.simpledanmuview.danmu;


import android.support.annotation.NonNull;

/***
 * 存储每条弹幕 的具体数据 ，不支持换行
 */
public class DanmuItem  implements Comparable<DanmuItem>{
    public String text;
    public long startTime;
    // 弹幕从右至左的移动速度
    public double moveVelocity = -1;
    public int width = -1;



    public int height = -1;
    public int top = -1;


    public int getBottom() {
        return top + height;
    }




    public double getMoveVelocity() {
        return moveVelocity;
    }

    public void setMoveVelocity(double moveVelocity) {
        this.moveVelocity = moveVelocity;
    }



    public DanmuItem(String text, long startTime) {
        this.text = text;
        this.startTime = startTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }


    @Override
    public int compareTo(@NonNull DanmuItem o) {
        if(this.startTime == o.startTime){
            return -1;
        }
        return (int) (this.startTime - o.startTime);
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /***
     *   以播放器的时间 为参照，
     *   判断该条弹幕是否 超出 边界
     * @param currentVideoTime
     * @return
     */
    public boolean isOutside(long currentVideoTime) {
        long dtime = currentVideoTime - startTime;
        return dtime <= 0 || dtime >= DanmuConstants.DANMU_ON_SCREEN_TIME;
    }

}
