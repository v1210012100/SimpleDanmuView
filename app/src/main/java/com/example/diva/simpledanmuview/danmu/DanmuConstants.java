package com.example.diva.simpledanmuview.danmu;

import android.graphics.Color;

public class DanmuConstants {
    //192.168.100.19    192.168.100.42
    public static final int TEXT_SIZE = 30;    // 单位 dp
    public static final int DANMU_ON_SCREEN_TIME = 6111;   // 单位 ms
    public static final int DANMU_PARALLEL = 5111;          // 两条弹幕时间差 小于 它，则并行显示 。 单位 ms
    public static final int DANMU_MARGIN_BOTTOM = 5;   // 单位 dp
    public static final int DANMU_COLOR = Color.WHITE;
    public static final  float  DANMU_MAX_LINES = 3;   // 弹幕最多并行显示多少条
}
