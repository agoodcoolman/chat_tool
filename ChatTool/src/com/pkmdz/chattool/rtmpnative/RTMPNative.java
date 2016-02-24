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
	
	public native void close();
	
	public native void sendAudioData(byte [] buffer, int length, int tick);
	
	public native void putIntoQueue(int type, byte [] buffer, int length);
	
	public native void setSPSAndPPS(byte [] sps, int spsLen,  byte [] pps, int ppsLen);
	
	static{
		System.loadLibrary("rtmp");
		System.loadLibrary("RTMPSender");
	}
	
	private RTMPNative(){};
	
	public final static RTMPNative getInstance(){
		if(rInstance == null){
			synchronized (RTMPNative.class) {
				if(rInstance == null){
					RTMPNative.rInstance = new RTMPNative();
				}
			}
		}
		return rInstance;
	}
	
	
}
