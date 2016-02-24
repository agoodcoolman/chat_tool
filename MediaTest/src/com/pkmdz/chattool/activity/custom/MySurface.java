package com.pkmdz.chattool.activity.custom;

import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.WindowManager;

/** 
 * @author  作者 E-mail:agoodcoolman 
 * @date 创建时间：2016年1月23日 下午3:07:30
 * @version 1.0 
 * @Description: 展示自己的视频
 * @parameter  
 * @since   
 * @return  
 */
@SuppressLint("NewApi") public class MySurface extends SurfaceView{
	private final String TAG = MySurface.class.getSimpleName();
	private Context context;
	
	public MySurface(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}

	public MySurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
	}

	public MySurface(Context context) {
		super(context);
		init(context);

		// 初始化完毕,直接开始
	}
	
	// 初始化相机
	 public void init(Context context) {
		// 初始化大小
		this.context = context;
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 初始化设置大小。初始位置
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		@SuppressWarnings("deprecation")
		int height = (int) (wm.getDefaultDisplay().getHeight()*0.36);
		@SuppressWarnings("deprecation")
		int width = (int) (wm.getDefaultDisplay().getWidth()*0.4);
				
		setMeasuredDimension(width, height);
	}
}
