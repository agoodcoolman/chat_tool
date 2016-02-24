package com.pkmdz.chattool.activity.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/** 
 * @author  作者 E-mail:agoodcoolman 
 * @date 创建时间：2016年1月24日 上午10:11:03
 * @version 1.0 
 * @Description: TODO
 * @parameter  
 * @since   
 * @return  
 */
public class DragerFramelayout extends FrameLayout {
	private ViewDragHelper mDragger;
	public DragerFramelayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public DragerFramelayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DragerFramelayout(Context context) {
		super(context);
		init();
	}
	
	void init() {
		mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
			
			@Override
			public boolean tryCaptureView(View arg0, int arg1) {
				
				return true;
			}
			 @Override
            public int clampViewPositionHorizontal(View child, int left, int dx)
            {
				// 边界控制
                final int leftBound = getPaddingLeft();
                final int rightBound = getWidth() - child.getWidth() - leftBound;

                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);

                return newLeft;
            }
			 
			@Override
			public int clampViewPositionVertical(View child, int top, int dy) {
				final int paddingTop = getPaddingTop();
				final int topBound = getHeight() - paddingTop - child.getHeight();
				final int newTop = Math.min(Math.max(top, paddingTop), topBound);
				return newTop;
			}
			 
			@SuppressLint("NewApi") @Override
			public void onViewPositionChanged(View changedView, int left,
					int top, int dx, int dy) {
				
				super.onViewPositionChanged(changedView, left, top, dx, dy);
				
				changedView.setLeft(left);
				changedView.setTop(top);
				invalidate();
			}
			
		});
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mDragger.shouldInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDragger.processTouchEvent(event);
		return true;
	}

}
