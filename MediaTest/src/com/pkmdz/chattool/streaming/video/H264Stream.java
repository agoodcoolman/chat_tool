/*
 * Copyright (C) 2011-2014 GUIGUI Simon, fyhertz@gmail.com
 * 
 * This file is part of libstreaming (https://github.com/fyhertz/libstreaming)
 * 
 * Spydroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.pkmdz.chattool.streaming.video;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.pkmdz.chattool.streaming.exceptions.ConfNotSupportedException;
import com.pkmdz.chattool.streaming.exceptions.StorageUnavailableException;
import com.pkmdz.chattool.streaming.hw.EncoderDebugger;
import com.pkmdz.chattool.streaming.mp4.MP4Config;
import com.pkmdz.chattool.streaming.rtmp.H264RTMPSender;

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;
import android.graphics.ImageFormat;
import android.hardware.Camera.CameraInfo;
import android.media.MediaRecorder;
import android.os.Environment;
import android.service.textservice.SpellCheckerService.Session;
import android.util.Base64;
import android.util.Log;

/**
 * A class for streaming H.264 from the camera of an android device using RTP.
 * You should use a {@link Session} instantiated with {@link SessionBuilder} instead of using this class directly.
 * Call {@link #setDestinationAddress(InetAddress)}, {@link #setDestinationPorts(int)} and {@link #setVideoQuality(VideoQuality)}
 * to configure the stream. You can then call {@link #start()} to start the RTP stream.
 * Call {@link #stop()} to stop the stream.
 */
public class H264Stream extends VideoStream {

	public final static String TAG = "H264Stream";

	//private Semaphore mLock = new Semaphore(0);
	private MP4Config mConfig;

	/**
	 * Constructs the H.264 stream.
	 * Uses CAMERA_FACING_BACK by default.
	 */
	public H264Stream() {
		this(CameraInfo.CAMERA_FACING_BACK);
	}

	/**
	 * Constructs the H.264 stream.
	 * @param cameraId Can be either CameraInfo.CAMERA_FACING_BACK or CameraInfo.CAMERA_FACING_FRONT
	 * @throws IOException
	 */
	public H264Stream(int cameraId) {
		super(cameraId);
		mMimeType = "video/avc";
		mCameraImageFormat = ImageFormat.NV21;
		mRTMPSender = new H264RTMPSender();
		//mVideoEncoder = MediaRecorder.VideoEncoder.H264;
		//mPacketizer = new H264Packetizer();
	}

	/**
	 * Starts the stream.
	 * This will also open the camera and dispay the preview if {@link #startPreview()} has not aready been called.
	 */
	public synchronized void start() throws IllegalStateException, IOException {
		configure();
		if (!mStreaming) {
			byte[] pps = Base64.decode(mConfig.getB64PPS(), Base64.NO_WRAP);
			byte[] sps = Base64.decode(mConfig.getB64SPS(), Base64.NO_WRAP);
			((H264RTMPSender)mRTMPSender).setStreamParameters(sps, pps);
			//((H264Packetizer)mPacketizer).setStreamParameters(pps, sps);
			super.start();
		}
	}

	/**
	 * Configures the stream. You need to call this before calling {@link #getSessionDescription()} to apply
	 * your configuration of the stream.
	 */
	public synchronized void configure() throws IllegalStateException, IOException {
		super.configure();
		mMode = mRequestedMode;
		mQuality = mRequestedQuality.clone();
		mConfig = testH264();
	}
	
	/** 
	 * Tests if streaming with the given configuration (bit rate, frame rate, resolution) is possible 
	 * and determines the pps and sps. Should not be called by the UI thread.
	 **/
	private MP4Config testH264() throws IllegalStateException, IOException {
		 return testMediaCodecAPI();
	}

	@SuppressLint("NewApi")
	private MP4Config testMediaCodecAPI() throws RuntimeException, IOException {
		createCamera();
		updateCamera();
		try {
			if (mQuality.resX>=640) {
			}
			EncoderDebugger debugger = EncoderDebugger.debug(mSettings, mQuality.resX, mQuality.resY);
			return new MP4Config(debugger.getB64SPS(), debugger.getB64PPS());
		} catch (Exception e) {
		}
		
		return null;
	}
	
}
