package com.pkmdz.chattool;

import java.util.ArrayList;

import io.vov.vitamio.utils.Log;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmdz.chattool.activity.ChatActivity;
import com.pkmdz.chattool.service.AlertAudio;
import com.pkmdz.chattool.service.PushService;
import com.pkmdz.chattool.service.PushService.UrlBinder;
import com.pkmdz.chattool.service.onSucessReceiveLisener;
import com.pkmdz.chattool.service.modle.User;
import com.pkmdz.chattool.utils.JsonUtils;
import com.pkmdz.chattool.utils.LoggerUtils;
// 展示用户
@SuppressLint("NewApi")
public class MainActivity extends ListActivity implements onSucessReceiveLisener {
	private AlertDialog dialog;
	private PushService.UrlBinder Binders;
	private ServiceConnection connect = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Binders = ((PushService.UrlBinder)service);
			Binders.setSucessListener(MainActivity.this);
			// 查看是否有轮询
			Binders.startLoopQueryVideo();
			Binders.getUserRoster();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        String[] arr = { "java", "c/c++", "python", "ruby" };  
        // 获取当前在线人数
        /*ArrayList<User> arrayList = new ArrayList<User>();
		arrayList.add(new User());
		arrayList.add(new User());*/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  
                R.layout.activity_main_roster_list_item, R.id.text_01, arr);  
		// 显示用户列表
		setListAdapter(adapter);
		
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	startMyService();
    }
    
 // 启动轮询服务
    private void startMyService() {
    	Intent intent = new Intent(this, PushService.class);
    	
    	bindService(intent, connect, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
    	super.onStop();
    	this.unbindService(connect);  
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
		LoggerUtils.i("用户列表"+ roster);
		
//		ArrayList<User> arrayList = new ArrayList<User>();
//		arrayList.add(new User());
//		arrayList.add(new User());
//		// 显示用户列表
//		setListAdapter(new MyAdapter(arrayList));
		
	}
	
	class MyAdapter extends BaseAdapter {
		private ArrayList<User> arrayList;
		MyAdapter(ArrayList<User> arrayList) {
			this.arrayList = arrayList;
		}
		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return Long.parseLong(arrayList.get(position).getUid());
		}

		@Override
		public boolean hasStableIds() {
			
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.activity_main_roster_list_item, null);
				holder = new ViewHolder();
				holder.textView = (TextView) convertView.findViewById(R.id.text_01);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText("大道理");
			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			// 所有的类型都是一样的
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// view的类型都是同一个
			return 1;
		}

		@Override
		public boolean isEmpty() {
			
			return false;
		}

		@Override
		public boolean areAllItemsEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	static class ViewHolder {
		TextView textView ;
	}

}
