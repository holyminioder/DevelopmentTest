//
// Created by Administrator on 2017/12/11 0011.
//

#ifndef DEVELOPMENTTEST_NATIVE_SCANNER_H
#define DEVELOPMENTTEST_NATIVE_SCANNER_H

/**********************************************************************************************************************
* 通用宏定义定义
**********************************************************************************************************************/
#if WIN32
#define 	__WINAPI WINAPI
#else
#define 	__WINAPI
#define	BOOL				int
#define	bool				int
#define BYTE				unsigned char
#define LONG				long
#define WORD				unsigned short
#define DWORD				unsigned long
#define TRUE				1
#define FALSE				0
#define true				1
#define false				0
#endif

/**********************************************************************************************************************
* Hscanner外部接口函数定义
**********************************************************************************************************************/
#ifdef	__cplusplus
extern "C" {
#endif

typedef void(*SCANNERCALLBACK)(int eventId, void* data);

/**
 * 1 初始化读票机
 */
int __WINAPI SInit(SCANNERCALLBACK scannerCallBack(int,void*), const char*
input_dir, const char* output_dir);

/**
 * 2 获取扫描图像的分辨率
 */
bool __WINAPI SGetScanDpi(int* const widthDpi, int* const heightDpi);

/**
 * 3 获取扫描图像的分辨率
 */
bool __WINAPI SGetBrandDpi(int* const widthDpi, int* const heightDpi);

/**
 * 4 获取最近一次的错误码
 */
int __WINAPI SGetLastErrorCode();

/**
 * 5 读票机能力查询
 */
int __WINAPI SQueryCapability();

/**
 * 6 获取最近一次的错误描述（信息长度在 100 字节以内，包含结束符）。
 */
void __WINAPI SGetLastErrorStr(char * const errStr, unsigned int const errStrBufLen);

/**
 *7  启动读票机
 */
bool __WINAPI SStart();

/**
 *8 停止读票机
 */
bool __WINAPI SStop();

/**
 * 9 是否扫描完成
 */
bool __WINAPI ScanIsComplete();

/**
 * 10 读票机状态检测
 */
bool __WINAPI ScannerIsReady();

/**
 * 11 获得原始图像大小，如果没有图像，则两项均为0
 * 传出：
 * width : 图像实际宽度,像素大小
 * height: 图像实际高度，像素大小
 * bufsize: BCRGetImage 接口所需分配空间的大小
 */
bool __WINAPI SGetOriginImageSize(int* const width,int* const height, int* const bufsize);

/**
 * 12 获得原始图像,其图像应该是经过可污点处理和方位校正后的单色图像
 * 传出：
 * image :图像数据
 * bufferLen: Image缓冲区大小
 */
int __WINAPI SGetOriginImage(char* const image,int const bufferLen);

/**
 * 13 读取扫描的内容
 * 传出：
 * ticketInfo: 返回解析出的条码信息，具体格式参考第 1 章中公共数据返回结构定义
 * 传入：
 * bufferLen: 应用程序分配的数据区的大小
 * 返回：
 * 0 :读取失败
 * 正整数：读取到图像的时间大小
 */
int __WINAPI SGetTicketInfo(unsigned char* const ticketInfo, int const bufferLen);

/**
 * 14 标记打印
 */
int __WINAPI SPrintBrandImage(const char* image, int index, int xpos, int ypos);

/**
 * 15 退纸
 */
bool __WINAPI SRollBack();

/**
 * 16 识别指定图像区域中的黑标或二维条码
 */
bool __WINAPI SRecognizeItem(int posX, int posY, int width, int height, const char*
const image, char* result);

/**
 * 17 识别指定图像区域中的黑标或二维条码
 */
bool __WINAPI SAdjustSensibility(int *currentSens, int *adjustSens);

/**
 * 18 取得打印机的硬件信息
 *  传出：hwinfo
 *  传入：标识 hwInfo 的长度（在4096字节以内）
 */
bool __WINAPI SGetHWInformation(char* const hwInfo , unsigned int const length);

/**
 * 19 获取软件版本
 * 传出：
 * swVersion:版本号
 */
bool __WINAPI SGetSWVersion(char* const swVersion , unsigned int const length);



#ifdef	__cplusplus
}
#endif

#endif //DEVELOPMENTTEST_NATIVE_SCANNER_H
