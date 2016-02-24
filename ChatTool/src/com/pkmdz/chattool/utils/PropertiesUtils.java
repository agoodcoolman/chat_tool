package com.pkmdz.chattool.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.util.Log;

/** 
 * @author  ���� E-mail:agoodcoolman 
 * @date ����ʱ�䣺2016��1��21�� ����10:09:48
 * @version 1.0 
 * @Description: �����йصĹ�����
 * @parameter  
 * @since   
 * @return  
 */
public class PropertiesUtils {

	/**
	 * ��õ�ǰraw�ļ����µ�server.properties������
	 * @param context
	 * @return
	 */
	public static Properties getRawProperties(Context context) {
		int identifier = context.getResources().getIdentifier("server", "raw", context.getPackageName());
		InputStream resourceInputStream = context.getResources().openRawResource(identifier);
		
		Properties properties = new Properties();
		try {
			properties.load(resourceInputStream);
			Log.i("log", properties.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return properties;
	}
}
