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
	// 回话同意与否回复
	private String st1 = "http://pvm.com/index.php?s=/admin/video/viAnswer&vcid=101&answer=yes";
	// 获取当前在线人数数据
	private String roster = "http://pvm.com/index.php?s=/admin/video/ajaxGetOnliner.html&t=1456365368487";
	// 回话维护
	private String st3 = "http://pvm.com/index.php?s=/admin/video/vcMaintain&t=14563653830068";
	// 轮询是否有找自己的回话
	private String query = "http://pvm.com/index.php?s=/admin/video/voCallmePoll.html";
	
	private String login = "http://";
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
		startTimer();
	}
	
	private void initTimer () {
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
						StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.baidu.com", new Response.Listener<String>() {

							@Override
							public void onResponse(String response) {
//								System.out.println(response);
								
								// 这里判断下读入的数据,是否符合标准
								LoggerUtils.i("PushServices"+ response);
								// 判断下是否要视频
								if (sucessReceiveListener != null)
									sucessReceiveListener.receiveVideoCall();
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
//								System.out.println("sorry,Error");
								
							}
						});
						stringRequest.getHeaders().put("Cookie", cookie);
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

		public void setSucessListener(onSucessReceiveLisener sucessReceiveListener) {
			PushService.this.sucessReceiveListener = sucessReceiveListener;
		}
		
		public PushService getService() {
			LoggerUtils.i("getService()...");
			return PushService.this;
		}
		
		public void stopTask() {
			LoggerUtils.i("stopTask()...");
//			stopTask();
		}
		
		public void startTask() {
			LoggerUtils.i("startTask()...");
			startTimer();
		}
		
		public void beCalledVideo() {
			LoggerUtils.i("beCalledVideo()...");
			// 被人呼叫
		}
		
		public void isAllowVideo(boolean isAllow) {
			LoggerUtils.i("isAllowVideo()...");
			// 是否同意视频
			
		}
		
		// 获取用户列表
		public void getUserRoster() {
			try {
				StringRequest stringRequest = new StringRequest(Request.Method.GET, roster, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
//						System.out.println(response);
						// 判断下数据是否正确
						// 这里判断下读入的数据,是否符合标准
						LoggerUtils.i("PushServices"+ response);
						if (sucessReceiveListener != null)
							sucessReceiveListener.receiveRoster(response);
						
						
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
//						System.out.println("sorry,Error");
						
					}
				});
				stringRequest.getHeaders().put("Cookie", cookie);
				stringRequest.setShouldCache(false);
				requestQueue.add(stringRequest);
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
				
		
		// 登录
		public void login(String acount, String password) {
//			SparseArray<E> sparseArray = new SparseArray<E>();
			HashMap<String, String> Map = new LinkedHashMap<String, String>();
			Map.put("username", acount);
			Map.put("password", password);
			final JsonObjectPostRequest jsonObjectPostRequest = new JsonObjectPostRequest(login,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							
							cookie = response.getString("Cookie");
							SharedPreferences share = ShareUtils.getShare(PushService.this);
							ShareUtils.saveCookie(share, cookie);
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
					}
				
			}, 
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						
						
					}
				
			}, Map);
		}
		
	}
    public class JsonObjectPostRequest extends Request<JSONObject> {
        private Map<String, String> mMap;
        private Response.Listener<JSONObject> mListener;
        public String cookieFromResponse;
        private String mHeader;
        private Map<String, String> sendHeader=new HashMap<String, String>(1);
        public JsonObjectPostRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map map) {
            super(Request.Method.POST, url, errorListener);
            mListener = listener;
            mMap = map;
        }
      
        //当http请求是post时，则需要该使用该函数设置往里面添加的键值对
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return mMap;
        }
     
        @Override
        protected void deliverResponse(JSONObject response) {
            mListener.onResponse(response);
        }
      
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return sendHeader;
        }
        public void setSendCookie(String cookie){
            sendHeader.put("Cookie",cookie);
        }

		@Override
		protected Response<JSONObject> parseNetworkResponse(
				NetworkResponse response) {
			try {
                String jsonString =
                        new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                mHeader = response.headers.toString();
                LoggerUtils.w("get headers in parseNetworkResponse "+response.headers.toString());
                //使用正则表达式从reponse的头中提取cookie内容的子串
                Pattern pattern=Pattern.compile("Set-Cookie.*?;");
                Matcher m=pattern.matcher(mHeader);
                if(m.find()){
                    cookieFromResponse =m.group();
                    LoggerUtils.w("cookie from server "+ cookieFromResponse);
                }
                //去掉cookie末尾的分号
                cookieFromResponse = cookieFromResponse.substring(11,cookieFromResponse.length()-1);
                LoggerUtils.w("cookie substring "+ cookieFromResponse);
                //将cookie字符串添加到jsonObject中，该jsonObject会被deliverResponse递交，调用请求时则能在onResponse中得到
                JSONObject jsonObject = new JSONObject(jsonString);
                jsonObject.put("Cookie",cookieFromResponse);
                LoggerUtils.w("jsonObject "+ jsonObject.toString());
                return Response.success(jsonObject,
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
		}
    }
}
