#include<common.h>
#include<Android_Log.h>
#include <stdio.h>

jfieldID get_field_ID(JNIEnv *env,const char *class_name,
		const char *name,const char *signature){
	jclass clazz = env->FindClass(class_name);
	jfieldID jField = env->GetFieldID(clazz, name, signature);
	env->DeleteLocalRef(clazz);
	return jField;
}

CRTMPStream * get_stream_field(JNIEnv *env, jobject thiz,
		const char *class_name, const char *param){
	jfieldID field_id= get_field_ID(env, class_path_name, param, "I");
	CRTMPStream * rtmpStream = (CRTMPStream *) env->GetIntField(thiz, field_id);
	return rtmpStream;
}

void addADTStoPacket(char packet [],unsigned int packetLen) {
	    int profile = 2;  //AAC LC
	                      //39=MediaCodecInfo.CodecProfileLevel.AACObjectELD;
	    int freqIdx = 0;
	    int chanCfg = 2;  //CPE

	   // for(int i = 0; i< AACStream.AUDIO_SAMPLING_RATES.length; i++){
	    	//if(AACStream.AUDIO_SAMPLING_RATES[i] == samplingRate){
	    		freqIdx = 4;
	    	//	break;
	    	//}
	    //}

	    // fill in ADTS data
	    packet[0] = (char)0xFF;
	    packet[1] = (char)0xF9;
	    packet[2] = (char)(((profile-1)<<6) + (freqIdx<<2) +(chanCfg>>2));
	    packet[3] = (char)(((chanCfg&3)<<6) + (packetLen>>11));
	    packet[4] = (char)((packetLen&0x7FF) >> 3);
	    packet[5] = (char)(((packetLen&7)<<5) + 0x1F);
	    packet[6] = (char)0xFC;
}

void *RTMPLive_Thread(void *arg){
	CRTMPStream *rtmpStream =(CRTMPStream *) arg;
	uint32_t timestamp ;
	MediaData md, *mediaData = &md;
	//rtmpStream->Connect("rtmp://139.196.175.109/oflaDemo/streams120120");
	rtmpStream->Connect("rtmp://120.27.118.186/live/streams2018");
	rtmpStream->RTMP_Send_MetaData();
	rtmpStream->SendAudioMetadata();
	int tick = 0;
	int v_count = 1;
	int a_count = 0;
	//char packet[8] = {0};
	//FILE *fp = fopen("/storage/emulated/0/2016_o.aac", "wb");
	while(true){
		if(rtmpStream==NULL || rtmpStream->abort){
			break;
		}
		if(a_count < 2){
			rtmpStream->QueueGet(rtmpStream->AudioQueue, mediaData);
			//addADTStoPacket(packet, mediaData->size+7);
			//fwrite(packet, sizeof(char), strlen(packet), fp);
			//fwrite(mediaData->data, sizeof(unsigned char), mediaData->size, fp);
			rtmpStream->SendAACData(mediaData->data, mediaData->size, tick);
			//sleep(23);
			tick += 23;
			a_count ++;
		}else{
			rtmpStream->QueueGet(rtmpStream->mQueue, mediaData);
			tick = 50 * v_count;
			rtmpStream->RTMP_Send_H264(mediaData->data, mediaData->size, tick);
			tick += 23;
			a_count = 0;
			v_count++;
		}


	}
}



