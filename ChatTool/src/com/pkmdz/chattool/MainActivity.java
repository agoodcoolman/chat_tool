package com.pkmdz.chattool;

import io.vov.vitamio.utils.Log;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmdz.chattool.activity.ChatActivity;
import com.pkmdz.chattool.service.AlertAudio;
import com.pkmdz.chattool.service.PushService;
import com.pkmdz.chattool.service.PushService.UrlBinder;
import com.pkmdz.chattool.service.onSucessReceiveLisener;
import com.pkmdz.chattool.utils.JsonUtils;
import com.pkmdz.chattool.utils.LoggerUtils;

@SuppressLint("NewApi")
public class MainActivity extends ListActivity implements onSucessReceiveLisener {
	private AlertDialog dialog;
	private PushService.UrlBinder Binders;
	private ServiceConnection connect = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Binders = ((PushService.UrlBinder)service);
			Binders.setSucessListener(MainActivity.this);

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        startMyService();
    }
    
    // 启动轮询服务
    private void startMyService() {
    	Intent intent = new Intent(this, PushService.class);
    	
    	bindService(intent, connect, BIND_AUTO_CREATE);
    	
    }


	@Override
	public void onSucessReceive(String result) {
		
		LoggerUtils.i("onSucess() ... 请求成功");
		Binders.stopTask();
		AlertAudio.alartVideoAudio(this, R.raw.phonering);
	}

	@Override
	public void receiveVideoCall() {
		if (dialog != null && dialog.isShowing())
			return;
		dialog = new AlertDialog.Builder(this).setTitle("是否接受视频请求")
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				AlertAudio.stopAudio();
			}
		})
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				AlertAudio.stopAudio();
				// TODO 去视频页面
			}
			
		})
		.setCancelable(false)
		.create();
		dialog.show();
		
	}
	
	public void callVideo() {
		
	}

	@Override
	public void receiveRoster(String roster) {
		// TODO Auto-generated method stub
		// 显示用户列表
	}

}
