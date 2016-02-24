#include<com_example_mediatest_rtmpnative_RTMPNative.h>
#include<common.h>
#include<Android_Log.h>

void Java_com_example_mediatest_rtmpnative_RTMPNative_rtmpInit
  (JNIEnv *env, jobject thiz){
	CRTMPStream *rtmpStream = new CRTMPStream();
	jfieldID field_id = get_field_ID(env, class_path_name, "mRTMPSender", "I");
	env->SetIntField(thiz,field_id, (int)rtmpStream);
	rtmpStream->Connect("rtmp://139.196.175.109/oflaDemo/streams1010");
}

//发送视频的Metadata
void Java_com_example_mediatest_rtmpnative_RTMPNative_sendVMetaData
  (JNIEnv *env, jobject thiz, jbyteArray spsArray, jint spsLen, jbyteArray ppsArray, jint ppsLen){
	jbyte *spsByte = env->GetByteArrayElements(spsArray, NULL);
	jbyte  *ppsByte = env->GetByteArrayElements(ppsArray, NULL);
	unsigned char *sps =(unsigned char *)malloc(spsLen + 1);
	unsigned char *pps =(unsigned char *)malloc(ppsLen + 1);

	CRTMPStream *rtmpStream = get_stream_field(env,thiz,"com/example/mediatest/rtmpnative/RTMPNative",
				"mRTMPSender");

	memcpy(sps, spsByte, spsLen);
	memcpy(pps, ppsByte, ppsLen);
	sps[spsLen] = '\0';
	pps[ppsLen] = '\0';
	env->ReleaseByteArrayElements( spsArray, spsByte, 0);
	env->ReleaseByteArrayElements( ppsArray, ppsByte, 0);
	rtmpStream->RTMP_Send_MetaData(sps, spsLen, pps, ppsLen);
	free(sps);
	free(pps);
}

//发送视频数据
void Java_com_example_mediatest_rtmpnative_RTMPNative_sendVideoData
  (JNIEnv *env, jobject thiz, jbyteArray bufferArray, jint bufferLen, jint tick){
	CRTMPStream *rtmpStream = get_stream_field(env,thiz,"com/example/mediatest/rtmpnative/RTMPNative",
				"mRTMPSender");
	jbyte *bufferByte = env->GetByteArrayElements(bufferArray, NULL);
		unsigned char *buffer =(unsigned char *)malloc(bufferLen + 1);

	memcpy(buffer, bufferByte, bufferLen);
	buffer[bufferLen] = '\0';
	env->ReleaseByteArrayElements( bufferArray, bufferByte, 0);
	rtmpStream->RTMP_Send_H264(buffer, bufferLen, tick);
	free(buffer);
}

void Java_com_example_mediatest_rtmpnative_RTMPNative_sendAMetaData
  (JNIEnv *env, jobject thiz){
	CRTMPStream *rtmpStream = get_stream_field(env,thiz,"com/example/mediatest/rtmpnative/RTMPNative",
					"mRTMPSender");
	rtmpStream->SendAudioMetadata();
}

void Java_com_example_mediatest_rtmpnative_RTMPNative_sendAudioData
  (JNIEnv *env, jobject thiz, jbyteArray bufferArray, jint length, jint tick){
	CRTMPStream *rtmpStream = get_stream_field(env,thiz,"com/example/mediatest/rtmpnative/RTMPNative",
					"mRTMPSender");
	jbyte *bufferBytes = env->GetByteArrayElements(bufferArray, NULL);
	unsigned char *buffer = (unsigned char *)malloc(length + 1);

	memcpy(buffer, bufferBytes, length);
	buffer[length] = '\0';
	env->ReleaseByteArrayElements(bufferArray, bufferBytes, 0);
	rtmpStream->SendAACData(buffer, length, tick);
	free(buffer);
}


