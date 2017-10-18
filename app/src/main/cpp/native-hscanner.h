//
// Created by chandler on 2017/10/17.
//

#ifndef DEVELOPMENTTEST_BCR_NATIVE_HSCANNER_H
#define DEVELOPMENTTEST_BCR_NATIVE_HSCANNER_H


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
 * 1 初始化扫描枪
 */
int __WINAPI BCRInit(SCANNERCALLBACK scannerCallBack(int,void*), const char*
input_dir, const char* output_dir);

/**
 * 2 关闭扫描枪
 */
void __WINAPI BCRClose();

/**
 * 3 获取最近一次的错误码
 */
int __WINAPI BCRGetLastErrorCode();

/**
 * 4 扫描枪能力查询
 */
int __WINAPI BCRQueryCapability();

/**
 * 5 获取最近一次的错误描述（信息长度在 100 字节以内，包含结束符）。
 */
void __WINAPI BCRGetLastErrorStr(char *const errStr, int const errStrBufLen);

/**
 *6  打开瞄准灯
 */
bool __WINAPI BCRAimOn();

/**
 *7 关闭瞄准灯
 */
bool __WINAPI BCRAimOff();

/**
 * 8 启用扫描枪设备
 */
bool __WINAPI BCREnable();

/**
 * 9 禁用扫描枪设备
 */
bool __WINAPI BCRDisable();

/**
 * 10 启用某种条码类型的支持，由于默认只打开 PDF417、 Data Matrix 和 Interleaved 2 of 5 类型的条码，
当需要识别其它类型条码时，可通过此接口打开其它条码的识别支持。
 */
bool __WINAPI BCREnableCode(unsigned int const codeType);

/**
 * 11 关闭某种条码类型的支持。
 */
bool __WINAPI BCRDisableCode(unsigned int const codeType);

/**
 * 12 进入休眠模式，进入休眠后若有条码进入扫描区域或用户按下扫描开关，可以离开休眠模式，或者
通过 Wakeup 接口离开休眠模式。
 */
bool __WINAPI BCRSleep(unsigned int const time);

/**
 * 13 唤醒扫描枪
 */
bool __WINAPI BCRWakeup();

/**
 * 14 重置通讯
 */
bool __WINAPI BCRResetComm();

/**
 * 15 开始扫描识别
 */
bool __WINAPI BCRStartScan();

/**
 * 16 停止扫描识别
 */
bool __WINAPI BCRStopScan();

/**
 * 17 是否扫描完成
 */
bool __WINAPI BCRScanIsComplete();

/**
 * 18 扫描枪状态检测
 */
bool __WINAPI BCRIsReady();

/**
 * 19 设置扫描枪模式
 */
bool __WINAPI BCRSetScanMode(unsigned int const mode);

/**
 * 20 获取扫描枪模式
 */
bool __WINAPI BCRGetScanMode(unsigned int *const mode);

/**
 * 21 获取扫描枪扳机状态，使用手动模式的时候有效。
 */
void __WINAPI BCRGetTriggerStatus(int *const status);

/**
 * 22 获取扫描图像的分辨率
 */
void __WINAPI BCRGetScanDpi(int *const widthDpi, int *const heightDpi);

/**
 * 23 获得原始图像大小，如果没有图像，则两项均为0
 * 传出：
 * width : 图像实际宽度,像素大小
 * height: 图像实际高度，像素大小
 * bufsize: BCRGetImage 接口所需分配空间的大小
 */
bool __WINAPI BCRGetImageSize(int* const width,int* const height, int* const bufsize);

/**
 * 24 获得原始图像,其图像应该是经过可污点处理和方位校正后的单色图像
 * 传出：
 * image :图像数据
 * bufferLen: Image缓冲区大小
 */
int __WINAPI BCRGetImage(char* const image,int const bufferLen);

/**
 * 25 获取扫描数据的长度，供应用分配数据区大小。
 * 传出：
 * length: 为GetTicketInfo 接口调用时ticketInfo参数所需的空间大小
 */
bool __WINAPI BCRGetDataLength(unsigned int *const length);

/**
 * 26 读取扫描的内容
 * 传出：
 * ticketInfo: 返回解析出的条码信息，具体格式参考第 1 章中公共数据返回结构定义
 * 传入：
 * bufferLen: 应用程序分配的数据区的大小
 * 返回：
 * 0 :读取失败
 * 正整数：读取到图像的时间大小
 */
int __WINAPI BCRGetTicketInfo(unsigned char *const ticketInfo, unsigned int const bufferLen);

/**
 * 27 播放扫描枪提示音
 */
bool __WINAPI BCRBeep(int const tone);

/**
 * 28 允许扫描枪播放提示音
 */
void __WINAPI BCREnableBeep();

/**
 * 29 禁止扫描枪播放提示音
 */
void __WINAPI BCRDisableBeep();

/**
 * 30 清空扫描枪缓存中的数据
 */
void __WINAPI BCRClearBuffer();

/**
 * 31 取得扫描枪的硬件信息
 *  传出：hwinfo
 *  传入：标识 hwInfo 的长度（在4096字节以内）
 */
bool __WINAPI BCRGetHWInformation(char* const hwInfo , int const length);

/**
 * 32 获取软件版本
 * 传出：
 * swversion:版本号
 */
void __WINAPI BCRGetSWVersion(char* const swversion , unsigned int const length);

/**
 * 33 打开并设置用户反馈灯的颜色
 * 传入：
 *  1 Green LED
    2 Red LED
 */
bool __WINAPI BCRUserLEDOn(unsigned int *const mode);

/**
 * 34 关闭用户反馈灯
 */
void __WINAPI BCRUserLEDOff();

#ifdef	__cplusplus
}
#endif

#endif //DEVELOPMENTTEST_BCR_NATIVE_HSCANNER_H
