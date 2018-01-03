package com.example.lederui.developmenttest.data;

/**
 * Created by chandler on 2017/8/23.
 */

public class BCRInterface {
    static {
        System.loadLibrary("HWILatechBCR-uc");
        System.loadLibrary("native-hscanner");
    }

    public static native int BCRInit();
    public static native void BCRClose();
    public static native boolean BCRStartScan();
    public static native boolean BCRStopScan();
    public static native boolean BCRScanIsComplete();
    public static native boolean BCRIsReady();
    public static native int BCRGetTicketInfo(byte[] pSrc, Integer nW);
    public static native boolean BCRGetHWInformation(byte[] hwInfo  , int length);
    public static native boolean BCRBeep(int tone);

    public static native void BCRUserLEDOff();

    public static native boolean BCRUserLEDOn(int mode);

    public static native boolean BCRAimOff();

    public static native boolean BCRAimOn();

    public static native boolean BCRSetScanMode(int mode);

    public static native boolean BCRGetScanMode(Integer mode);

    public static native void BCRGetTriggerStatus(Integer status);

    public static native void BCRGetScanDpi(Integer widthDpi, Integer heightDpi);

    public static native boolean BCRGetImageSize(Integer width,Integer height, Integer bufsize);

    public static native void BCREnableBeep();

    public static native void BCRDisableBeep();

    public static native void BCRClearBuffer();

    public static native String BCRGetSWVersion();

    public static native int BCRGetImage(byte[] image, Integer bufferLen);

    public static native boolean BCRResetComm();

    public static native boolean BCRWakeup();

    public static native boolean BCRSleep(int time);

    public static native boolean BCRDisableCode(int codeType);

    public static native boolean BCREnableCode(int codeType);

    public static native boolean BCRDisable();

    public static native boolean BCREnable();

    public static native int BCRQueryCapability();

    public static native String BCRGetLastErrorStr();

    public static native int BCRGetLastErrorCode();

    public static native boolean BCRGetDataLength(Integer length);


}
