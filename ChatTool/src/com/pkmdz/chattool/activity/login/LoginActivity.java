package com.pkmdz.chattool.activity.login;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.pkmdz.chattool.MainActivity;
import com.pkmdz.chattool.R;
import com.pkmdz.chattool.service.modle.Receive;
import com.pkmdz.chattool.service.modle.User;
import com.pkmdz.chattool.utils.InterfaceUtils;
import com.pkmdz.chattool.utils.JsonUtils;
import com.pkmdz.chattool.utils.LoggerUtils;
import com.pkmdz.chattool.utils.ShareUtils;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private EditText username;
	private EditText userpassword;
	private CheckBox remember;
	private CheckBox autologin;
	private Button login;
	private SharedPreferences sp;
	private String userNameValue, passwordValue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		username = (EditText) findViewById(R.id.username);
		userpassword = (EditText) findViewById(R.id.userpassword);
		remember = (CheckBox) findViewById(R.id.remember);
		autologin = (CheckBox) findViewById(R.id.autologin);
		login = (Button) findViewById(R.id.login);

		sp = getSharedPreferences("userInfo", 0);
		String name = sp.getString("USER_NAME", "");
		String pass = sp.getString("PASSWORD", "");

		boolean choseRemember = sp.getBoolean("remember", false);
		boolean choseAutoLogin = sp.getBoolean("autologin", false);
		// Toast.makeText(this, name, Toast.LENGTH_SHORT).show();

		if (choseRemember) {
			username.setText(name);
			userpassword.setText(pass);
			remember.setChecked(true);
		}

		if (choseAutoLogin) {
			autologin.setChecked(true);
		}

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				userNameValue = username.getText().toString();
				passwordValue = userpassword.getText().toString();
			
				login(userNameValue, passwordValue);
				
			}

		});

	}
	
	// 登录
	public void login(final String acount, final String password) {

		HashMap<String, String> Map = new LinkedHashMap<String, String>();
		Map.put("username", acount);
		Map.put("password", password);
		
		final JsonObjectPostRequest jsonObjectPostRequest = new JsonObjectPostRequest(InterfaceUtils.LOGIN_URL,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							String cookie = response.getString("Cookie");
							String[] split = cookie.split("=");
							cookie = split[1];
							SharedPreferences share = ShareUtils.getShare(LoginActivity.this);
							ShareUtils.saveCookie(share, cookie);
							SharedPreferences.Editor editor = ShareUtils.getShare(getApplicationContext()).edit();
							
							// 保存账号密码
							editor.putString("username", acount);
							editor.putString("password", password);
							
							// 保留状态
							if (remember.isChecked()) {
								editor.putBoolean("remember", true);
							} else {
								editor.putBoolean("remember", false);
							}

							// 是否记住密码
							if (autologin.isChecked()) {
								editor.putBoolean("autologin", true);
							} else {
								editor.putBoolean("autologin", false);
							}
							editor.commit();
							TypeToken<Receive> typeToken = new TypeToken<Receive>(){};
							Receive fromJson = JsonUtils.fromJson(response.toString(), typeToken);
							Toast.makeText(LoginActivity.this, fromJson.getLogin_hint(), Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
							startActivity(intent);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				
			}, 
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT)
						.show();
						
					}
				
			}, Map);
		
		RequestQueue newRequestQueue = Volley.newRequestQueue(getApplicationContext());
		newRequestQueue.add(jsonObjectPostRequest);
		newRequestQueue.start();
	}
	
}
 class JsonObjectPostRequest extends Request<JSONObject> {
    private Map<String, String> mMap;
    private Response.Listener<JSONObject> mListener;
    public String cookieFromResponse;
    private String mHeader;
    private Map<String, String> sendHeader=new HashMap<String, String>(1);
    public JsonObjectPostRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, String> map) {
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
