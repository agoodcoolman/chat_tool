#include<common.h>
#include<Android_Log.h>

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
