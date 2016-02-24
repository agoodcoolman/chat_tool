package com.pkmdz.chattool.rtmpnative;

import java.util.concurrent.Semaphore;

import android.util.Log;

public class RTMPNative {
	
	public static boolean isRTMPInit = false;
	
	public static Semaphore semp = new Semaphore(1);
	
    private int mRTMPSender;
    
    private static volatile RTMPNative rInstance = null;
	
	public native void rtmpInit();
	
	public native void sendVMetaData(byte [] sps, int spsLen,  byte [] pps, int ppsLen);
	
	public native void sendVideoData(byte [] buffer, int len,int tick);
	
	public native void sendAMetaData();
	
	public native void sendAudioData(byte [] buffer, int length, int tick);
	
	static{
		System.loadLibrary("rtmp");
		System.loadLibrary("RTMPSender");
	}
	
	private RTMPNative(){};
	
	public final static RTMPNative getInstance(){
		Log.e("TAG", "begin rInstance 22222222222");
		if(rInstance == null){
			synchronized (RTMPNative.class) {
				if(rInstance == null){
					Log.e("TAG", "new rInstance 1111111111111");
					RTMPNative.rInstance = new RTMPNative();
				}
			}
		}
		Log.e("TAG", "-----rInstance------->"+rInstance);
		return rInstance;
	}
	
	
}
