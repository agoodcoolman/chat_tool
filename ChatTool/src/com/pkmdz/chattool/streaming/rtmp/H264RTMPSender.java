package com.pkmdz.chattool.streaming.rtmp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pkmdz.chattool.rtmpnative.RTMPNative;

import android.os.Environment;


public class H264RTMPSender extends AbstractRTMPSender implements Runnable{
	
	private Thread t = null;
	
	private boolean isCardExist = false;
	
	private FileOutputStream mOutStream = null;
	
	//private int mRTMPSender;

	private byte[] sps;

	private byte[] pps;
	
	//private RTMPNative mRTMPNative;
	
	//private native void rtmpInit();
	
	//private native void sendMetaData(byte [] sps, int spsLen,  byte [] pps, int ppsLen);
	
	//private native void sendVideoData(byte [] buffer, int len,int tick);
	
	private int tick = 0;
	
	public H264RTMPSender() {
		super();
	}
	
	
	public void setStreamParameters(byte [] sps,byte [] pps){
		this.sps = sps;
		this.pps = pps;
		mRTMPNative.setSPSAndPPS(sps, sps.length, pps, pps.length);
	}

	@Override
	public void start() {
		//mRTMPNative = RTMPNative.getInstance();
		if(!RTMPNative.isRTMPInit){
			mRTMPNative.rtmpInit();//
			RTMPNative.isRTMPInit = true;
		}
		//mRTMPNative.sendVMetaData(sps, sps.length, pps, pps.length);
		if(Environment.getExternalStorageState().
				equals(Environment.MEDIA_MOUNTED)){
			isCardExist = true;
		}
		if(isCardExist){
			try {
				File path = Environment.getExternalStorageDirectory();
				File file = new File(path, "2016_testII.h264");
				if(file.exists()){
					file.delete();
				}
				mOutStream =new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		if(t == null){
			t = new Thread(this);
			t.start();
		}
	}

	@Override
	public void stop() {
		if (t != null) {
			try {
				/*if(mOutStream !=null){
					mOutStream.close();
				}*/
				is.close();
			} catch (Exception e) {}
			t.interrupt();
			try {
				t.join();
			} catch (InterruptedException e) {}
			t = null;
		}
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				
				byte[] raw_data;
				byte[] buffer = is.read();
				if(buffer == null){
					break;
				}
				if(mOutStream != null){
					mOutStream.write(buffer);
				}
				if(buffer[0] == 0x00 && buffer[1] == 0x00
						&& buffer[2] == 0x00 && buffer[3] == 0x01){
					int length = 0;
					length = buffer.length -4;
					raw_data = new byte[length];
					System.arraycopy(buffer, 4, raw_data, 0, length);
					//RTMPNative.semp.acquire();
					mRTMPNative.putIntoQueue( 1, raw_data, length);
					//mRTMPNative.sendVideoData(raw_data, length, tick);
					//RTMPNative.semp.release();
					//tick += 50;
				}else{
					//mRTMPNative.sendVideoData(buffer, buffer.length, tick);
					//tick += 50;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}//catch (InterruptedException e) {
				//Log.e("TAG", "rrrrrrrrrrrrrrr2016");
				//RTMPNative.semp.release();
				//e.printStackTrace();
			//}
			
				/*try {
					byte [] buffer = is.read();
					if(mOutStream != null){
						mOutStream.write(buffer);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}*/
			}
		}
	
}
