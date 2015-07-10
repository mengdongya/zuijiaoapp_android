package com.zuijiao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * display customized text-view group
 */
public class WordWrapView extends ViewGroup {
    private static final int PADDING_HOR = 11;
    private static final int PADDING_VERTICAL = 0;
    private static final int SIDE_MARGIN = 10;
    private static final int TEXT_MARGIN = 20;

    /**
     * @param context
     */
    public WordWrapView(Context context) {
        super(context);
    }

    public WordWrapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param context
     * @param attrs
     */
    public WordWrapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int autualWidth = r - l;
        int x = SIDE_MARGIN;
        int y = 0;
        int rows = 1;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            x += width + TEXT_MARGIN;
            if (x > autualWidth) {
                x = width + SIDE_MARGIN + TEXT_MARGIN;
                rows++;
            }
            y = rows * (height + TEXT_MARGIN) - TEXT_MARGIN;
            view.layout(x - width, y - height, x, y);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x = 0;
        int y = 0;
        int rows = 1;
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int actualWidth = specWidth - SIDE_MARGIN * 2;
        int childCount = getChildCount();
        int paddingLeft = 20, paddingRight = 14, paddingTop = 5, paddingBottom = 5, height1 = 24;
        final float scale = getResources().getDisplayMetrics().density;
        paddingLeft = (int) (paddingLeft * scale + 0.5f);
        paddingRight = (int) (paddingRight * scale + 0.5f);
        paddingTop = (int) (paddingTop * scale + 0.5f);
        paddingBottom = (int) (paddingBottom * scale + 0.5f);
        height1 = (int) (height1 * scale + 0.5f);
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            child.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            x += width + TEXT_MARGIN;
            if (x > actualWidth) {
                x = width;
                rows++;
            }
            y = rows * (height + TEXT_MARGIN) - TEXT_MARGIN;
        }
        setMeasuredDimension(actualWidth, y);
    }

}