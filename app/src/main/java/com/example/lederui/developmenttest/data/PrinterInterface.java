package com.example.lederui.developmenttest.data;

/**
 * Created by chandler on 2017/5/11.
 */

public class PrinterInterface {
    static{
        System.loadLibrary("usb1.0");
        System.loadLibrary("ConfigFileINI");
        System.loadLibrary("flog");
        System.loadLibrary("bitmap");
        System.loadLibrary("scanprn");
        System.loadLibrary("ScanPrnWrap");
        System.loadLibrary("native-printer");
    }

    public static native boolean PrintInit();

    public  static native  String GetLastErrStr();

    public native boolean SetCutMode(int mode);

    //change ini file , set allcut or halfcut
    public native void SetAllcutOrHalfcut(int mode);

    public native boolean PrintSample(int cutmode);

    public native boolean PrintAllString(int cutmode);

    public native boolean PrintImage(int cutmode);

    public native boolean PrintBlackBarcode(int cutmode,int size);

    public native String PrintCodePaper(int cutmode,int code);

    public native String PrintContinue(int cutmode , int time);

    public native boolean PrintPaperMode(int cutmode);

    public native String PrintSpeedTest(int cutmode);

    public native String PrintCutPaperSpeed(int cutmode);

    public native String PrintBarCode(int cutmode ,int codeType);

    public native  boolean PrintString(String str);

    public native boolean CutPaper();

    public native boolean PSetPageMode(int width, int height, int leftTop_x, int leftTop_y);

    public static native String GetPrintHwInfo();

    public static native String PrinterStatus();

    public static native boolean GetAuthority();
}
