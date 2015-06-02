package com.zuijiao.view;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.zuijiao.android.zuijiao.R;

public class MeasuredTextView extends TextView {
    private Context context = null;
    private TextPaint mPaint = null;
    private static int screenW = -1;

    public MeasuredTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mPaint = this.getPaint();
        mPaint.setTextSize(context.getResources().getDimension(R.dimen.comment_content_text_size));
        mPaint.setTextSize(context.getResources().getColor(R.color.comment_content_color));
    }

    public MeasuredTextView(Context context) {
        super(context);
        this.context = context;
        mPaint = this.getPaint();
        mPaint.setTextSize(context.getResources().getDimension(R.dimen.comment_content_text_size));
        mPaint.setTextSize(context.getResources().getColor(R.color.comment_content_color));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Layout layout = getLayout();
        if (layout != null) {
            int height = (int) FloatMath.ceil(getMaxLineHeight(this.getText()
                    .toString()))
                    + getCompoundPaddingTop()
                    + getCompoundPaddingBottom() + 5;
            int width = getMeasuredWidth();
            setMeasuredDimension(width, height);
        }
    }

    private float getMaxLineHeight(String str) {
        float height = 0.0f;
        if (screenW == -1)
            screenW = ((Activity) context).getWindowManager()
                    .getDefaultDisplay().getWidth();
        float paddingLeft = ((RelativeLayout) this.getParent()).getPaddingLeft();
        float paddingReft = ((RelativeLayout) this.getParent()).getPaddingRight();
        int line = (int) Math.ceil((this.getPaint().measureText(str) / (screenW
                - paddingLeft - paddingReft)));
        height = (mPaint.getFontMetrics().descent - mPaint
                .getFontMetrics().ascent) * line;
        return height;
    }
}
