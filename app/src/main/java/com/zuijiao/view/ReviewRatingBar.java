package com.zuijiao.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import net.zuijiao.android.zuijiao.R;

/**
 * Created by yitianhao on 2015/7/10.
 */
public class ReviewRatingBar extends LinearLayout {
    private RatingBar ratingBar;
    private Context mContext = null;
    private int imageSize;
    private int ratingBarType;
    private boolean isIndicator;


    public ReviewRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setCustomAttributes(attrs);
        initView(context);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.ReviewRatingBar);
        imageSize = a.getInteger(R.styleable.ReviewRatingBar_imageSize, 0);
        isIndicator = a.getBoolean(R.styleable.ReviewRatingBar_isIndicator, true);
        ratingBarType = a.getInt(R.styleable.ReviewRatingBar_ratingBarType, 0);
    }

    private void initView(Context context) {
        View view = null;

        switch (ratingBarType) {
            case 1:
                view = LayoutInflater.from(context).inflate(
                        R.layout.review_ratingbar, null);
                break;
            case 2:
                view = LayoutInflater.from(context).inflate(
                        R.layout.review_ratingbar_large, null);
                break;
            case 3:
                view = LayoutInflater.from(context).inflate(
                        R.layout.review_ratingbar_mid, null);
                break;
            default:
                view = LayoutInflater.from(context).inflate(
                        R.layout.review_ratingbar, null);

                break;
        }
        ratingBar = (RatingBar) view.findViewById(R.id.review_ratingbar);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ratingBar
                .getLayoutParams();
        // 获取屏幕密度
        float scale = context.getResources().getDisplayMetrics().density;
        // 计算高度
//        linearParams.height = (int) (imageSize * scale + 0.5f);
        linearParams.height = (int) (imageSize * scale );
        ratingBar.setLayoutParams(linearParams);
        ratingBar.setIsIndicator(isIndicator);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setRating(2);
        addView(view);
    }

    /**
     * 设置星星改变事件
     *
     * @param listener
     */
    public void setOnRatingBarChangeListener(RatingBar.OnRatingBarChangeListener listener) {
        ratingBar.setOnRatingBarChangeListener(listener);
    }

    /**
     * 设置星星显示数量
     *
     * @param num
     */
    public void setRating(float num) {
        ratingBar.setRating(num);
    }

    /**
     * 设置星星显示数量
     *
     * @param num
     */
    public void setRating(int num) {
        ratingBar.setRating(num);
    }

    public void setNumStars(int num) {
        ratingBar.setNumStars(num);
    }

    /**
     * 获得星星显示数量
     *
     * @return
     */
    public float getRating() {
        return ratingBar.getRating();
    }

    /**
     * 设置是否能触摸改变
     *
     * @param isIndicator
     */
    public void setIsIndicator(boolean isIndicator) {
        ratingBar.setIsIndicator(isIndicator);
    }

    public void setStepSize(float f) {
        ratingBar.setStepSize(f);
    }
}
