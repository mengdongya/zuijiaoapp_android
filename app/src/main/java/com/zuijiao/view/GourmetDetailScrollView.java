package com.zuijiao.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import net.zuijiao.android.zuijiao.R;

/**
 * used in gourmet detail activity ;
 */

public class GourmetDetailScrollView extends ScrollView {
    private static final int CHANGE_TOP = 1;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CHANGE_TOP) {
                int desY = msg.arg1;
                GourmetDetailScrollView.this.layout(GourmetDetailScrollView.this.getLeft(), desY,
                        GourmetDetailScrollView.this.getRight(),
                        GourmetDetailScrollView.this.getBottom());
                currentY = desY;
                onScrollListener.onTopChange(GourmetDetailScrollView.this.getTop());
            }
            super.handleMessage(msg);
        }

    };
    private OnScrollListener onScrollListener;

    public void setImageHeight(float imageHeight) {
        this.imageHeight = imageHeight;
        invalidate();
    }

    public float getImageHeight() {
        return imageHeight;
    }

    public float getTopY() {
        return topY;
    }

    public void setTopY(float topY) {
        this.topY = topY;
    }

    private float imageHeight = 450;


    private float bottomY = 0;
    private float topY = 170;
    private String log = "my_scrollview";
    private boolean onScrollChanged = false;
    private float downY = 0;
    private float tempY = 0;
    private boolean bRecorded = false;

    public int getCurrentY() {
        return currentY;
    }

    public float getBottomY() {
        return bottomY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    private int currentY = 450;

    private float speedY = 0 ;

    public GourmetDetailScrollView(Context context) {
        this(context, null);
    }

    public GourmetDetailScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GourmetDetailScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViewAttr(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(log, "onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        Log.i(log, "computeScrollDeltaToGetChildRectOnScreen");
        return 0;
    }

    private void initViewAttr(Context context) {
        imageHeight = context.getResources().getDimension(R.dimen.food_detail_image_height);
        bottomY = context.getResources().getDimension(R.dimen.scroll_view_bottom);
        topY = context.getResources().getDimension(R.dimen.toolbar_height);
        currentY = (int) bottomY;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        Log.i(log, "onScrollChanged");
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener != null) {
            onScrollListener.onScroll(t);
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                  boolean clampedY) {
        Log.i(log, "onOverScrolled");
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    /**
     * get use touch event
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                int scrollY = this.getScrollY();
                if (scrollY > 0) {
                    bRecorded = false;
                    break;
                }
                bRecorded = true;
                break;
            case MotionEvent.ACTION_MOVE:
                tempY = ev.getY();
                float top = this.getTop();
                float gapY = tempY - downY;
                Log.i(log ,"gapy ==" + gapY) ;
                if (top > bottomY && gapY > 0) {
                    gapY = gapY *10  / (top - bottomY) > gapY ? gapY : gapY *10  / (top - bottomY);
                }
                Log.i(log ,"gapy ==" + gapY) ;
                if (bRecorded) {
                    if ((gapY < 0 && top >= topY) || (gapY > 0 && top < imageHeight)) {
                        changePositionByTouch((int) gapY);
                        speedY =  gapY;
                        Log.i(log , "speedY == " + speedY) ;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                downY = 0;
                bRecorded = false;
                if (this.getTop() > bottomY)
                    kickBack();
                else if (this.getTop() < topY * 4 / 3)
                    scrollToTop();
                else{
                    inertia() ;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void inertia() {
        if(Math.abs(speedY) <= 5)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                float y = Math.abs(speedY) / 20 ;
                int sign = speedY > 0 ? 1 : -1 ;
                float desY = speedY;
                int top = GourmetDetailScrollView.this.getTop();
                try {
                    Thread.currentThread().sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (desY != 0 && !bRecorded) {
                    Message msg = new Message();
                    msg.what = CHANGE_TOP;
                    msg.arg1 = (int) (top + desY);
                    if(msg.arg1 >= bottomY || msg.arg1 <= topY){
                        break;
                    }
                    handler.sendMessage(msg);
                    try {
                        Thread.currentThread().sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    top = GourmetDetailScrollView.this.getTop();
                    desY = desY - sign* y ;
                    Log.i(log , "run :speedY == " + speedY+ " desY = " + desY + "  y == " + y  ) ;
                    if(Math.abs(desY) <= y )
                        desY = 0;
                }
            }
        }) .start();
    }

    private void scrollToTop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int top = GourmetDetailScrollView.this.getTop();
                try {
                    Thread.currentThread().sleep(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (top > topY && !bRecorded) {
                    int desY = (int) (top - topY / 10);
                    Message msg = new Message();
                    msg.what = CHANGE_TOP;
                    msg.arg1 = desY;
                    handler.sendMessage(msg);
                    try {
                        Thread.currentThread().sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    top = GourmetDetailScrollView.this.getTop();
                }
            }
        }) {

        }.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private synchronized  void changePositionByTouch(int y) {
        float desY = this.getTop() + y;
        if (desY > imageHeight) {
            desY = imageHeight;
        } else if (desY < topY) {
            desY = topY - 10;
        }
        this.layout(this.getLeft(), (int) desY, this.getRight(),
                this.getBottom());
        currentY = (int) desY;
        onScrollListener.onTopChange(this.getTop());
    }

    /**
     * when the scrollview is beyond the bottom , call this function move back auto
     */
    private void kickBack() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int top = GourmetDetailScrollView.this.getTop();
                try {
                    Thread.currentThread().sleep(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int distanceY = (int) ((top - bottomY) / 5);
                while (top > bottomY && !bRecorded) {

                    int desY = top - distanceY;
                    if (distanceY > 10)
                        distanceY -= 5;
                    Message msg = new Message();
                    msg.what = CHANGE_TOP;
                    msg.arg1 = desY;
                    handler.sendMessage(msg);
                    try {
                        Thread.currentThread().sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    top = GourmetDetailScrollView.this.getTop();
                }
            }
        }).start();
    }

    @Deprecated
    public void moveToTop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int top = GourmetDetailScrollView.this.getTop();
                try {
                    Thread.currentThread().sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                while (top > topY - 10) {

                    int desY = GourmetDetailScrollView.this.getTop() * 9 / 10 - 10;
                    if (desY <= topY - 10) {
                        desY = (int) topY - 10;
                    }
                    Message msg = new Message();
                    msg.what = CHANGE_TOP;
                    msg.arg1 = desY;
                    handler.sendMessage(msg);
                    try {
                        Thread.currentThread().sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    top = GourmetDetailScrollView.this.getTop();
                }
            }
        }) {

        }.start();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    public interface OnScrollListener {
        void onScroll(int scrollY);

        void onTopChange(int top);
    }

}
