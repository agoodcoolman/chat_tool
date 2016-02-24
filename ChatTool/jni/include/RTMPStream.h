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
#include <stdio.h>
#include <pthread.h>
  
#define FILEBUFSIZE (1024 * 1024 * 10)       //  10M
#define MAXMEMORYSIZE 1024*1024
  
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
    int size;//音视频数据大小
    unsigned char data[1024 * 200];//闊宠棰戞暟鎹
    struct _MediaDataList *next;
}MediaData, MediaDataList;

typedef struct _MediaDataQueue
{
	MediaDataList *first,*last;
	pthread_mutex_t mutex;//浜掓枼閿
	pthread_cond_t cond;//鏉′欢鍙橀噺
	int list_size;
	int total_size;
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

    //bool queueInit(MediaDataQueue *mQueue);
    // 发送H264数据帧
    bool SendH264Packet(unsigned char *data,unsigned int size,bool bIsKeyFrame,unsigned int nTimeStamp);  
    // 发送H264文件
    bool SendH264File(const char *pFileName);

    bool RTMP_Send_H264(unsigned char *buffer, unsigned int len, unsigned int tick);

    bool RTMP_Send_MetaData(unsigned char *sps, unsigned int spsLen,
    		unsigned char *pps, unsigned int ppsLen);

    bool RTMP_Send_MetaData();

    bool SendAudioMetadata();

    bool SendAACPacket(unsigned char *data, unsigned int size, unsigned int nTimeStamp);

    bool SendAACData(unsigned char *data, unsigned int size, unsigned int tick);

    //初始化队列
    bool queueInit(MediaDataQueue *q);

    bool QueuePut(MediaDataQueue *q, int type, int size, unsigned char *data);

    bool QueueGet(MediaDataQueue *q, MediaData *mediaData);

    bool QueueFlush(MediaDataQueue *q);

    bool setSPSAndPPS(unsigned char *sps, unsigned int spsLen,
    		unsigned char *pps, unsigned int ppsLen);

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

    unsigned char *SPS;
    unsigned char *PPS;
    unsigned int spsLen;
    unsigned int ppsLen;

public:
    bool abort;
    MediaDataQueue *mQueue;
    MediaDataQueue *AudioQueue;
    //FILE *fp;
};  
