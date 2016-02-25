package com.pkmdz.chattool;

import io.vov.vitamio.utils.Log;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmdz.chattool.activity.ChatActivity;
import com.pkmdz.chattool.service.PushService;
import com.pkmdz.chattool.service.PushService.UrlBinder;
import com.pkmdz.chattool.service.SucessReceiveLisener;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity implements SucessReceiveLisener {
	private ServiceConnection connect = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			((PushService.UrlBinder)service).setSucessListener(MainActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        startMyService();
    }
    
    
    private void startMyService() {
    	Intent intent = new Intent(this, PushService.class);
    	
    	bindService(intent, connect, BIND_AUTO_CREATE);
    	
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @SuppressLint("NewApi")
		@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView te = (TextView)rootView.findViewById(R.id.text);
            te.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					startActivity(new Intent(getActivity(), ChatActivity.class));
				}
			});
            
            return rootView;
        }
    }

	@Override
	public void onSucess() {
//		Toast.makeText(this, "请求成功", 0).show();
		Log.i("MainActivity", "sa");
	}

}
