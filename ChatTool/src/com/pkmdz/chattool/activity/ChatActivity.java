package com.pkmdz.chattool.activity;

import com.pkmdz.chattool.R;
import com.pkmdz.chattool.Session;
import com.pkmdz.chattool.SessionBuilder;
import com.pkmdz.chattool.streaming.audio.AudioQuality;
import com.pkmdz.chattool.streaming.video.VideoQuality;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

/**
 * A straightforward example of how to stream AMR and H.263 to some public IP using libstreaming.
 * Note that this example may not be using the latest version of libstreaming !
 */
public class ChatActivity extends Activity implements OnClickListener, Session.Callback, SurfaceHolder.Callback {

	private final static String TAG = "MainActivity";

	private Button mButton1, mButton2;
	private SurfaceView mSurfaceView;
	private EditText mEditText;
	private Session mSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		setContentView(R.layout.activity_chat);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Vitamio.isInitialized(this);
		mButton1 = (Button) findViewById(R.id.button1);
		mButton2 = (Button) findViewById(R.id.button2);
		mSurfaceView = (SurfaceView) findViewById(R.id.mysurface);
		mSurfaceView.setZOrderOnTop(true);
		mEditText = (EditText) findViewById(R.id.editText1);

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				mSession = SessionBuilder.getInstance()
						.setCallback(ChatActivity.this)
						.setSurfaceView(mSurfaceView)
						.setPreviewOrientation(90)
						.setContext(getApplicationContext())
						.setAudioEncoder(SessionBuilder.AUDIO_AAC)
						.setAudioQuality(new AudioQuality(44100, 32000))
						.setVideoEncoder(SessionBuilder.VIDEO_H264)
						.setVideoQuality(new VideoQuality(320,240,20,500000))
						.build();

				mButton1.setOnClickListener(ChatActivity.this);
				mButton2.setOnClickListener(ChatActivity.this);
				mButton1.setEnabled(false);
				mButton2.setEnabled(false);
				playfunction();
				
				if (!mSession.isStreaming()) {
					mSession.configure();
				} else { 
					mSession.stop();
				}

				mSurfaceView.getHolder().addCallback(ChatActivity.this);
				
			}
		}).start();
		
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mSession == null)
			return;
		if (mSession.isStreaming()) {
			mButton1.setText("停止");
		} else {
			mButton1.setText("开始");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mSession.release();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button1) {
			// Starts/stops streaming
			mSession.setDestination(mEditText.getText().toString());
			if (!mSession.isStreaming()) {
				mSession.configure();
			} else {
				mSession.stop();
			}
			mButton1.setEnabled(false);
		} else {
			// Switch between the two cameras
			mSession.switchCamera();
		}
	}

	/*
	@Override
	public void onBitrateUpdate(long bitrate) {
		Log.d(TAG,"Bitrate: "+bitrate);
	}*/

	@Override
	public void onSessionError(int message, int streamType, Exception e) {
		mButton1.setEnabled(true);
		if (e != null) {
			logError(e.getMessage());
		}
	}

	@Override
	
	public void onPreviewStarted() {
		Log.d(TAG,"Preview started.");
	}

	@Override
	public void onSessionConfigured() {
		Log.d(TAG,"Preview configured.");
		// Once the stream is configured, you can get a SDP formated session description
		// that you can send to the receiver of the stream.
		// For example, to receive the stream in VLC, store the session description in a .sdp file
		// and open it with VLC while streming.
		//Log.d(TAG, mSession.getSessionDescription());
		mSession.start();
	}

	@Override
	public void onSessionStarted() {
		Log.d(TAG,"Session started.");
		mButton1.setEnabled(true);
		mButton1.setText("停止");
	}

	@Override
	public void onSessionStopped() {
		Log.d(TAG,"Session stopped.");
		mButton1.setEnabled(true);
		mButton1.setText("开始");
	}	
	
	/** Displays a popup to report the eror to the user */
	private void logError(final String msg) {
		final String error = (msg == null) ? "Error unknown" : msg; 
		AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
		builder.setMessage(error).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSession.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSession.stop();
	}

	@Override
	public void onBitrareUpdate(long bitrate) {
		Log.d(TAG,"Bitrate: "+bitrate);
	}
	
	/**
	 * 播放方法
	 */
	void playfunction(){
		 String path = "";
		 VideoView mVideoView;
		 EditText mEditText;
		 
		//if (!LibsChecker.checkVitamioLibs(this))
		//	    return;
		
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		
		//path = "rtmp://139.196.175.109/oflaDemo/streams";
		//path = "rtmp://120.27.118.186/live/mycamera";
		//path = "rtmp://139.196.175.109/oflaDemo/Avengers2.mp4";
		path = "rtmp://120.27.118.186/live/streams2016";
		
		// https://139.196.175.109/svn/RTMPProject/trunk
		// https://139.196.175.109/svn/RTMPProject/branches
		/*
		 * Alternatively,for streaming media you can use
		 * mVideoView.setVideoURI(Uri.parse(URLstring));
		 */
		mVideoView.setVideoPath(path);
//		mVideoView.setMediaController(new MediaController(this));// 控制视频的工具
		mVideoView.requestFocus();
		mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);// quality 参见MediaPlayer的常量：VIDEOQUALITY_LOW（流畅）、VIDEOQUALITY_MEDIUM（普通）、VIDEOQUALITY_HIGH（高质）
		mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, mVideoView.getVideoAspectRatio());
//		mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_ORIGIN, aspectRatio)
		
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				// optional need Vitamio 4.0
				mediaPlayer.setPlaybackSpeed(1.0f);
			}
		});
		
		// 设置错误
		mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Toast.makeText(ChatActivity.this, "异常", Toast.LENGTH_LONG).show();
				return false;
			}
		});
	}

}
