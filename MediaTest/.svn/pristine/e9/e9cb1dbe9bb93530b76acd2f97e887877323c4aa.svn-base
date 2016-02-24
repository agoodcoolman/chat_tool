/********************************************************************  
filename:   RTMPStream.h 
created:    2016-01-20
author:     firehood  
purpose:    发送H264视频到RTMP Server，使用libRtmp库
*********************************************************************/   
#pragma once  
//#include "rtmp.h"  
#include "rtmp_sys.h"  
#include "amf.h"  
//#include <stdio.h>
#include <pthread.h>
  
#define FILEBUFSIZE (1024 * 1024 * 10)       //  10M  
  
// NALU单元
typedef struct _NaluUnit  
{  
    int type;  
    int size;  
    unsigned char *data;
}NaluUnit;

typedef struct _MediaDataList
{
    int type;
    int size;//音视频数据的大小
    unsigned char *data;//音视频数据
    struct _MediaDataList *next;
}MediaDataList;

typedef struct _MediaDataQueue
{
	MediaDataList *first,*last;
	pthread_mutex_t mutex;//互斥锁
	pthread_cond_t cond;//条件变量
	int list_size;
}MediaDataQueue;
  
typedef struct _RTMPMetadata  
{  
    // video, must be h264 type  
    unsigned int    nWidth;  
    unsigned int    nHeight;  
    unsigned int    nFrameRate;     // fps  
    unsigned int    nVideoDataRate; // bps  
    unsigned int    nSpsLen;  
    unsigned char   Sps[1024];  
    unsigned int    nPpsLen;  
    unsigned char   Pps[1024];  
  
    // audio, must be aac type  
    bool            bHasAudio;  
    unsigned int    nAudioSampleRate;  
    unsigned int    nAudioSampleSize;  
    unsigned int    nAudioChannels;  
    char            pAudioSpecCfg;  
    unsigned int    nAudioSpecCfgLen;  
  
} RTMPMetadata,*LPRTMPMetadata;  
  
  
class CRTMPStream  
{  
public:  
    CRTMPStream(void);  
    ~CRTMPStream(void);  
public:  
    // 连接到RTMP Server
    bool Connect(const char* url);  
    // 断开连接
    void Close();  
    // 发送MetaData
    bool SendMetadata(LPRTMPMetadata lpMetaData);

    bool queueInit(MediaDataQueue *mQueue);
    // 发送H264数据帧
    bool SendH264Packet(unsigned char *data,unsigned int size,bool bIsKeyFrame,unsigned int nTimeStamp);  
    // 发送H264文件
    bool SendH264File(const char *pFileName);

    bool RTMP_Send_H264(unsigned char *buffer, unsigned int len, unsigned int tick);

    bool RTMP_Send_MetaData(unsigned char *sps, unsigned int spsLen,
    		unsigned char *pps, unsigned int ppsLen);

    bool SendAudioMetadata();

    bool SendAACPacket(unsigned char *data, unsigned int size, unsigned int nTimeStamp);

    bool SendAACData(unsigned char *data, unsigned int size, unsigned int tick);
private:  
    // 送缓存中读取一个NALU包
    bool ReadOneNaluFromBuf(NaluUnit &nalu);  
    // 发送数据
    int SendPacket(unsigned int nPacketType,unsigned char *data,unsigned int size,unsigned int nTimestamp);  
private:  
    RTMP* m_pRtmp;  
    unsigned char* m_pFileBuf;  
    unsigned int  m_nFileBufSize;  
    unsigned int  m_nCurPos;  
};  
