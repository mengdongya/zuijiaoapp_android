package com.zuijiao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * used in banquet detail activity
 * Created by xiaqibo on 2015/6/10.
 */
public class BanquetDetailScrollView extends ScrollView {

    private ScrollStateChangeListener scrollStateListener;
    private boolean bRecord = false;

    public BanquetDetailScrollView(Context context) {
        super(context);
    }

    public BanquetDetailScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * change the banquet detail toolbar
     *
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (bRecord)
            scrollStateListener.onScroll(oldt - t);
        bRecord = false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            bRecord = true;
        }
        return super.onTouchEvent(ev);
    }

    public void setScrollStateListener(ScrollStateChangeListener listener) {
        this.scrollStateListener = listener;
    }

    public interface ScrollStateChangeListener {
        public void onScroll(int gapScrollY);
    }
}
