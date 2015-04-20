package com.zuijiao.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import net.zuijiao.android.zuijiao.R;

/**
 * Created by xiaqibo on 2015/4/16.
 */
public class MyEditText2 extends EditText {


    private int mFocusedColor = Color.BLACK;
    private int mUnFocusedColor = Color.BLACK;
    private Context mContext = null;

    public MyEditText2(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.mContext = context;
        // setCustomAttributes(attrs) ;

    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.roundedimageview);
        mFocusedColor = a.getColor(R.styleable.edittext_focused_color, Color.BLACK);
        mUnFocusedColor = a.getColor(R.styleable.edittext_unfocused_color, Color.BLACK);
    }

    @Override

    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        if (this.hasFocus()) {
            paint.setColor(Color.parseColor("#f36c60"));
        } else {
            paint.setColor(Color.parseColor("#959595"));
        }
//        if(this.isFocused() == true)
//
//            paint.setColor(Color.parseColor("#343434"));
//
//        else
//
//            paint.setColor(Color.rgb(0,0,0));

        canvas.drawLine(this.getLeft(), this.getBottom() - 2, this.getRight(), this.getBottom(), paint);
        super.onDraw(canvas);

    }
}