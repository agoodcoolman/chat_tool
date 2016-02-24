package com.example.front;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
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
		LayoutParams layoutParams = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		/*
		 * android.view.WindowManager.LayoutParams layoutParams2 = new
		 * WindowManager.LayoutParams(1, 1,
		 * WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA,
		 * WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.RGB_888);
		 * 
		 * getWindowManager().updateViewLayout(bit, layoutParams2);
		 */

		// View peekDecorView = getWindow().peekDecorView();

		View decorView = getWindow().getDecorView();
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
		p.height = (int) 1; // 高度设置为屏幕的1.0
		p.width = (int) 1; // 宽度设置为屏幕的0.8
		p.alpha = 0.6f; // 设置本身透明度
		p.dimAmount = 0.0f; // 设置黑暗度

		p.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 系统提示类型,重要
		p.format = 1;
		p.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
		p.flags = p.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		p.flags = p.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制
		getWindow().setAttributes(p); // 设置生效
		getWindow().setGravity(Gravity.LEFT); // 设置靠右对齐

		// 以屏幕左上角为原点，设置x、y初始值
		p.x = 0;
		p.y = 0;

		// 设置悬浮窗口长宽数据
		p.width = 140;
		p.height = 140;

		// 显示myFloatView图像
		m.addView(bit, p);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 所有事件不处理.
		return true;
	}

}
