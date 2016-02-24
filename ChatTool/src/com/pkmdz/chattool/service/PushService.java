package com.pkmdz.chattool.service;

import io.vov.vitamio.utils.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


public class PushService extends Service {

	private Timer timer;
	private SucessReceiveLisener sucessReceiveListener;
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return new UrlBinder();
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		initTimer();
		startTimer();
	}
	
	private void initTimer () {
		timer = new Timer();
		
	}
	private void startTimer() {
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				try {
					URL url = new URL("http://www.baidu.com");
					HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();
					
					if (openConnection.getResponseCode() != 200) {
						return;
					}
					InputStream is = openConnection.getInputStream();
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len = -1;
					while((len = is.read(buffer)) != -1) {
						byteArrayOutputStream.write(buffer, 0, len);
					}
					
					byteArrayOutputStream.flush();
					byteArrayOutputStream.close();
					is.close();
					// 这里判断下读入的数据,是否符合标准
					Log.i("PushServices", byteArrayOutputStream.toString());
					if (sucessReceiveListener != null)
					sucessReceiveListener.onSucess();
				} catch (MalformedURLException e) {
					
					e.printStackTrace();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		};
		
		timer.scheduleAtFixedRate(timerTask, 2000, 5000);
	}
	
	
	private void stopTask() {
		timer.cancel();
		timer.purge();
	}
	
    public class UrlBinder extends Binder {

		public void setSucessListener(SucessReceiveLisener sucessReceiveListener) {
			PushService.this.sucessReceiveListener = sucessReceiveListener;
		}
		
		public PushService getService() {
			return PushService.this;
		}
	}

}
