package com.example.diva.simpledanmuview.danmu;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleDanmuView extends View {
    private ArrayList<DanmuItem> danmuOnscreen = new ArrayList<>();
    public DanmuDateController dateController = new DanmuDateController() ;
    public LayoutDanmuItemYHelper layoutDanmuItemYHelper = new LayoutDanmuItemYHelper();

    private Canvas drawCanvas;
    private Paint drawPaint;
    private long startTime = 0;
    private long seekTime = 0;
    private  long pauseStartTime = 0;
    private  long pauseEndTime = 0;

    // 暂停时间会累加 ，以和视频同步
    private  long pauseDuration = 0;

    // 快进和快退的时间累加
    private long seekDiffTime = 0;


    private ValueAnimator valueAnimator;

    boolean isPause = false;

    boolean isUseValueAnimator = false;    // 是否由属性动画驱动 , 否者则使用 Choreographer 驱动

    boolean isStopped = false;

    boolean isHidden = false;               // 显示和隐藏弹幕

    FrameCallback mFrameCallback;            //  由Choreographer 回调驱动 ， 属性动画 本质上也是由 Choreographer 驱动的




    float textSize;
    int marginBottomLine;


    public SimpleDanmuView(Context context) {
        super(context);
        initPaint();
    }

    public SimpleDanmuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public SimpleDanmuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }


    /***
     * 设置 所有的弹幕
     */
    public void setDanmus(Collection<DanmuItem> danmuItems){
        dateController.addDanmus(danmuItems);
    }

    /***
     * 开始播放弹幕 由  Choreographer的弹幕 来驱动时间的更新 。
     */
    public void start(){
        // 记录开始播放的时间
        resetTimeHistory();
        startTime = System.currentTimeMillis();
        if(isUseValueAnimator){
            // 属性动画驱动
            if(valueAnimator == null){
                valueAnimator =  ValueAnimator.ofInt(0);
            }else{
                // 清除 播放上一次视频的回调信息
                valueAnimator.cancel();
                valueAnimator.removeAllUpdateListeners();
                valueAnimator =  ValueAnimator.ofInt(0);
            }
            valueAnimator.setDuration(Integer.MAX_VALUE);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if(!isPause)
                        invalidate();
                }
            });
            valueAnimator.start();
        }else{
            // 系统Choreographer驱动
            if(mFrameCallback == null){
                mFrameCallback = new FrameCallback();
            }
            if(!isStopped){
                Choreographer.getInstance().postFrameCallback(mFrameCallback);
            }
        }
    }

    private void resetTimeHistory(){
        startTime = 0;
        seekDiffTime = 0;
        pauseDuration = 0;
    }

    private class FrameCallback implements Choreographer.FrameCallback {
        @Override
        public void doFrame(long frameTimeNanos) {
            if(!isPause){
                invalidate();
            }
            if(!isStopped){
                Choreographer.getInstance().postFrameCallback(mFrameCallback);
            }
        }
    };


    /***
     * 设置弹幕并同时开始 播放弹幕
     */
    public void setDanmusAndPlay(Collection<DanmuItem> danmuItems){
            setDanmus(danmuItems);
            start();
    }

    /***
     *  设置弹幕 ， 并设置开始播放的时间
     *  @param videoTime ms
     */
    public void setDanmusAndPlay(Collection<DanmuItem> danmuItems,long videoTime){
        setDanmus(danmuItems);
        start();
        seek(videoTime);
    }

    /***
     *  隐藏弹幕
     */
    public void hide(){
        isHidden = true;
        if(isPause){
            // 暂停状态 下，清空 当前 canvas
            invalidate();
        }
    }


    /***
     * 弹幕从隐藏状态变为显示状态
     */
    public void show(){
        isHidden = false;
        if(isPause){
            // 暂停状态 下，清空 当前 canvas
            invalidate();
        }
    }




    /***
     * 暂停弹幕
     */
    public void pause(){
        if(!isPause){
            isPause = true;
            pauseStartTime =  System.currentTimeMillis();
        }

    }

    /***
     * 当视频恢复播放时调用
     */
    public void resume(){
        if(isPause){
            pauseEndTime = System.currentTimeMillis();
            pauseDuration = pauseDuration + (pauseEndTime-pauseStartTime);
            isPause = false;
        }
    }


    /***
     * Seek 到指定的播放时间 ，这时候 截取当前时间点 屏幕上应当显示的弹幕的逻辑
     * 小于 seekTime 的 弹幕 不会再截取
     * @param seekTime  ms
     */
    public void seek(long seekTime){
        long diff = seekTime - getNowVideoTime();     // diff >0  表示 快进 ，diff 小于0 表示快退 。
        this.seekTime = seekTime;
        seekDiffTime = seekDiffTime + diff;
        // 清空弹幕记录的历史位置信息
        dateController.clearAllDanmusYPos();
        layoutDanmuItemYHelper.clearParallelInfo();
        isPause = false;
    }


    /***
     *  需要销毁的时候调用 ，释放资源。
     */
    public void destroy(){
        isStopped = true;
        if(isUseValueAnimator &&valueAnimator != null){
            valueAnimator.cancel();
        }
    }


    /***
     * 将系统时间转换 为 视频播放的时间
     * @return
     */
    public long getNowVideoTime(){
        if(!isPause){
            return System.currentTimeMillis() - startTime - pauseDuration  +seekDiffTime;
        }else{
            return pauseStartTime - startTime - pauseDuration + seekDiffTime;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCanvas = canvas;
        drawDanmu(getNowVideoTime());
    }

    /***
     * 初始化 ，画笔
     */
    public void initPaint(){
        if(drawPaint == null){
            drawPaint = new Paint();
            drawPaint.setAntiAlias(true);
            drawPaint.setColor(DanmuConstants.DANMU_COLOR);
//            drawPaint.setTypeface(Typeface.DEFAULT_BOLD);
            Utils.init(getContext().getResources());
            textSize = Utils.convertDpToPixel(DanmuConstants.TEXT_SIZE);
            marginBottomLine = (int) Utils.convertDpToPixel(DanmuConstants.DANMU_MARGIN_BOTTOM);
            drawPaint.setTextSize(textSize);
        }
    }

    public void drawDanmu(long currentVideoTime){
        // 第一步 获取 屏幕上可能显示的弹幕
        danmuOnscreen.clear();
        if(seekTime==0){
            danmuOnscreen.addAll(dateController.getDanmusOnScreen(currentVideoTime));
        }else{
            danmuOnscreen.addAll(dateController.getDanmusOnScreen(currentVideoTime,seekTime));
        }




        // 第二步 对弹幕 做测量
        measureDanmuItems();

        // 第三步 确定弹幕的 Y 轴坐标 ， X 轴的坐标是通过 当前时间 和弹幕时间的差值 * 弹幕速度 。
        for(int i=0;i<danmuOnscreen.size();i++){
            DanmuItem danmuItem = danmuOnscreen.get(i);
            if (danmuItem.top == -1) {
                // 确定 弹幕的 Y轴 位置
                layoutDanmuItemYHelper.layoutItems(currentVideoTime, danmuItem);
            }

        }

        // 开始画弹幕
        for(int i = 0;i<danmuOnscreen.size();i++){
            DanmuItem danmuItem = danmuOnscreen.get(i);
            drawDanmuItem(danmuItem,currentVideoTime);
        }

    }


    public void drawDanmuItem(DanmuItem danmuItem ,long currentVideoTime){

        if (isHidden) {
            return;
        }
        if (danmuItem.isOutside(currentVideoTime)) return;
        float x = (float) (getWidth() - danmuItem.moveVelocity * (currentVideoTime - danmuItem.startTime));
        float y = danmuItem.getBottom();
        drawCanvas.drawText(danmuItem.text, x, y, drawPaint);
    }




    /***
     *  如果 弹幕没有测量过宽高 则 进行 测量
     */
    public void measureDanmuItems(){
        for(int i=0;i<danmuOnscreen.size();i++){
            DanmuItem danmuItem = danmuOnscreen.get(i);
            if (danmuItem.height == -1 || danmuItem.width == -1) {
                danmuItem.setWidth(TextMeasureUtil.measureWidth(danmuItem.text));
                danmuItem.setHeight(TextMeasureUtil.measureHeight());
            }
            if(danmuItem.moveVelocity == -1 ){
                double velocity = ((double) danmuItem.width+ getWidth())/DanmuConstants.DANMU_ON_SCREEN_TIME;
                danmuItem.setMoveVelocity(velocity);
            }
        }
    }




     class LayoutDanmuItemYHelper {

         //  记录 最近的 并行的 多条弹幕 ，一旦换行就清空 。
         ArrayList<DanmuItem> danmusParallel= new ArrayList<>();
         DanmuItem lastItem = null;

         /***
          *  清空当前屏幕最近的并行记录信息
          */
         public void clearParallelInfo(){
             danmusParallel.clear();
         }
        /***
         *   弹幕在 屏幕上的 X 的坐标是确定的 ，不确定的是 Y 坐标 。
         *   假设 有5 行 弹幕 的时间间隔 是 0.1 毫秒 。 如何确定他们 的 Y轴 坐标 。
         */
        public void  layoutItems( long currentVideoTime,DanmuItem nowItem){
            if(nowItem.isOutside(currentVideoTime)) return;
            if(danmusParallel.size() == 0){
                nowItem.top = 0;
            }else{
                // 检测是否可以并行显示，并行则利用上一条弹幕的位置
                for(int i=0;i<danmusParallel.size();i++){
                    if(isParallel(nowItem,danmusParallel.get(i),currentVideoTime)){
                        lastItem = danmusParallel.get(i);
                    }else{
                        // 另起一行  。清空并行弹幕的数据
                        danmusParallel.clear();
                        lastItem = null;
                        break;
                    }
                }

                if(lastItem == null){
                    nowItem.top = 0;
                }else{
                    nowItem.top = lastItem.getBottom()+ marginBottomLine;
                    // 检测是否 到达 显示区域的 底部
                    if(nowItem.getBottom() >getHeight()){
                        // 超出底部
                        nowItem.top = 0;
                        danmusParallel.clear();
                    }

                }

            }
            danmusParallel.add(nowItem);
            if(danmusParallel.size()>=DanmuConstants.DANMU_MAX_LINES){
                // 超过最大显示 行数后 ，需要另起一行
                danmusParallel.clear();
            }

        }


        /***
         * 检测两条弹幕的时间差 ,是否满足并行显示的条件 。
         * 相差大于 DANMU_PARALLEL 则 ： 新的弹幕另起一行  。
         * 小于 DANMU_PARALLEL 并行显示
         * @param itemA
         * @param itemB
         * @return
         */
        public boolean isParallel(DanmuItem itemA, DanmuItem itemB, long currentVideoTime){

            if(itemA.isOutside( currentVideoTime) || itemB.isOutside(currentVideoTime)){
                return false;
            }
            long dTime = itemA.getStartTime() - itemB.getStartTime();
            if (Math.abs(dTime) >= DanmuConstants.DANMU_PARALLEL ) {
                return false;
            }else{
                return true;
            }
        }

    }
}
