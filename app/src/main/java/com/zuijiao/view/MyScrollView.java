package com.zuijiao.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
    private OnScrollListener onScrollListener;
    private float bottomY = 450;
    private float topY = 170;
    private static final int CHANGE_TOP = 1;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CHANGE_TOP) {
                int desY = msg.arg1;
                MyScrollView.this.layout(MyScrollView.this.getLeft(), desY,
                        MyScrollView.this.getRight(),
                        MyScrollView.this.getBottom());
                onScrollListener.onTopChange(MyScrollView.this.getTop());
            }
            super.handleMessage(msg);
        }

    };

    public float getBottomY() {
        return bottomY;
    }

    public void setBottomY(float bottomY) {
        this.bottomY = bottomY;
    }

    public float getTopY() {
        return topY;
    }

    public void setTopY(float topY) {
        this.topY = topY;
    }

    public MyScrollView(Context context) {
        this(context, null);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener != null) {
            onScrollListener.onScroll(t);
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                  boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    private float downY = 0;
    private float tempY = 0;
    private boolean bRecorded = false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                bRecorded = true;
                break;
            case MotionEvent.ACTION_MOVE:
                tempY = ev.getY();
                float top = this.getTop();
                float gapY = tempY - downY;
                if (bRecorded) {
                    if ((gapY < 0 && top >= topY) || gapY > 0 && top < bottomY) {

                        changePositionByTouch((int) (tempY - downY));
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                downY = 0;
                bRecorded = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void changePositionByTouch(int y) {
        float desY = this.getTop() + y;
        if (desY > bottomY) {
            desY = bottomY;
        } else if (desY < topY) {
            desY = topY - 10;
        }
        this.layout(this.getLeft(), (int) desY, this.getRight(),
                this.getBottom());
        onScrollListener.onTopChange(this.getTop());
    }

    public void moveToTop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int top = MyScrollView.this.getTop();
                try {
                    Thread.currentThread().sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                while (top > topY - 10) {

                    int desY = MyScrollView.this.getTop() * 9 / 10 - 10;
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
                    top = MyScrollView.this.getTop();
                }
            }
        }) {

        }.start();
    }

    public interface OnScrollListener {
        public void onScroll(int scrollY);

        public void onTopChange(int top);
    }

}
