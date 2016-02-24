#include<jni.h>
#include<include/RTMPStream.h>

#define class_path_name "com/pkmdz/chattool/rtmpnative/RTMPNative"

#define FLV_TAG_TYPE_AUDIO 0
#define FLV_TAG_TYPE_VIDEO 1

jfieldID get_field_ID(JNIEnv *env,const char *class_name,const char *name,const char *signature);

CRTMPStream * get_stream_field(JNIEnv *env, jobject thiz, const char *class_name, const char *param);

void *RTMPLive_Thread(void *arg);

void addADTStoPacket(char packet [],unsigned int packetLen);
