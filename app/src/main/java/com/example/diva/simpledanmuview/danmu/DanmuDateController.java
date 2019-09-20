package com.example.diva.simpledanmuview.danmu;

import java.util.Collection;
import java.util.TreeSet;

/***
 *  存取弹幕的地方
 */
public class DanmuDateController {

    TreeSet<DanmuItem> allDanmuItems = new TreeSet<>();

   DanmuItem  clipStartTime = new DanmuItem("",0);
   DanmuItem  clipEndTime = new DanmuItem("",0);


    public DanmuDateController(Collection<DanmuItem> danmuItems){
        allDanmuItems.addAll(danmuItems);
    }

    public DanmuDateController(){

    }


    public void addDanmus(Collection<DanmuItem> danmuItems){
        allDanmuItems.clear();
        allDanmuItems.addAll(danmuItems);
    }


    public void clearAllDanmusYPos(){
        for(DanmuItem danmuItem :allDanmuItems){
            danmuItem.top = -1;
        }
    }


    /***
     * 获取该时间点 屏幕上的弹幕
     * @param videoNowTime
     * @return
     */
    public Collection<DanmuItem> getDanmusOnScreen(long videoNowTime){
        clipStartTime.setStartTime(videoNowTime-DanmuConstants.DANMU_ON_SCREEN_TIME-10);
        clipEndTime.setStartTime(videoNowTime+DanmuConstants.DANMU_ON_SCREEN_TIME-10);
        return allDanmuItems.subSet(clipStartTime,clipEndTime);
    }


    /***
     * 有Seek 的操作
     * @param videoNowTime
     * @return
     */
    public Collection<DanmuItem> getDanmusOnScreen(long videoNowTime,long seekTime){

        clipStartTime.setStartTime(videoNowTime-DanmuConstants.DANMU_ON_SCREEN_TIME-10);
        clipEndTime.setStartTime(videoNowTime+DanmuConstants.DANMU_ON_SCREEN_TIME-10);

        if(seekTime>clipStartTime.startTime ){
            clipStartTime.startTime = seekTime;
        }
        return allDanmuItems.subSet(clipStartTime,clipEndTime);
    }
}
