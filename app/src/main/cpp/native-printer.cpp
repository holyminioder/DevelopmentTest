#include <jni.h>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include <android/log.h>

extern "C"

#include "byScanPrnDeviceDll.h"
#include "com_example_lederui_developmenttest_data_PrinterInterface.h"

#define  LOG_TAG    "native-printer"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , LOG_TAG, __VA_ARGS__)


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
//#define	PRINTER_INVALID_ARGUMNET		(PRINTER_NO_ERROR - 0x0B)		//参数无效
#define 	PRINTER_MODE_SWITCH_ERROR		(PRINTER_NO_ERROR - 0x0C)		//模式切换错误
#define 	PRINTER_HEAD_ERROR			(PRINTER_NO_ERROR - 0x0D)		//打印头抬起
#define 	PRINTER_CUT_ERROR			(PRINTER_NO_ERROR - 0x0E)		//切刀错误
#define	PRINTER_NOT_FULL			(PRINTER_NO_ERROR - 0x10)		//打印不完整
#define	PRINTER_FORBID			(PRINTER_NO_ERROR - 0x11)		//打印机禁止(一票一控打印出错)
#define	PRINTER_NOSUPPORT			(PRINTER_NO_ERROR - 0x12)		//不支持
#define	PRINTER_MIN_CODE			(PRINTER_NO_ERROR - 0x13)		//最小错误值
#define	PRINTER_INVALID_ERROR_CODE		(PRINTER_NO_ERROR - 0x14)		//无效错误码


int	returnValueInt = PRINTER_NO_ERROR;
bool isInit = false;
char  mbarCode[256] = {0x00};



JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintInit
        (JNIEnv * env, jobject)
{
    char errStr[256] = {0x00};

    returnValueInt = byPInit("/sdcard","/sdcard");
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B) {
        //printf("PrinterInit failed, return code: %d\n", returnValueInt);
        LOGI("Init return %d", returnValueInt);
        byPGetLastErrorStr(errStr, 200);
        return false;
    }else{
        isInit = true;
    }

    return isInit;

}


JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_SetCutMode
        (JNIEnv * env, jobject, jint mode) {

    bool ret = PSetCutterMode(mode);

    char err[256] = {0x00};
    byPGetLastErrorStr(err,200);
    LOGI("setcutmode(1) return %d err = %s",ret,err);


    return  ret;
}



bool PrintPDF417() {
    //*****************************************************************************************************************
    //定义变量并初始化
    int		returnValueInt = PRINTER_NO_ERROR;
    bool		returnValueBool = false;
    bool		returnValue = false;
    char    	barCode[256] = {0x00};
    int		barCodeLength = 0;
    int		ii = 0;
    int 		begin = 0;

    //*****************************************************************************************************************
    //设置行模式
    if(!byPSetLineMode())
    {
        goto ExitLine;
    }

    //*****************************************************************************************************************
    //设置底部边距
    if(!byPSetBottomMargin(200))
    {
        goto ExitLine;
    }

    //*****************************************************************************************************************
    //判断设备状态是否正常
//    byPPrinterIsReady();

    //*****************************************************************************************************************
    //打印彩票数据
    returnValueBool = byPSetLeftMargin(10);//设置左边界
    if(!returnValueBool)
    {
        printf("PrinterSetLeftMargin failed!\n");
        fflush(stdout);
        goto ExitLine;
    }

    returnValueBool = byPSetLineSpace(10);//设置行高
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(36) failed!\n");
        fflush(stdout);
        goto ExitLine;
    }

    returnValueBool = byPSetFont(0x00, 0x11,0x01);//设置字体
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(0x33,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = byPPrintString("体彩<全国联网排列3>\n\n");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('体彩<全国联网排列3>') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    returnValueBool = byPSetLineSpace(14);//设置行间距
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(24) failed!\n");
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueBool = byPSetFont(0x00,0x00,0x01);//设置字体
    if(!returnValueBool)
    {
        printf("PrinterSetFont(0x00,0x00,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = byPPrintString("35073110603460810054   d2WIvi   64319331\n");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('35073110603460810054') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    returnValueInt = byPPrintString("销售点:06034      06073期 2006/03/22开奖");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('销售点:06034') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }
    byPPrintString("\n");

    returnValueInt = byPPrintString("倍:1  合计:2元       2006/03/22 12:26:49\n");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('倍:1  合计:2元') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    //begin = systick();
    returnValueInt = byPPrintIsComplete(5);//判断打印是否完成
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintIsComplete('%s') failed, return code: %d\n", barCode, returnValueInt);
        goto ExitLine;
    }
    //    begin = systick() - begin;
    printf("PrinterPrintIsComplete(2)-time is %d\n",begin);

    byPFeedLine(1);//进纸
    returnValueBool = byPSetFont(0x00,0x10,0x01);
    if(!returnValueBool)
    {
        printf("PrinterSetFont(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = byPPrintString("直 选 票");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('直 选 票') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("① 3 2 2");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('① 3 2 2') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸

    returnValueInt = byPPrintString("② . . .");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('② . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("③ . . .");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('③ . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("④ . . .");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('④ . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("⑤ . . .");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('⑤ . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(2);//进纸
    returnValueBool = byPSetFont(0x00,0x00,0x01);
    if(!returnValueBool)

    {
        printf("PrinterSetFont(2)(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = byPPrintString("电话查询:123456短信查询:输入1234发至4321");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('电话查询') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("＊＊＊＊胡同＊＊号＊＊大厦");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('＊＊＊＊') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    returnValueInt = byPPrintString("\n\n\n");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('＊＊＊＊') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    //*****************************************************************************************************************
    //生成条码数据
    memset(barCode, 0x00, sizeof(barCode));
    barCodeLength = (rand() % 21);
    barCodeLength = (barCodeLength < 10) ? 10 : barCodeLength;
    for(ii = 0; ii < barCodeLength; ii++)
    {
        if(ii < (barCodeLength / 2))//数字
        {
            barCode[ii] = (rand() % 10) + '0';
        }
        else//字母
        {
            barCode[ii] = rand() % 27 + 'A';
        }
    }

    //*****************************************************************************************************************
    //打印条码
    memcpy(mbarCode, barCode, sizeof(barCode));
    returnValueInt = byPrintPDF417(16, 50, 4, 4, 3, barCode, 32, 2);
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintPDF417('%s') failed, return code: %d\n", barCode, returnValueInt);
        goto ExitLine;
    }

    byPCutPaper();//切纸

    returnValue = true;
    ExitLine:
        return  false;

    return returnValue;
}


JNIEXPORT jstring JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_GetPDFCode
        (JNIEnv *env, jobject) {

    return env->NewStringUTF(mbarCode);
}




int PrintBarcodeTicket(int codeType)
{
    //*****************************************************************************************************************
    //定义变量并初始化
    int	returnValueInt = PRINTER_NO_ERROR;
    int	returnValueBool = true;
    char   barCode[256] = {0x00};
    int	barCodeLength = 0;
    int	ii = 0;
    //unsigned char codeType = 0,
    unsigned char		setType = 0;
    int 	begin = 0;

    //*****************************************************************************************************************
    //输入选择条码类型
    //printf(Printer_Barcode_Item);
    //printf("Please input your select(0-9):");
    //scanf("%d",&codeType);ClearStdin//条码类型
    if(codeType < 0 || codeType > 9)
    {
        printf("The selection error!\n");
        goto ExitLine;
    }

    //*****************************************************************************************************************
    //设备初始化
//    returnValueInt = byPInit("/sdcard","/sdcard");
//    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
//    {
//        printf("PrinterInit failed, return code: %d\n", returnValueInt);
//        goto ExitLine;
//    }
//    else {
//        LOGI("Printer Init ok");
//    }

    //*****************************************************************************************************************
    //判断设备状态是否正常
//	returnValueBool = byPPrinterIsReady();
//	if(!returnValueBool)
//	{
//		printf("PrinterSetLeftMargin failed!\n");
//		goto ExitLine;
//	}

    //*****************************************************************************************************************
    //打印彩票数据
    byPSetAreaWidth(0x300); //设置打印幅面
    returnValueBool = byPSetLeftMargin(0x10);//设置左边界
    if(!returnValueBool)
    {
        printf("PrinterSetLeftMargin failed!\n");
        goto ExitLine;
    }

    returnValueBool = byPSetLineSpace(36);//设置行高
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(36) failed!\n");
        goto ExitLine;
    }

    returnValueBool = byPSetFont(0x00,0x10,0x01);//设置字体
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = byPPrintString("体彩<全国联网排列3>");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('体彩<全国联网排列3>') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(3);//进纸
    returnValueBool = byPSetLineSpace(24);//设置行间距
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(24) failed!\n");
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueBool = byPSetFont(0x00,0x00,0x01);//设置字体
    if(!returnValueBool)
    {
        printf("PrinterSetFont(0x00,0x00,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = byPPrintString("35073110603460810054   d2WIvi   64319331");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('35073110603460810054') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("销售点:06034      06073期 2006/03/22开奖");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('销售点:06034') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("倍:1  合计:2元       2006/03/22 12:26:49");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('倍:1  合计:2元') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueBool = byPSetFont(0x00,0x10,0x01);//设置字体
    if(!returnValueBool)
    {
        printf("PrinterSetFont(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = byPPrintString("直 选 票");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('直 选 票') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("① 3 2 2");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('① 3 2 2') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("② . . .");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('② . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("③ . . .");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('③ . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("④ . . .");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('④ . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("⑤ . . .");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('⑤ . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueBool = byPSetFont(0x00,0x00,0x01);
    if(!returnValueBool)
    {
        printf("PrinterSetFont(2)(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = byPPrintString("电话查询:123456短信查询:输入1234发至4321");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('电话查询') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(1);//进纸
    returnValueInt = byPPrintString("＊＊＊＊胡同＊＊号＊＊大厦");//打印字符串
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('＊＊＊＊') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    byPFeedLine(3);//进纸

    //*****************************************************************************************************************
    //生成条码数据
    memset(barCode, 0x00, sizeof(barCode));
    switch(codeType)
    {
        case 0://ITF
        {
            barCodeLength = (2 * (rand() % 128 + 1));//1-255(偶数)
            barCodeLength = (barCodeLength > 8) ? 8 : barCodeLength;
            for(ii = 0; ii < barCodeLength; ii++)
            {
                barCode[ii] = 48 + (rand() % (57 - 48 + 1));//48-57
            }
        }
            break;
        case 1://CodeBar
        {
            barCodeLength = (rand() % 254 + 1);//1-255
            barCodeLength = (barCodeLength > 8) ? 8 : barCodeLength;
            barCode[0] = (rand() % 4 + 65);//65-68(A-D)
            for(ii = 1; ii < barCodeLength - 1; ii++)
            {
                barCode[ii] = 48 + (rand() % (57 - 48 + 1));//48-57(仅演示)
            }
            barCode[barCodeLength - 1] = (rand() % 4 + 65);//65-68(A-D or (TE*N))
        }
            break;
        case 2://Code93
        {
            barCodeLength = (rand() % 255 + 1);//1-255
            barCodeLength = (barCodeLength > 10) ? 10 : barCodeLength;
            for(ii = 0; ii < barCodeLength; ii++)
            {
                barCode[ii] = '0' + (rand() % ('9' - '0' + 1));//0-9
            }
        }
            break;
        case 3://Code128
        {
            setType = 65 + (rand() % (67 - 65 + 1));//65-67
            printf("setType: %d\n",setType);
            if(setType == 0x43)//字符集C
            {
                barCodeLength = (2 * (rand() % 127 + 2));//2-255(偶数)
                barCodeLength = (barCodeLength > 12) ? 12 : barCodeLength;
                for(ii = 0; ii < barCodeLength; ii++)
                {
                    barCode[ii] = '0' + (rand() % ('9' - '0' + 1));//0-9
                }
            }
            else//字符集A\B
            {
                barCodeLength = (rand() % (255 - 2 + 1) + 2);//2-255
                barCodeLength = (barCodeLength > 8) ? 8 : barCodeLength;
                for(ii = 0; ii < barCodeLength; ii++)
                {
                    barCode[ii] = (rand() % 27) + 'A';
                }
            }
        }
            break;
        case 4://None
            break;
        case 5://UPC-A
        {
            barCodeLength = (rand() % (12 - 11 + 1) + 11);//11-12
            for(ii = 0; ii < barCodeLength; ii++)
            {
                barCode[ii] = 48 + (rand() % (57 - 48 + 1));//48-57
            }
        }
            break;
        case 6://UPC-E
        {
            barCodeLength = 12;
            barCode[0] = 0x30;
            for(ii = 1; ii < barCodeLength; ii++)
            {
                barCode[ii] = 0x30 + ii;
            }
            barCode[barCodeLength - 2] = 0x30;
            barCode[barCodeLength - 1] = 0x31;
        }
            break;
        case 7://EAN13
        {
            LOGI("printcodetype   %d",codeType);

            barCodeLength = (rand() % (13 - 12 + 1) + 12);//12-13
            for(ii = 0; ii < barCodeLength; ii++)
            {
                barCode[ii] = 48 + (rand() % (57 - 48 + 1));//48-57
            }
        }
            break;
        case 8://EAN8
        {
            barCodeLength = (rand() % (8 - 7 + 1) + 7);//7-8
            for(ii = 0; ii < barCodeLength; ii++)
            {
                barCode[ii] = 48 + (rand() % (57 - 48 + 1));//48-57
            }
        }
            break;
        case 9://Code39
        {
            barCodeLength = (rand() % (255 - 1 + 1) + 1);//1-255
            barCodeLength = (barCodeLength > 8) ? 8 : barCodeLength;
            barCode[0] = barCode [barCodeLength -1] = '*';
            for(ii = 1; ii < barCodeLength - 1; ii++)
            {
                if((ii % 2) == 0)
                {
                    barCode[ii] = 48 + (rand() % (57 - 48 + 1));//48-57
                }
                else
                {
                    barCode[ii] = 65 + (rand() % (90 - 65 + 1));//65-90
                }
            }
        }
            break;
    }

    //*****************************************************************************************************************
    //打印条码
    if(codeType != 4)
    {
        returnValueInt = byPrint1DBar(3, 60, barCode, codeType, setType);
        printf("barCodeLength: %d\n",barCodeLength);
        if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
        {
            printf("PrinterPrint1DBar('%s') failed, return code: %d\n", barCode, returnValueInt);
            goto ExitLine;
        }
    }
    byPCutPaper();//切纸

    //*****************************************************************************************************************
    //判断打印是否完成
    returnValueInt = byPPrintIsComplete(5);
    ExitLine:
        return  false;
    return TRUE;
}

JNIEXPORT jstring JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_GetLastErrStr
        (JNIEnv *env, jobject) {

        char	errStr[256] = {0x00};
        byPGetLastErrorStr(errStr, 200);
        return env->NewStringUTF(errStr);

}

JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintSample
        (JNIEnv *, jobject, jint mode) {

    PInit("/sdcard", "/sdcard");
    PSetCutterMode(mode);
    if (PrintPDF417() != NO_ERROR)
        return false;
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintAllString
        (JNIEnv *, jobject, jint mode) {

    PInit("/sdcard", "/sdcard");
    PSetCutterMode(mode);
    bool returnValueBool = true;
    char	errStr[256] = {0x00};

        byPSetAreaWidth(0x300); //设置打印幅面
        returnValueBool = byPSetLeftMargin(0x10);//设置左边界
        if(!returnValueBool)
        {
            printf("PrinterSetLeftMargin failed!\n");
            LOGI("PrinterSetLeftMargin failed!");
            goto ExitLine;

        }
        returnValueBool = byPSetLineSpace(36);//设置行高
        if(!returnValueBool)
        {
            printf("PrinterSetLineSpace(36) failed!\n");
            LOGI("PrinterSetLineSpace(36) failed!");
            goto ExitLine;
        }

        returnValueBool = byPSetFont(0x00,0x10,0x01);//设置字体
        if(!returnValueBool)
        {
            printf("PrinterSetLineSpace(0x00,0x10,0x01) failed!\n");
            LOGI("PrinterSetLineSpace(0x00,0x10,0x01) failed!");
            goto ExitLine;
        }

        returnValueInt = byPPrintString("测试字符串");//打印字符串
        byPFeedLine(4);
        byPPrintString("zxcvbnmlkjhgfdsaqwertyuiopZXCVBNMLKJHGFDSAQWERTYUIOP0123456789[]{}-=_+,./<>?`1~!@#$%^&*()");
        PCutPaper();
    LOGI("print string");

    ExitLine:
     return false;

    return  true;
}

JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintImage
        (JNIEnv *, jobject, jint){

        PInit("/sdcard", "/sdcard");
        returnValueInt = byPPrintDiskImage(0, 0, "/sdcard/conf/demo.bmp");
        PCutPaper();

        if(returnValueInt != PRINTER_NO_ERROR)
            return false;

        return  true;
}

JNIEXPORT bool JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintBlackBarcode
        (JNIEnv *, jobject, jint, jint size) {
    PInit("/sdcard", "/sdcard");
    int msize = size;
    if(isInit){
        if(size == 0){
            returnValueInt = byPPrintDiskImage(0, 0, "/sdcard/conf/block0.bmp");
            PFeedLine(4);
            returnValueInt = byPPrintDiskImage(0, 0, "/sdcard/conf/block0.bmp");
            PFeedLine(4);
            returnValueInt = byPPrintDiskImage(0, 0, "/sdcard/conf/block0.bmp");
            PCutPaper();
        }else if(size == 1){
            returnValueInt = byPPrintDiskImage(0, 0, "/sdcard/conf/block4.bmp");
            PCutPaper();
        }

    }
    if(returnValueInt != NO_ERROR)
        return  false;

    return true;
}

JNIEXPORT jstring JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintBarCode
        (JNIEnv *env, jobject, jint cutmode, jint codeType) {
    PInit("/sdcard", "/sdcard");
    int mcut = cutmode;
    int type = codeType;
    LOGI("codeTypessss=%d",type);
    switch (type){
        case 0: //pdf
            PrintPDF417();
            break;
        case 1: //QR

            break;
        case 2://EAN
            PrintBarcodeTicket(7);
            break;
        case 3://code39
            PrintBarcodeTicket(9);
            break;
        case 4://code128
            PrintBarcodeTicket(3);
            break;
        default:
            break;
    }

    char errStr[256] = {0x00};
    byPGetLastErrorStr(errStr, 200);
    return env->NewStringUTF(errStr);
}

JNIEXPORT bool JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintPaperMode
        (JNIEnv *, jobject, jint) {
    PInit("/sdcard", "/sdcard");

    bool ret = true;
    PSetPageMode(400 , 700 , 40 , 40);
    PSetAngle(90);
    PPrintString("中国体育彩票1中国体育彩票2中国体育彩票3中国体育彩票4");
    PPrintPage();
    ret = PCutPaper();

    PSetPageMode(400 , 700 , 40 , 40);
    PSetAngle(180);
    PPrintString("中国体育彩票1中国体育彩票2中国体育彩票3中国体育彩票4");
    PPrintPage();
    ret = PCutPaper();

    PSetPageMode(400 , 700 , 40 , 40);
    PSetAngle(270);
    PPrintString("中国体育彩票1中国体育彩票2中国体育彩票3中国体育彩票4");
    PPrintPage();
    ret = PCutPaper();

    PSetPageMode(400 , 700 , 40 , 40);
    PSetAngle(0);
    PPrintString("中国体育彩票1中国体育彩票2中国体育彩票3中国体育彩票4");
    PPrintPage();
    ret = PCutPaper();

    if(!ret)
        return false;


    return true;
}

JNIEXPORT bool JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintString
        (JNIEnv *, jobject , jstring str) {

    int ret = byPPrintString("cutpaper test\n");

    if(ret != NO_ERROR)
        return false;
    PCutPaper();

    char err[256] = {0x00};
    byPGetLastErrorStr(err,200);
    LOGI("byPPrintString return %d err = %s",ret,err);


    return true;
}

JNIEXPORT bool JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_CutPaper
        (JNIEnv *, jobject) {
    bool ret = PCutPaper();

}

JNIEXPORT bool JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PSetPageMode
        (JNIEnv *, jobject, jint width ,jint heigt, jint leftTop_x, jint leftTop_y) {
    int wid = width;
    int h = heigt;
    int leftx = leftTop_x;
    int lefty = leftTop_y;
    PSetPageMode(wid , h , leftx , lefty);
}

JNIEXPORT jstring JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_GetPrintHwInfo
        (JNIEnv *env, jobject) {

//    PInit("/sdcard", "/sdcard");
    char info[1024] = {0x00};
    bool ret = PGetHWInformation(info, 1000);
    if (!ret) {
        char err[256] = {0x00};
        byPGetLastErrorStr(err, 200);
        LOGI("gethwinfo return = %d, hwinfo = %s", ret, info);
        return env->NewStringUTF(err);

    }
    else {
        LOGI("gethwinfo return = %d, hwinfo = %s", ret, info);

    }
    bool isready = byPPrinterIsReady();
    LOGI("printerIsReady return = %d",isready);

    return env->NewStringUTF(info);
}

JNIEXPORT jstring JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrinterStatus
        (JNIEnv *env, jobject) {

//    bool isready = PPrinterIsReady();
//    if(!isready) {
        if (byPInit("/sdcard","/sdcard") != PRINTER_NO_ERROR) {
            char errStr[256] = {0x00};
            byPGetLastErrorStr(errStr, 200);
            LOGI("printer init false! err = %s",errStr);
            return env->NewStringUTF(errStr);
        } else{
            if(PPrinterIsReady())
                return  env->NewStringUTF("正常");
        }

//    }
//    LOGI("isready =%d",isready);
    return  env->NewStringUTF("正常");
}

JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_GetAuthority
        (JNIEnv *, jobject) {


    system("mv /sdcard/conf/HWISNBCPrinter0.ini /sdcard/conf/HWISNBCPrinter.ini");


}




