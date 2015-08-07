package com.zuijiao.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import net.zuijiao.android.zuijiao.PictureFragment;

public class MyViewPager extends ViewPager {

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        try {
//            return super.onInterceptTouchEvent(ev);
//        } catch (IllegalArgumentException e) {
//            return false;
//        } catch (ArrayIndexOutOfBoundsException e) {
//            return false;
//        }
////        return true;
//    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        getParent().requestDisallowInterceptTouchEvent(true);
//        return super.onInterceptTouchEvent(ev);
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        getParent().requestDisallowInterceptTouchEvent(true);
//        return super.dispatchTouchEvent(ev);
//    }
}
