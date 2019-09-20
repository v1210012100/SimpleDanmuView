package com.example.diva.simpledanmuview.danmu;

import android.content.Context;


import com.example.diva.simpledanmuview.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/***
 *  将评论转换为弹幕
 */
public class FakeDanmuFactory {

    //
    public static ArrayList<String> danmuTexts = new ArrayList<>();
    /***
     *  @param videoLength   视频的时长 ，单位是  s
     * @return
     */
    public static ArrayList<DanmuItem >  getComment(int videoLength){
        Collections.shuffle(danmuTexts);
        ArrayList<DanmuItem > danmuItems = new ArrayList<>();

        // 弹幕的数目设置为 时长的一半
        int danmuNus;

        Random random = new Random();
        int type = random.nextInt(3);
        if(type == 0){
            danmuNus = videoLength/2;
        }else if(type == 1){
            danmuNus = videoLength/3;
        }else{
            danmuNus = (videoLength/4)*3;
        }

        int half = danmuNus/2;
        for(int i = 0;i<half;i++){
            if(i<danmuTexts.size()){
                int timeSecond = random.nextInt(videoLength);
                String text = danmuTexts.get(i);
                DanmuItem danmuItem = new DanmuItem(text,timeSecond*1000 + random.nextInt(1000));
                int timeSecondSame = random.nextInt(videoLength);
                DanmuItem danmuItemSame = new DanmuItem(text,timeSecondSame*1000 + random.nextInt(1000));
                danmuItems.add(danmuItem);
                danmuItems.add(danmuItemSame);
            }else{
                int timeSecond = random.nextInt(videoLength);
                String text = danmuTexts.get(random.nextInt(danmuTexts.size()));
                int timeSecondSame = random.nextInt(videoLength);
                DanmuItem danmuItemSame = new DanmuItem(text,timeSecondSame*1000 + random.nextInt(1000));
                DanmuItem danmuItem = new DanmuItem(text,timeSecond*1000 + random.nextInt(1000));
                danmuItems.add(danmuItem);
                danmuItems.add(danmuItemSame);
            }
        }
        return danmuItems;
    }


    /***
     * Application 初始化的时候调用
     * @return
     */
    public static  void initDate(Context context){
        ArrayList<String> texts = new ArrayList<>();
        try {
            InputStreamReader inputreader = new InputStreamReader(context.getResources().openRawResource(R.raw.danmu),"GBK");
            BufferedReader buffreader = new BufferedReader(inputreader);
            String lineTxt = null;
            while ((lineTxt = buffreader.readLine()) != null) {
                texts.add(lineTxt) ;
            }
            buffreader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        danmuTexts.clear();
        danmuTexts.addAll(texts);
    }

}
