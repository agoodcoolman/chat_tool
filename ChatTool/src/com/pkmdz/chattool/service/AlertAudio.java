package com.pkmdz.chattool.service;

import android.R.plurals;
import android.content.Context;
import android.media.MediaPlayer;

/**
 * 各种声音提示
 * @author Administrator
 *
 */
public class AlertAudio {
	private static MediaPlayer create;
	public static boolean isPlay = false;
	public static void alartVideoAudio(final Context con, final int resource) {
		if (isPlay)
			return;
		new Thread(){
			@Override
			public void run() {
				super.run();
				
				create = MediaPlayer.create(con, resource);
				create.start();
				create.setLooping(true);
				isPlay = true;
			}
		}.start();
	}
	
	public static void stopAudio() {
		isPlay = false;
		if (create != null)
		create.stop();
	}
}
