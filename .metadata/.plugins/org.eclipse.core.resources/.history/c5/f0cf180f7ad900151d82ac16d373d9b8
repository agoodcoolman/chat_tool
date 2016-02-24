package com.example.front;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;


public class MainActivity extends Activity {

	@SuppressLint({ "NewApi", "ResourceAsColor" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		Bit bit = new Bit(this);
		/*LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		android.view.WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(1, 1, WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.RGB_888);
		getWindowManager().updateViewLayout(bit, layoutParams2);*/
		
		View peekDecorView = getWindow().peekDecorView();
		peekDecorView.getLayoutParams().height = 1;
		peekDecorView.getLayoutParams().width = 1;
		View decorView = getWindow().getDecorView();
		decorView.setMinimumHeight(1);
		decorView.setMinimumWidth(1);
		decorView.setBackgroundColor(android.R.color.transparent);
		decorView.setBackground(getResources().getDrawable(R.drawable.bit_01));
		decorView.bringToFront();
		decorView.invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 所有事件不处理.
		return true;
	}
	
	
	
}
