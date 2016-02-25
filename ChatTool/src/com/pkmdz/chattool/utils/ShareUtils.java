package com.pkmdz.chattool.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareUtils {
	public static final String SHARE_NAME = "config";
	public static SharedPreferences getShare(Context context) {
		return context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
	}
	
	public static void saveCookie(SharedPreferences pre, String cookie) {
		pre.edit().putString("Cookie", cookie).commit();
	}
	
	public static String getCookie(SharedPreferences pre) {
		return pre.getString("Cookie", "");
	}
}
