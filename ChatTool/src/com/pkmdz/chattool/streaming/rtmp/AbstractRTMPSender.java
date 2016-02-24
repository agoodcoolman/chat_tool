package com.pkmdz.chattool.streaming.rtmp;

import com.pkmdz.chattool.rtmpnative.RTMPNative;

public abstract class AbstractRTMPSender {
	protected MediaCodecInputStream is;
	protected RTMPNative mRTMPNative;
	
	public AbstractRTMPSender() {
		mRTMPNative = RTMPNative.getInstance();
	}
	
	public void setInputStream(MediaCodecInputStream is){
		this.is = is;
	}
	
	public abstract void start();
	
	public abstract void stop();
}
