package com.pkmdz.chattool.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.WindowManager;

/** 
 * @author  ���� E-mail:agoodcoolman 
 * @date ����ʱ�䣺2016��1��23�� ����3:07:30
 * @version 1.0 
 * @Description: չʾ�Լ�����Ƶ
 * @parameter  
 * @since   
 * @return  
 */
@SuppressLint("NewApi") public class MySurface extends SurfaceView{
	private final String TAG = MySurface.class.getSimpleName();
	private Camera camera;
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

		// ��ʼ�����,ֱ�ӿ�ʼ
	}
	
	// ��ʼ�����
	 public void init(Context context) {
		// ��ʼ����С
		this.context = context;
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// ��ʼ�����ô�С����ʼλ��
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		@SuppressWarnings("deprecation")
		int height = (int) (wm.getDefaultDisplay().getHeight()*0.36);
		@SuppressWarnings("deprecation")
		int width = (int) (wm.getDefaultDisplay().getWidth()*0.4);
				
		setMeasuredDimension(width, height);
	}
}
