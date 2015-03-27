package com.zuijiao.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class LabelContainer extends ViewGroup {
	private Context mContext = null;

	public LabelContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public LabelContainer(Context context) {
		super(context);
		this.mContext = context;
	}

	private final static String TAG = "MyViewGroup";

	private final static int VIEW_MARGIN = 30;
	private Canvas mCanvas = null;
	private int heightMeasured = 0 ;

	int height = 0 ;
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG, "widthMeasureSpec = " + widthMeasureSpec
				+ " heightMeasureSpec" + heightMeasureSpec);
		for (int index = 0; index < getChildCount(); index++) {
			final View child = getChildAt(index);
			// measure
			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			System.out.println("child height = " + child.getHeight());
			height += child.getHeight() ;
		}
		setMeasuredDimension(widthMeasureSpec ,height*11/20);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.d(TAG, "changed = " + changed + " left = " + l + " top = " + t
				+ " right = " + r + " botom = " + b);
		final int count = getChildCount();
		int row = 0;// which row lay you view relative to parent
		int lengthX = l; // right position of child relative to parent
		int lengthY = t; // bottom position of child relative to parent
		int padding = 20;
		for (int i = 0; i < count; i++) {

			final View child = this.getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			lengthX += width + VIEW_MARGIN;
			lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height ;
			// if it can't drawing on a same line , skip to next line
			if (lengthX > r) {
				lengthX = width + VIEW_MARGIN + l;
				row++;
				lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height
						;

			}
			child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
		}
	};


	@Override
	public void addView(View child) {
		super.addView(child);
	}
}
