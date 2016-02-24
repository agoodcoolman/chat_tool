#include<jni.h>
#include<include/RTMPStream.h>

#define class_path_name "com/pkmdz/chattool/rtmpnative/RTMPNative"

jfieldID get_field_ID(JNIEnv *env,const char *class_name,const char *name,const char *signature);

CRTMPStream * get_stream_field(JNIEnv *env, jobject thiz, const char *class_name, const char *param);
