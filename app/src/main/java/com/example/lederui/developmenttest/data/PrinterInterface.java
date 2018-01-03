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
        System.loadLibrary("HWISNBCPrinter");
        System.loadLibrary("native-printer");
    }

    public static native boolean PrintInit();

    public  static native  String GetLastErrStr();

    public  static  native  int GetLastErrCode();

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

    public static native int PrinterStatus();

    public static native boolean GetAuthority();

    public static native String GetPDFCode();

    public  int NO_ERROR = 0;

    public final int NO_PRINTER = 1;

    public final int DATA_LINE_ERROR = 2;

    public final int POWER_ERROR = 3;

    public final int PRINTER_IS_BUSY = 4;

    public final int TIME_OUT = 5;

    public final int GET_HWVERSION_ERROR = 6;

    public final int START_ERROR = 7;

    public final int COVER_ERROR = 8;

    public final int PAPER_EMPTY_ERROR = 9;

    public final int PAPER_JAM_ERROR = 10;

    public final int PAPER_FEW_ERROR = 11;

    public  final int PRINT_NOT_COMPELTE =12 ;

    public final int PRINTER_CUTTER_ERROR = 13;

    public final int SET_CUTMODE_ERROR = 20;

    public final int OTHER_ERROR = 500;

}
