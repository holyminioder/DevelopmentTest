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
* Printer????????
**********************************************************************************************************************/
#define	PRINTER_START_CODE			0x00
#define	PRINTER_NO_ERROR			0x00
#define	PRINTER_TIME_OUT			(PRINTER_NO_ERROR - 0x01)		//???
#define	PRINTER_DATA_ERROR			(PRINTER_NO_ERROR - 0x02)		//?????????
#define	PRINTER_PAPER_ERROR			(PRINTER_NO_ERROR - 0x03)		//??????
#define	PRINTER_POWER_ERROR			(PRINTER_NO_ERROR - 0x04)		//????????
#define	PRINTER_COVER_ERROR			(PRINTER_NO_ERROR - 0x05)		//????
#define	PRINTER_PAPER_JAM			(PRINTER_NO_ERROR - 0x06)		//???
#define	PRINTER_NOT_COMPLETE			(PRINTER_NO_ERROR - 0x07)		//??????δ???(????????)
#define	PRINTER_OTHER_ERROR			(PRINTER_NO_ERROR - 0x08)		//????????(??????)
#define	PRINTER_TABPAR_NONE			(PRINTER_NO_ERROR - 0x09)		//???????
#define	PRINTER_HAVE_NOT_INIT		(PRINTER_NO_ERROR - 0x0A)		//δ?????
//#define	PRINTER_INVALID_ARGUMNET		(PRINTER_NO_ERROR - 0x0B)		//??????Ч
#define 	PRINTER_MODE_SWITCH_ERROR		(PRINTER_NO_ERROR - 0x0C)		//???л?????
#define 	PRINTER_HEAD_ERROR			(PRINTER_NO_ERROR - 0x0D)		//???????
#define 	PRINTER_CUT_ERROR			(PRINTER_NO_ERROR - 0x0E)		//?е?????
#define	PRINTER_NOT_FULL			(PRINTER_NO_ERROR - 0x10)		//?????????
#define	PRINTER_FORBID			(PRINTER_NO_ERROR - 0x11)		//????????(???????????)
#define	PRINTER_NOSUPPORT			(PRINTER_NO_ERROR - 0x12)		//?????
#define	PRINTER_MIN_CODE			(PRINTER_NO_ERROR - 0x13)		//??С?????
#define	PRINTER_INVALID_ERROR_CODE		(PRINTER_NO_ERROR - 0x14)		//??Ч??????


int	returnValueInt = PRINTER_NO_ERROR;
bool isInit = false;
char  mbarCode[256] = {0x00};



JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintInit
        (JNIEnv * env, jobject)
{
    char errStr[256] = {0x00};

    returnValueInt = PInit("/sdcard/","/sdcard/");
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B) {
        //printf("PrinterInit failed, return code: %d\n", returnValueInt);
        LOGI("Init return %d", returnValueInt);
        PGetLastErrorStr(errStr, 200);
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
    PGetLastErrorStr(err,200);
    LOGI("setcutmode(1) return %d err = %s",ret,err);


    return  ret;
}

bool PrintStringForTest() {
    LOGI("in PrintPDF417");
    int		returnValueInt = PRINTER_NO_ERROR;
    bool		returnValueBool = false;
    bool		returnValue = false;
    char    	barCode[256] = {0x00};
    int		barCodeLength = 0;
    int		ii = 0;
    int 		begin = 0;


    bool ret = PPrinterIsReady();
    int errcode = 0;

    if (!ret) {
        return false;
    }

    //*****************************************************************************************************************
    //????????
    if(!PSetLineMode())
    {
        goto ExitLine;
    }

    //*****************************************************************************************************************
    //?????????
    if(!PSetBottomMargin(200))
    {
        goto ExitLine;
    }

    //*****************************************************************************************************************
    //?ж??豸?????????
//    PPrinterIsReady();

    //*****************************************************************************************************************
    //??????????
    returnValueBool = PSetLeftMargin(0x0);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetLeftMargin failed!\n");
        fflush(stdout);
        goto ExitLine;
    }

    returnValueBool = PSetLineSpace(10);//?????и?
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(36) failed!\n");
        fflush(stdout);
        goto ExitLine;
    }

    returnValueBool = PSetFont(0x00, 0x11,0x01);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(0x33,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = PPrintString("演示票<全国联网排列5>\n\n");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('演示票<全国联网排列5>') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    returnValueBool = PSetLineSpace(14);//?????м??
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(24) failed!\n");
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueBool = PSetFont(0x00,0x00,0x01);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetFont(0x00,0x00,0x01) failed!\n");
        goto ExitLine;
    }

    PFeedLine(1);
    returnValueInt = PPrintString("35073110603460810054   d2WIvi   64319331\n");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('35073110603460810054') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    returnValueInt = PPrintString("销售点:06034      06073期 2006/03/22开奖");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('销售点:06034') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }
    PPrintString("\n");
    PFeedLine(1);
    returnValueInt = PPrintString("倍:1  合计:2元       2006/03/22 12:26:49\n");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('倍:1  合计:2元') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }


    PFeedLine(2);//???
    returnValueBool = PSetFont(0x00,0x10,0x01);
    if(!returnValueBool)
    {
        printf("PrinterSetFont(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = PPrintString("单 式 票");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('单 式 票') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(2);//???
    returnValueInt = PPrintString("① 3 3 2 1 5");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('① 3 3 2 1 5') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(2);//???

    returnValueInt = PPrintString("② 5 8 1 4 6");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('② 5 8 1 4 6') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(2);//???
    returnValueInt = PPrintString("③ 3 3 2 6 2");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('③ 3 3 2 6 2') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(2);//???
    returnValueInt = PPrintString("④ 5 8 3 1 3");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('④ 5 8 3 1 3') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(2);//???
    returnValueInt = PPrintString("⑤ 3 3 2 4 7");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('⑤ 3 3 2 4 7') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(2);//???
    returnValueBool = PSetFont(0x00,0x00,0x01);
    if(!returnValueBool)

    {
        printf("PrinterSetFont(2)(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = PPrintString("电话查询:123456短信查询:输入1234发至4321");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('电话查询') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(2);//???
    returnValueInt = PPrintString("＊＊＊＊胡同＊＊号＊＊大厦");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('＊＊＊＊') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    returnValueInt = PPrintString("\n\n\n");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('＊＊＊＊') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    //*****************************************************************************************************************
    //????????????
    memset(barCode, 0x00, sizeof(barCode));
    barCodeLength = (rand() % 21);
    barCodeLength = (barCodeLength < 10) ? 10 : barCodeLength;
    for(ii = 0; ii < barCodeLength; ii++)
    {
        if(ii < (barCodeLength / 2))//????
        {
            barCode[ii] = (rand() % 10) + '0';
        }
        else//???
        {
            barCode[ii] = rand() % 27 + 'A';
        }
    }


    PCutPaper();//???

    returnValue = true;
    ExitLine:
    return  false;

    return returnValue;
}

bool PrintPDF417() {
    //*****************************************************************************************************************
    //??????????????
    LOGI("in PrintPDF417");
    int		returnValueInt = PRINTER_NO_ERROR;
    bool		returnValueBool = false;
    bool		returnValue = false;
    char    	barCode[256] = {0x00};
    int		barCodeLength = 0;
    int		ii = 0;
    int 		begin = 0;


    bool ret = PPrinterIsReady();
    int errcode = 0;

    if (!ret) {
        return false;
    }

    //*****************************************************************************************************************
    //????????
    if(!PSetLineMode())
    {
        goto ExitLine;
    }

    //*****************************************************************************************************************
    //?????????
    if(!PSetBottomMargin(200))
    {
        goto ExitLine;
    }

    //*****************************************************************************************************************
    //?ж??豸?????????
//    PPrinterIsReady();

    //*****************************************************************************************************************
    //??????????
    returnValueBool = PSetLeftMargin(0x0);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetLeftMargin failed!\n");
        fflush(stdout);
        goto ExitLine;
    }

    returnValueBool = PSetLineSpace(10);//?????и?
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(36) failed!\n");
        fflush(stdout);
        goto ExitLine;
    }

    returnValueBool = PSetFont(0x00, 0x11,0x01);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(0x33,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = PPrintString("演示票<全国联网排列3>\n\n");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('演示票<全国联网排列3>') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    returnValueBool = PSetLineSpace(14);//?????м??
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(24) failed!\n");
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueBool = PSetFont(0x00,0x00,0x01);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetFont(0x00,0x00,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = PPrintString("35073110603460810054   d2WIvi   64319331\n");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('35073110603460810054') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    returnValueInt = PPrintString("销售点:06034      06073期 2006/03/22开奖");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('销售点:06034') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }
    PPrintString("\n");

    returnValueInt = PPrintString("倍:1  合计:2元       2006/03/22 12:26:49\n");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('倍:1  合计:2元') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    //begin = systick();
//    returnValueInt = PPrintIsComplete(5);//?ж?????????
//    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
//    {
//        printf("PrinterPrintIsComplete('%s') failed, return code: %d\n", barCode, returnValueInt);
//        goto ExitLine;
//    }
//    //    begin = systick() - begin;
//    printf("PrinterPrintIsComplete(2)-time is %d\n",begin);

    PFeedLine(1);//???
    returnValueBool = PSetFont(0x00,0x10,0x01);
    if(!returnValueBool)
    {
        printf("PrinterSetFont(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = PPrintString("单 式 票");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('单 式 票') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("① 3 3 2");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('① 3 3 2') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???

    returnValueInt = PPrintString("② 5 8 1");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('② 5 8 1') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("③ 3 3 2");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('③ 3 3 2') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("④ 5 8 3");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('④ 5 8 3') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("⑤ 3 3 2");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('⑤ 3 3 2') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(2);//???
    returnValueBool = PSetFont(0x00,0x00,0x01);
    if(!returnValueBool)

    {
        printf("PrinterSetFont(2)(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = PPrintString("电话查询:123456短信查询:输入1234发至4321");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('电话查询') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("＊＊＊＊胡同＊＊号＊＊大厦");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('＊＊＊＊') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    returnValueInt = PPrintString("\n\n\n");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('＊＊＊＊') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    //*****************************************************************************************************************
    //????????????
    memset(barCode, 0x00, sizeof(barCode));
    barCodeLength = (rand() % 21);
    barCodeLength = (barCodeLength < 10) ? 10 : barCodeLength;
    for(ii = 0; ii < barCodeLength; ii++)
    {
        if(ii < (barCodeLength / 2))//????
        {
            barCode[ii] = (rand() % 10) + '0';
        }
        else//???
        {
            barCode[ii] = rand() % 27 + 'A';
        }
    }

    //*****************************************************************************************************************
    //???????
    memcpy(mbarCode, barCode, sizeof(barCode));
    returnValueInt = PrintPDF417(15, 38, 20 , 6, 3, barCode, 32, 2);
//    returnValueInt = PrintPDF417(10, 10, 1 , 1, 3, barCode, 32, 2);
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintPDF417('%s') failed, return code: %d\n", barCode, returnValueInt);
        goto ExitLine;
    }

    PCutPaper();//???

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

    LOGI("printer","ITF code");
    //*****************************************************************************************************************
    //??????????????
    int	returnValueInt = PRINTER_NO_ERROR;
    int	returnValueBool = true;
    char   barCode[256] = {0x00};
    int	barCodeLength = 0;
    int	ii = 0;
    //unsigned char codeType = 0,
    unsigned char		setType = 0;
    int 	begin = 0;

    //*****************************************************************************************************************
    //???????????????
    //printf(Printer_Barcode_Item);
    //printf("Please input your select(0-9):");
    //scanf("%d",&codeType);ClearStdin//????????
    if(codeType < 0 || codeType > 9)
    {
        printf("The selection error!\n");
        goto ExitLine;
    }



    //*****************************************************************************************************************
    //??????????
    PSetAreaWidth(0x300); //??????????
    returnValueBool = PSetLeftMargin(0x0);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetLeftMargin failed!\n");
        goto ExitLine;
    }

    returnValueBool = PSetLineSpace(36);//?????и?
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(36) failed!\n");
        goto ExitLine;
    }

    returnValueBool = PSetFont(0x00,0x10,0x01);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = PPrintString("体彩<全国联网排列3>");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('体彩<全国联网排列3>') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(3);//???
    returnValueBool = PSetLineSpace(24);//?????м??
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(24) failed!\n");
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueBool = PSetFont(0x00,0x00,0x01);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetFont(0x00,0x00,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = PPrintString("35073110603460810054   d2WIvi   64319331");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('35073110603460810054') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("销售点:06034      06073期 2006/03/22开奖");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('销售点:06034') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("倍:1  合计:2元       2006/03/22 12:26:49");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('倍:1  合计:2元') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueBool = PSetFont(0x00,0x10,0x01);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetFont(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = PPrintString("直 选 票");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('直 选 票') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("① 3 2 2");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('① 3 2 2') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("② . . .");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('② . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("③ . . .");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('③ . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("④ . . .");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('④ . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("⑤ . . .");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('⑤ . . .') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueBool = PSetFont(0x00,0x00,0x01);
    if(!returnValueBool)
    {
        printf("PrinterSetFont(2)(0x00,0x10,0x01) failed!\n");
        goto ExitLine;
    }

    returnValueInt = PPrintString("电话查询:123456短信查询:输入1234发至4321");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('电话查询') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(1);//???
    returnValueInt = PPrintString("＊＊＊＊胡同＊＊号＊＊大厦");//????????
    if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
    {
        printf("PrinterPrintString('＊＊＊＊') failed, return code: %d\n", returnValueInt);
        goto ExitLine;
    }

    PFeedLine(3);//???


    //*****************************************************************************************************************
    //????????????
    memset(barCode, 0x00, sizeof(barCode));
    switch(codeType)
    {
        case 0://ITF
        {


            barCodeLength = (2 * (rand() % 128 + 1));//1-255(???)
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
                barCode[ii] = 48 + (rand() % (57 - 48 + 1));//48-57(?????)
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
            if(setType == 0x43)//?????C
            {
                barCodeLength = (2 * (rand() % 127 + 2));//2-255(???)
                barCodeLength = (barCodeLength > 12) ? 12 : barCodeLength;
                for(ii = 0; ii < barCodeLength; ii++)
                {
                    barCode[ii] = '0' + (rand() % ('9' - '0' + 1));//0-9
                }
            }
            else//?????A\B
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
    //???????
    if(codeType != 4)
    {
        returnValueInt = Print1DBar(5, 80, barCode, codeType, setType);
        printf("barCodeLength: %d\n",barCodeLength);

        if(returnValueInt != PRINTER_NO_ERROR && returnValueInt != 0x0B)
        {
            printf("PrinterPrint1DBar('%s') failed, return code: %d\n", barCode, returnValueInt);
            goto ExitLine;
        }
    }
    PCutPaper();//???

    //*****************************************************************************************************************
    //?ж?????????

    ExitLine:
    return  false;
    return TRUE;
}

JNIEXPORT jstring JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_GetLastErrStr
        (JNIEnv *env, jobject) {

    char	errStr[256] = {0x00};
    PGetLastErrorStr(errStr, 200);
    return env->NewStringUTF(errStr);

}

JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintSample
        (JNIEnv *, jobject, jint mode ) {

    int ret = PGetLastErrorCode();
    if(ret != 0)
        return false;
    PSetCutterMode(mode);
    if (PrintPDF417() != PRINTER_NO_ERROR)
        return false;
    return true;
}

JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintSample2
        (JNIEnv *, jobject, jint mode) {

    int ret = PGetLastErrorCode();
    if(ret != 0)
        return false;
    PSetCutterMode(mode);
    if (PrintStringForTest() != PRINTER_NO_ERROR)
        return false;
    return true;

}

JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintAllString
        (JNIEnv *, jobject, jint mode) {

//    PInit("/sdcard/conf", "/sdcard/conf");
    PSetCutterMode(mode);
    bool returnValueBool = true;
    char	errStr[256] = {0x00};

    PSetAreaWidth(0x300); //??????????
    returnValueBool = PSetLeftMargin(0x0);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetLeftMargin failed!\n");
        LOGI("PrinterSetLeftMargin failed!");
        return false;

    }
    returnValueBool = PSetLineSpace(36);//?????и?
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(36) failed!\n");
        LOGI("PrinterSetLineSpace(36) failed!");
        return false;
    }

    returnValueBool = PSetFont(0x00,0x10,0x01);//????????
    if(!returnValueBool)
    {
        printf("PrinterSetLineSpace(0x00,0x10,0x01) failed!\n");
        LOGI("PrinterSetLineSpace(0x00,0x10,0x01) failed!");
        return false;
    }

    returnValueInt = PPrintString("测试字符串");//????????
    PFeedLine(4);
    returnValueInt =PPrintString("zxcvbnmlkjhgfdsaqwertyuiopZXCVBNMLKJHGFDSAQWERTYUIOP0123456789[]{}-=_+,./<>?`1~!@#$%^&*()");
    PFeedLine(4);
    PSetFont(0x00, 0x11, 0x01);
    PPrintString("1 4 3 8");
    PCutPaper();

    if(returnValueInt != NO_ERROR){
        return false;
    }

    return  true;
}

JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintImage
        (JNIEnv *, jobject, jint){

//    PInit("/sdcard/conf", "/sdcard/conf");
    returnValueInt = PPrintDiskImage(0, 0, "/sdcard/config/demo.bmp");
    PCutPaper();

    if(returnValueInt != PRINTER_NO_ERROR)
        return false;

    return  true;
}

JNIEXPORT bool JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintBlackBarcode
        (JNIEnv *, jobject, jint, jint size) {
//    PInit("/sdcard/conf", "/sdcard/conf");
    int msize = size;
    if(isInit){
        if(size == 0){
            returnValueInt = PPrintDiskImage(0, 0, "/sdcard/config/block0.bmp");
            PFeedLine(4);
            returnValueInt = PPrintDiskImage(0, 0, "/sdcard/config/block0.bmp");
            PFeedLine(4);
            returnValueInt = PPrintDiskImage(0, 0, "/sdcard/config/block0.bmp");
            PCutPaper();
        }else if(size == 1){
            returnValueInt = PPrintDiskImage(0, 0, "/sdcard/config/block4.bmp");
            PCutPaper();
        }

    }
    if(returnValueInt != NO_ERROR)
        return  false;

    return true;
}

int PrintQR(){
    int flag = 0;
    int module_width =255;
    int module_height =255;
    int border_size =4;
    int version= 0;
    int err_correct_level = 3;

    char databuf[] = {"Latech Machine"} ;
//    strcpy(databuf,"Latech Machine");
    int length = strlen(databuf);

    flag = PPrintQRCode( module_width,  module_height,  border_size,  version,
                         err_correct_level,databuf, length);

    return flag;
}

JNIEXPORT jstring JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintBarCode
        (JNIEnv *env, jobject, jint cutmode, jint codeType) {
//    PInit("/sdcard/conf", "/sdcard/conf");
    PSetCutterMode(cutmode);
    int mcut = cutmode;
    int type = codeType;
    LOGI("codeTypessss=%d",type);
    switch (type){
        case 0: //pdf
            PrintPDF417();
            break;
        case 1: //QR
            PrintQR();

            PCutPaper();
//            LOGI("PCutPaper=%d",f);
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
        case 5:
            PrintBarcodeTicket(0);
        default:
            break;
    }

    char errStr[256] = {0x00};
    PGetLastErrorStr(errStr, 200);
    return env->NewStringUTF(errStr);
}

JNIEXPORT bool JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintPaperMode
        (JNIEnv *, jobject, jint) {
    PInit("/sdcard/", "/sdcard/");

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

    int ret = PPrintString("cutpaper test\n");

    if(ret != NO_ERROR)
        return false;
    PCutPaper();

    char err[256] = {0x00};
    PGetLastErrorStr(err,200);
    LOGI("PPrintString return %d err = %s",ret,err);


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

    char info[1024] = {0x00};
    bool ret = PGetHWInformation(info, 1000);
    if (!ret) {
        char err[256] = {0x00};
        PGetLastErrorStr(err, 200);
        LOGI("gethwinfo return = %d, hwinfo = %s", ret, info);
        return env->NewStringUTF(err);

    }
    else {
        LOGI("gethwinfo return = %d, hwinfo = %s", ret, info);

    }
    bool isready = PPrinterIsReady();
    LOGI("printerIsReady return = %d",isready);

    return env->NewStringUTF(info);
}

JNIEXPORT jint JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrinterStatus
        (JNIEnv *env, jobject) {

    bool ret = PPrinterIsReady();
    int errcode = PGetLastErrorCode();

    if (errcode != 0 || ret == false) {
        LOGI("PGetLastErrorCode return=%d" , errcode);
        return errcode;
    }
    LOGI("errcode = %d" ,errcode);

    return errcode;
}

JNIEXPORT jboolean JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_GetAuthority
        (JNIEnv *, jobject) {


}

JNIEXPORT jint JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_GetLastErrCode
        (JNIEnv *, jobject) {
    int errCode = PGetLastErrorCode();
    return  errCode;
}


JNIEXPORT jint JNICALL Java_com_example_lederui_developmenttest_data_PrinterInterface_PrintQRCode
        (JNIEnv *, jobject){

    int flag = 0;
    int module_width =255;
    int module_height =255;
    int border_size =4;
    int version= 0;
    int err_correct_level = 3;

    char databuf[] = {"Latech Machine"} ;
//    strcpy(databuf,"Latech Machine");
    int length = strlen(databuf);

    flag = PPrintQRCode( module_width,  module_height,  border_size,  version,
                         err_correct_level,databuf, length);

    return flag;
}








