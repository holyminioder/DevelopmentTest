/**********************************************************************************************************************
版权所有 Copyright (C), 2010-2014
文件名: LoadScanPrnDeviceLibrary.h
版本:	V4.001       
日期:	20141212
描述:   动态库相关定义及函数声明
**********************************************************************************************************************/
#ifndef __LOADSCANPRNDEVICELIBRARY__
#define __LOADSCANPRNDEVICELIBRARY__

/**********************************************************************************************************************
* 系统支持宏定义
**********************************************************************************************************************/
//#define    Windows_System

/**********************************************************************************************************************
* 类型宏定义
**********************************************************************************************************************/
#ifdef Windows_System
	#include <windows.h>
	#define Fonction_Call_Type __stdcall 	
#else
	#include <dlfcn.h>
	#define Fonction_Call_Type
	#define BOOL				int
	#define bool				int
	#define WORD				unsigned short
	#define DWORD				unsigned long
	#define LONG				long
	#define BYTE				unsigned char
	#define TRUE				1
	#define FALSE				0 
	#define true				1
	#define false				0 
	#define NULL				0
#pragma pack(1)
	//*********************************************************************************************************************
	//位图文件头
	typedef struct tagBITMAPFILEHEADER {
		WORD bfType; 
		DWORD bfSize; 
		WORD bfReserved1; 
		WORD bfReserved2; 
		DWORD bfOffBits; 
	} BITMAPFILEHEADER;

	//*********************************************************************************************************************
	//位图头信息
	typedef struct tagBITMAPINFOHEADER{
		DWORD  biSize; 
		LONG   biWidth; 
		LONG   biHeight; 
		WORD   biPlanes; 
		WORD   biBitCount; 
		DWORD  biCompression; 
		DWORD  biSizeImage; 
		LONG   biXPelsPerMeter; 
		LONG   biYPelsPerMeter; 
		DWORD  biClrUsed; 
		DWORD  biClrImportant; 
	} BITMAPINFOHEADER, *PBITMAPINFOHEADER; 
	
	//*********************************************************************************************************************
	//调色板
	typedef struct tagRGBQUAD { 
		BYTE rgbBlue;
		BYTE rgbGreen;
		BYTE rgbRed;
		BYTE rgbReserved;
	} RGBQUAD;

	typedef struct tagBITMAPINFO { 
		BITMAPINFOHEADER bmiHeader; 
		RGBQUAD          bmiColors[1]; 
	} BITMAPINFO, *PBITMAPINFO; 
#pragma pack()
#endif

/**********************************************************************************************************************
* Printer错误码定义
**********************************************************************************************************************/
#define	PRINTER_START_CODE			0x00
#define	PRINTER_NO_ERROR			0x00
#define	PRINTER_TIME_OUT			(PRINTER_NO_ERROR - 0x01)		//超时
#define	PRINTER_DATA_ERROR			(PRINTER_NO_ERROR - 0x02)		//数据线故障
#define	PRINTER_PAPER_ERROR			(PRINTER_NO_ERROR - 0x03)		//纸卷错误
#define	PRINTER_POWER_ERROR			(PRINTER_NO_ERROR - 0x04)		//电源线故障
#define	PRINTER_COVER_ERROR			(PRINTER_NO_ERROR - 0x05)		//上盖打开
#define	PRINTER_PAPER_JAM			(PRINTER_NO_ERROR - 0x06)		//卡纸
#define	PRINTER_NOT_COMPLETE			(PRINTER_NO_ERROR - 0x07)		//上一打印未完成(正在打印状态)
#define	PRINTER_OTHER_ERROR			(PRINTER_NO_ERROR - 0x08)		//其他错误(内存错误)
#define	PRINTER_TABPAR_NONE			(PRINTER_NO_ERROR - 0x09)		//参数文件
#define	PRINTER_HAVE_NOT_INIT		(PRINTER_NO_ERROR - 0x0A)		//未初始化
#define	PRINTER_INVALID_ARGUMNET		(PRINTER_NO_ERROR - 0x0B)		//参数无效
#define 	PRINTER_MODE_SWITCH_ERROR		(PRINTER_NO_ERROR - 0x0C)		//模式切换错误
#define 	PRINTER_HEAD_ERROR			(PRINTER_NO_ERROR - 0x0D)		//打印头抬起
#define 	PRINTER_CUT_ERROR			(PRINTER_NO_ERROR - 0x0E)		//切刀错误
#define	PRINTER_NOT_FULL			(PRINTER_NO_ERROR - 0x10)		//打印不完整
#define	PRINTER_FORBID			(PRINTER_NO_ERROR - 0x11)		//打印机禁止(一票一控打印出错)
#define	PRINTER_NOSUPPORT			(PRINTER_NO_ERROR - 0x12)		//不支持
#define	PRINTER_MIN_CODE			(PRINTER_NO_ERROR - 0x13)		//最小错误值
#define	PRINTER_INVALID_ERROR_CODE		(PRINTER_NO_ERROR - 0x14)		//无效错误码

/**********************************************************************************************************************
* Printer外部接口函数定义
**********************************************************************************************************************/
/**********************************************************************************************************************
* 序号:	1.1
* 功能:		初始化打印模块,完成缓存的清空,走纸到位等工作
* 入口参数:	无
* 出口参数:	无
* 返回值: 
* PRINTER_NO_ERROR			//正常
* PRINTER_DATA_ERROR			//数据线故障
* PRINTER_PAPER_ERROR		//纸卷错误(如：缺纸)
* PRINTER_POWER_ERROR		//电源线故障
* PRINTER_COVER_ERROR		//上盖打开
* PRINTER_PAPER_JAM			//卡纸
* PRINTER_NOT_COMPELTE		//上一打印未完成
* PRINTER_TABPAR_NONE	      	//参数文件错误
* PRINTER_MODE_SWITCH_ERROR   	//模式切换错误
* PRINTER_HEAD_ERROR			//打印头错误
* PRINTER_CUT_ERROR			//切刀错误
**********************************************************************************************************************/
typedef	int (Fonction_Call_Type *mPrinterInit)(const char* input_dir, const char* output_dir);

/**********************************************************************************************************************
* 序号:	1.2
* 功能:		设置打印模块切纸模式
* 入口参数:	modeType 模式 0 黑标模式 1 非黑标模式
* 出口参数:	无
* 返回值: 
* PRINTER_NO_ERROR			//正常
* PRINTER_DATA_ERROR			//数据线故障
* PRINTER_PAPER_ERROR		//纸卷错误(如：缺纸)
* PRINTER_POWER_ERROR		//电源线故障
* PRINTER_COVER_ERROR		//上盖打开
* PRINTER_PAPER_JAM			//卡纸
* PRINTER_NOT_COMPELTE		//上一打印未完成
* PRINTER_HAVE_NOT_INIT		//未初始化
* PRINTER_INVALID_ARGUMNET  	//参数无效
* PRINTER_MODE_SWITCH_ERROR 	//模式切换错误
* PRINTER_HEAD_ERROR			//打印头错误
* PRINTER_CUT_ERROR			//切刀错误
* PRINTER_FORBID			//打印机禁止
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterSetMode)(int const mode);

/**********************************************************************************************************************
* 序号:	1.3
* 功能:		打印模块的字体,大小和对齐方式
* 入口参数:	index 打印模块字体 0-12*24;1-9*24
		Size 打印字体的大小 0-255 0-3位为高倍数，4-7位为宽倍数
		Alignment 对齐方式 0-居左;1-居中;2-居右
* 出口参数:	无
* 返回值:	TRUE 成功 FALSE 失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterSetFont)(unsigned char const index, unsigned char const size, unsigned char const alignment);

/**********************************************************************************************************************
* 序号:	1.4
* 功能:		设置黑体
* 入口参数:	n 打印模块字体 0-255
* 出口参数:	无
* 返回值:	TRUE 成功 FALSE 失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterSetFontEmpha)(unsigned char const n);

/**********************************************************************************************************************
* 序号:	1.5
* 功能:		设置行间距
* 入口参数:	n 打印模块字体 0-127
* 出口参数:	无
* 返回值:	TRUE 成功 FALSE 失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterSetLineSpace)(unsigned char const size);

/**********************************************************************************************************************
* 序号:	1.6
* 功能:		设置字符间距
* 入口参数:	size 字符间距 0-255
* 出口参数:	无
* 返回值:	TRUE 成功 FALSE 失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterSetCharSpace)(unsigned char const size);

/**********************************************************************************************************************
* 序号:	1.7
* 功能:		设置左边界
* 入口参数:	size 左边界
* 出口参数:	无
* 返回值:	TRUE 成功 FALSE 失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterSetLeftMargin)(int const size);

/**********************************************************************************************************************
* 序号:	1.8
* 功能:		设置有效打印区域宽度
* 入口参数:	width 有效打印区域宽度
* 出口参数:	无
* 返回值:	无
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterSetAreaWidth)(int const width);

/**********************************************************************************************************************
* 序号:	1.9
* 功能:		打印模块能力查询，是否支持字符打印模式及图像打印模式
* 入口参数:	无
* 出口参数:	无
* 返回值:	MODE_SUPPORT_CHAR 0x01 支持字符模式
**********************************************************************************************************************/
typedef	int (Fonction_Call_Type *mPrinterQueryCapability)();

/**********************************************************************************************************************
* 序号:	1.10
* 功能:		获取打印模块切纸模式
* 入口参数:	无
* 出口参数:	无
* 返回值:	模式 0 黑标模式
		     1 非黑标模式
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterGetMode)(int* const mode);

/**********************************************************************************************************************
* 序号:	1.11
* 功能:		检测打印模块状态
* 入口参数:	无
* 出口参数:	无
* 返回值:	TRUE 正常
		FALSE 错误状态
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterIsReady)();

/**********************************************************************************************************************
* 序号:	1.12
* 功能:		获取打印模块的dpi值
* 入口参数:	无
* 出口参数:	widthDpi 宽的dpi heightDpi 长的dpi
* 返回值:	无
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterGetDpi)(int* const widthDpi,int* const heightDpi);

/**********************************************************************************************************************
* 序号:	1.13
* 功能:		取得打印模块的硬件版本号
* 入口参数:	无
* 出口参数:	无
* 返回值:	硬件版本号
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterGetHWVersion)(char* const hwInfo, unsigned int const length);

/**********************************************************************************************************************
* 序号:	1.14
* 功能:		获取最近一次的错误码
* 入口参数:	无
* 出口参数:	无
* 返回值:	错误码
**********************************************************************************************************************/
typedef	int (Fonction_Call_Type *mPrinterGetLastErrorCode)();

/**********************************************************************************************************************
* 序号:	1.15
* 功能:		获取最近一次的错误描述
* 入口参数:	无
* 出口参数:	errStr 错误描述
* 返回值:	无
**********************************************************************************************************************/
typedef	void (Fonction_Call_Type *mPrinterGetLastErrorStr)(char* const errStr, unsigned int const length);

/**********************************************************************************************************************
* 序号:	1.16
* 功能:		打印字符串，字符集为GB2312
* 入口参数:	str 字符串
* 出口参数:	无
* 返回值: 
* PRINTER_NO_ERROR			//正常
* PRINTER_DATA_ERROR			//数据线故障
* PRINTER_PAPER_ERROR		//纸卷错误(如：缺纸)
* PRINTER_POWER_ERROR		//电源线故障
* PRINTER_COVER_ERROR		//上盖打开
* PRINTER_PAPER_JAM			//卡纸
* PRINTER_NOT_COMPELTE		//上一打印未完成
* PRINTER_HAVE_NOT_INIT		//未初始化
* PRINTER_INVALID_ARGUMNET  	//参数无效
* PRINTER_MODE_SWITCH_ERROR 	//模式切换错误
* PRINTER_HEAD_ERROR			//打印头错误
* PRINTER_CUT_ERROR			//切刀错误
* PRINTER_FORBID			//打印机禁止
**********************************************************************************************************************/
typedef	int (Fonction_Call_Type *mPrinterPrintString)(const char* const str);

/**********************************************************************************************************************
* 序号:	1.17
* 功能:		空行
* 入口参数:	lineCount 空行的行数
* 出口参数:	无
* 返回值:	无
**********************************************************************************************************************/
typedef	void (Fonction_Call_Type *mPrinterFeedLine)(unsigned char const lineCount);

/**********************************************************************************************************************
* 序号:	1.18
* 功能:		切纸，字符打印模式与图像打印模式公用
* 入口参数:	无
* 出口参数:	无
* 返回值:	无
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterCutPaper)();

/**********************************************************************************************************************
* 序号:	1.19
* 功能:		打印标准条码,打印一维条码
* 入口参数:	width 条码宽度 2-6
		height 条码高度 0-255
		str 条码内容
		codeType 设置条码类型
		setType 精确设置条码类型
* 出口参数:	无
* 返回值: 
* PRINTER_NO_ERROR			//正常
* PRINTER_DATA_ERROR			//数据线故障
* PRINTER_PAPER_ERROR		//纸卷错误(如：缺纸)
* PRINTER_POWER_ERROR		//电源线故障
* PRINTER_COVER_ERROR		//上盖打开
* PRINTER_PAPER_JAM			//卡纸
* PRINTER_NOT_COMPELTE		//上一打印未完成
* PRINTER_OTHER_ERROR		//其他错误
* PRINTER_HAVE_NOT_INIT		//未初始化
* PRINTER_INVALID_ARGUMNET  	//参数无效
* PRINTER_MODE_SWITCH_ERROR 	//模式切换错误
* PRINTER_HEAD_ERROR			//打印头错误
* PRINTER_CUT_ERROR			//切刀错误
* PRINTER_FORBID			//打印机禁止
**********************************************************************************************************************/
typedef	int (Fonction_Call_Type *mPrinterPrint1DBar)(unsigned char width, unsigned char height, const char* const str, char codeType, char setType);

/**********************************************************************************************************************
* 序号:	1.20
* 功能:		打印二维条码，标准为PDF417
* 入口参数:	module_width 条码宽度 5-35
		module_height 条码高度 10-124
		str 条码内容
* 出口参数:	无
* 返回值: 
* PRINTER_NO_ERROR			//正常
* PRINTER_DATA_ERROR			//数据线故障
* PRINTER_PAPER_ERROR		//纸卷错误(如：缺纸)
* PRINTER_POWER_ERROR		//电源线故障
* PRINTER_COVER_ERROR		//上盖打开
* PRINTER_PAPER_JAM			//卡纸
* PRINTER_NOT_COMPELTE		//上一打印未完成
* PRINTER_OTHER_ERROR		//其他错误
* PRINTER_HAVE_NOT_INIT		//未初始化
* PRINTER_INVALID_ARGUMNET    	//参数无效
* PRINTER_MODE_SWITCH_ERROR   	//模式切换错误
* PRINTER_HEAD_ERROR			//打印头错误
* PRINTER_CUT_ERROR			//切刀错误
* PRINTER_FORBID			//打印机禁止
**********************************************************************************************************************/
typedef	int (Fonction_Call_Type *mPrinterPrintPDF417)(int module_width, int module_height, int data_rows, int data_columns, int err_correct_level,const char* databuf, int length, int mode);

/**********************************************************************************************************************
* 序号:	1.21
* 功能:		设置自定义字符
* 入口参数:	c1 起始字符 32-127
		c2 结束字符 32-127
		m 字体大小 12 12*24; 9 9*24
		data 用户定义字符的内容(点阵格式)
		length 用户定义字符的内容的长度
* 出口参数:	无
* 返回值:	TRUE 成功 FALSE 失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterSetUserDefChar)(unsigned char c1, unsigned char c2, unsigned char m, const unsigned char* const data, int length);

/**********************************************************************************************************************
* 序号:	1.22
* 功能:		取消用户自定义字符
* 入口参数:	c1 用户自定义的起始字符 32-127
		c2 用户自定义的结束字符 32-127
* 出口参数:	无
* 返回值:	TRUE 成功 FALSE 失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterCancelUserDefChar)(unsigned char c1, unsigned char c2);

/**********************************************************************************************************************
* 序号:
* 功能:		打印用户自定义字符
入口参数： 	c1      用户自定义起始字符    32~255
         	c2      用户自定义结束字符    32~255
出口参数： 	无
返回值：   	true 成功 false 失败 /7 
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterPrintUserChar)(unsigned char c1, unsigned char c2);

/**********************************************************************************************************************
* 序号:	1.23
* 功能:		打印用户定义的图形点阵
* 入口参数:	xPos 起始位置x 0-255,单位0.1mm
		yPos 起始位置y 0-255,单位0.1mm
		imagePath LOGO图像文件路径
* 出口参数:	无
* 返回值: 
* PRINTER_NO_ERROR			//正常
* PRINTER_DATA_ERROR			//数据线故障
* PRINTER_PAPER_ERROR		//纸卷错误(如：缺纸)
* PRINTER_POWER_ERROR		//电源线故障
* PRINTER_COVER_ERROR		//上盖打开
* PRINTER_PAPER_JAM			//卡纸
* PRINTER_NOT_COMPELTE		//上一打印未完成
* PRINTER_OTHER_ERROR		//其他错误
* PRINTER_HAVE_NOT_INIT		//未初始化
* PRINTER_INVALID_ARGUMNET    	//参数无效
* PRINTER_MODE_SWITCH_ERROR   	//模式切换错误
* PRINTER_HEAD_ERROR			//打印头错误
* PRINTER_CUT_ERROR			//切刀错误
* PRINTER_FORBID			//打印机禁止
**********************************************************************************************************************/
typedef int (Fonction_Call_Type *mPrinterPrintDiskImage)(int xPos, int yPos, const char* const imagePath);

/**********************************************************************************************************************
* 序号:	1.24
* 功能:		打印用户定义的图形点阵
* 入口参数:	xPos 起始位置x 单位0.1mm
		yPos 起始位置y 单位0.1mm
		width 图形宽度 像素单位
		height 图形高度 像素单位
		image 图形内容（用户自定义点阵）
* 出口参数:	无
* 返回值:	
* PRINTER_NO_ERROR			//正常
* PRINTER_DATA_ERROR			//数据线故障
* PRINTER_PAPER_ERROR		//纸卷错误(如：缺纸)
* PRINTER_POWER_ERROR		//电源线故障
* PRINTER_COVER_ERROR		//上盖打开
* PRINTER_PAPER_JAM			//卡纸
* PRINTER_NOT_COMPELTE		//上一打印未完成
* PRINTER_OTHER_ERROR		//其他错误
* PRINTER_HAVE_NOT_INIT		//未初始化
* PRINTER_INVALID_ARGUMNET    	//参数无效
* PRINTER_MODE_SWITCH_ERROR   	//模式切换错误
* PRINTER_HEAD_ERROR			//打印头错误
* PRINTER_CUT_ERROR			//切刀错误
* PRINTER_FORBID			//打印机禁止
* PRINTER_NOSUPPORT			//不支持
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterPrintUserBitmap)(int xPos, int yPos, const char* const bitmap);

/**********************************************************************************************************************
* 序号:	1.25
* 功能:		打印大黑标
* 入口参数:	length barCode的长度，保证是12的整数倍，小于等于96
		barcode 需要打印的内容，其中存的是字符’0’或’1’，以’\0’结束
* 出口参数:	无
* 返回值:	TRUE 成功 FALSE 失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterPrintMark)(const char* const barcode, unsigned int const length);

/**********************************************************************************************************************
* 序号:	1.26
* 功能:		获取打印模块切刀与打印头间距
* 入口参数:	无
* 出口参数:	无
* 返回值:	打印模块切刀与打印头间距，单位为毫米
**********************************************************************************************************************/
typedef	bool  (Fonction_Call_Type *mPrinterGetTopMargin)(int* const topMargin);

/**********************************************************************************************************************
* 序号:	1.29
* 功能:		判断打印完整性
* 入口参数:	time_out：超时时间，单位秒，1-10;
* 出口参数:	无	
* 返回值: 
* PRINTER_NO_ERROR			//正常
* PRINTER_DATA_ERROR			//数据线故障
* PRINTER_PAPER_ERROR		//纸卷错误(如：缺纸)
* PRINTER_POWER_ERROR		//电源线故障
* PRINTER_COVER_ERROR		//上盖打开
* PRINTER_PAPER_JAM			//卡纸
* PRINTER_NOT_COMPELTE		//上一打印未完成
* PRINTER_HAVE_NOT_INIT		//未初始化
* PRINTER_INVALID_ARGUMNET  	//参数无效
* PRINTER_MODE_SWITCH_ERROR 	//模式切换错误
* PRINTER_HEAD_ERROR			//打印头错误
* PRINTER_CUT_ERROR			//切刀错误
* PRINTER_NOT_FULL			//打印不完整
* PRINTER_FORBID			//打印机禁止
**********************************************************************************************************************/
typedef	int (Fonction_Call_Type *mPrinterPrintIsComplete)(int const time_out);

/**********************************************************************************************************************
* 序号:	1.27
* 功能:		获取打印长度
* 入口参数:	无
* 出口参数:	无
* 返回值:	115
**********************************************************************************************************************/
typedef	long (Fonction_Call_Type *mGetPrintLength)();

/**********************************************************************************************************************
* 序号:	1.28
* 功能:		打印小黑标
* 入口参数:	length barCode的长度，保证是24或26的整数倍，小于等于96或104
		barcode 需要打印的内容，其中存的是字符’0’或’1’，以’\0’结束
* 出口参数:	无
* 返回值: 	TRUE 成功 FALSE 失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrinterPrintMiniMark)(int length,char *barcode);

/**********************************************************************************************************************
* 序号:	1.27
* 功能:		获取软件版本
* 入口参数:	无
* 出口参数:	无
* 返回值:	115
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mGetSWVersion)(char* const swVersion, unsigned int const length);

/**********************************************************************************************************************
* 功能：    	打印机下载自定义LOGO图像到打印机
*入口参数:  	imgCount LOGO图像列表的个数
*          	imgList LOGO图像的图像数据列表(包含一位位图信息头及数据部分) 	
*出口参数:  	无
*返回值:	true    成功
*		false   失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mLoadLogoImage)(int imgCount, char* imgList[]);

/**********************************************************************************************************************
* 功能:		打印机下载自定义LOGO图像到打印机
*入口参数:	imgCount LOGO图像列表的个数
*		imgFileList LOGO图像文件列表(若只有文件名则为当前路径) 	
*出口参数:  	无
*返回值：   	true    成功
*		false   失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mLoadDiskLogo)(int imgCount, char* imgList[]);

/**********************************************************************************************************************
* 功能:		打印LOGO图像
*入口参数:  	xpos  打印LOGO图像左上角x坐标
*		ypos  打印LOGO图像左上角y坐标
*		index 指定LOGO的编号 	
*出口参数:	无
*返回值:	true    成功
*		false   失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mPrintLogo)(unsigned int const xpos, unsigned int const ypos, int const index);

/**********************************************************************************************************************
* 功能:		设置打印的旋转角度
*入口参数:  	angle  旋转角度（0~360） 	
*出口参数:  	无
*返回值：   	true    成功
*		false   失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mSetAngle)(unsigned int const angle);

/**********************************************************************************************************************
* 功能:		设置底部空白距离
*入口参数:	margin  底部空白距离,单位0.1mm 	
*出口参数:	无
*返回值:	true    成功
*		false   失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mSetBottomMargin)(int const margin);

/**********************************************************************************************************************
* 功能:		执行ESC/POS指令
*入口参数:	command   指令字符串
*		strLen    指令字符串的长度
*出口参数:	无
*返回值:	true    成功
*		false   失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mExec_ESC_POS)(char* const command, int const strlen);

/**********************************************************************************************************************
* 序号:	
* 功能:		设置打印模式为页模式，并设置页模式相应信息
*入口参数:	width       页宽，单位0.1mm
		height      页高，单位0.1mm
		leftTop_x   页左上角定点位置x坐标，单位0.1mm
		leftTop_y   页左上角定点位置y坐标，单位0.1mm
*出口参数:	无
*返回值：	true    成功
*		false   失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mSetPageMode)(int width, int height, int leftTop_x, int leftTop_y);

/**********************************************************************************************************************
* 序号:	
* 功能:		设置打印模式为行模式
*入口参数:	无
*出口参数:	无
*返回值：	true    成功
*		false   失败
**********************************************************************************************************************/
typedef	bool (Fonction_Call_Type *mSetLineMode)();

/**********************************************************************************************************************
* 序号:	
* 功能:		结束页模式输入并打印该页内容
*入口参数:	无
*出口参数:	无
*返回值：	true    成功
*		false   失败
**********************************************************************************************************************/
typedef	int (Fonction_Call_Type *mPrintPage)();



#endif
