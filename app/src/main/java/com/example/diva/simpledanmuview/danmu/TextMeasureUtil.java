package com.example.diva.simpledanmuview.danmu;


import android.graphics.Paint;

/****
 *  测量的大小
 */
public class TextMeasureUtil {

    public static Paint measurePaint;
    public static  float textSize = -1;
    public static int measureWidth(String str){
        if(measurePaint == null){
            measurePaint = new Paint();
            if(textSize == -1){
                textSize = Utils.convertDpToPixel(DanmuConstants.TEXT_SIZE);
            }
            measurePaint.setTextSize(textSize);
        }
        return (int) measurePaint.measureText(str);
    }

    /***
     *   高度 为 纯粹的 高度 加上推荐的 上行间距
     * @return
     */
    public static int measureHeight(){
        if(measurePaint == null){
            measurePaint = new Paint();
            if(textSize == -1){
                textSize = Utils.convertDpToPixel(DanmuConstants.TEXT_SIZE);
            }
            measurePaint.setTextSize(textSize);
        }
        Paint.FontMetrics fontMetrics = measurePaint.getFontMetrics();
        return (int) (fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading);
    }
}
