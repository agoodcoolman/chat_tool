package com.pkmdz.chattool.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.pkmdz.chattool.service.modle.User;
import com.pkmdz.chattool.utils.InterfaceUtils;
import com.pkmdz.chattool.utils.JsonUtils;
import com.pkmdz.chattool.utils.LoggerUtils;
import com.pkmdz.chattool.utils.ShareUtils;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

public class PushService extends Service {
	public static final String host = "";
	private Timer timer;
	private onSucessReceiveLisener sucessReceiveListener;

	private RequestQueue requestQueue;
	private String cookie = "";

	@Override
	public IBinder onBind(Intent intent) {
		return new UrlBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences share = ShareUtils.getShare(getApplicationContext());
		cookie = ShareUtils.getCookie(share);
		initTimer();

	}

	private void initTimer() {
		timer = new Timer();
		requestQueue = Volley.newRequestQueue(PushService.this);
		requestQueue.start();
	}

	// 轮询是否有视频请求
	private void startTimer() {
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {

				try {
					StringRequest stringRequest = new StringRequest(
							Request.Method.GET, InterfaceUtils.query,
							new Response.Listener<String>() {

								@Override
								public void onResponse(String response) {
									// System.out.println(response);

									// 这里判断下读入的数据,是否符合标准
									LoggerUtils.i("PushServices" + response);
									
									User fromJson = JsonUtils.fromJson(response.toString(), new TypeToken<User>(){});
									// 判断下是否要视频
									if (fromJson != null && sucessReceiveListener != null)
										sucessReceiveListener
												.receiveVideoCall();
								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									// System.out.println("sorry,Error");

								}
							}) {
						public java.util.Map<String,String> getHeaders() throws AuthFailureError {
							LinkedHashMap<String, String> maps = new LinkedHashMap<String, String>();
							maps.put("Cookie", cookie);
							return maps;
						};
					};
					
					
					stringRequest.setShouldCache(false);
					requestQueue.add(stringRequest);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		timer.scheduleAtFixedRate(timerTask, 2000, 10000);
	}

	// 获得当前用户列表
	public void getUsers() {
		
	}

	private void stopTask() {
		timer.cancel();
		timer.purge();
	}

	public class UrlBinder extends Binder {

		public void setSucessListener(
				onSucessReceiveLisener sucessReceiveListener) {
			PushService.this.sucessReceiveListener = sucessReceiveListener;
		}

		public PushService getService() {
			LoggerUtils.i("getService()...");
			return PushService.this;
		}

		public void stopTask() {
			LoggerUtils.i("stopTask()...");
			// stopTask();
		}

		public void startLoopQueryVideo() {
			LoggerUtils.i("startTask()...");
			startTimer();
		}

		public void isAllowVideo(boolean isAllow) {
			LoggerUtils.i("isAllowVideo()...");
			// 是否同意视频

		}

		// 获取用户列表
		public void getUserRoster() {
			try {
				StringRequest stringRequest = new StringRequest(
						Request.Method.GET, InterfaceUtils.roster,
						new Response.Listener<String>() {

							@Override
							public void onResponse(String response) {
								// System.out.println(response);
								// 判断下数据是否正确
								// 这里判断下读入的数据,是否符合标准
								LoggerUtils.i("PushServices" + response);
								if (sucessReceiveListener != null)
									sucessReceiveListener
											.receiveRoster(response);

							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {

							}
						}) {
					@Override
					public Map<String, String> getHeaders()
							throws AuthFailureError {
						LinkedHashMap<String, String> maps = new LinkedHashMap<String, String>();
						maps.put("Cookie", cookie);
						return maps;
					}
				};
				
				stringRequest.setShouldCache(false);
				requestQueue.add(stringRequest);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
