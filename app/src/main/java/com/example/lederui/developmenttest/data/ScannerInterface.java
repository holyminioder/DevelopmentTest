package com.example.lederui.developmenttest.data;

/**
 * Created by Administrator on 2017/12/9 0009.
 */

public class ScannerInterface {
    static {
        System.loadLibrary("scanner");
        System.loadLibrary("native-scanner");
    }

    public static native int SInit();

    public static native int SQueryCapability();

    public static native boolean SGetScanDpi(Integer widthDpi, Integer heightDpi);

    public static native boolean SGetBrandDpi(Integer widthDpi, Integer heightDpi);

    public static native boolean SGetHWInformation(byte[] hwInfo, int length);

    public static native boolean SGetSWVersion(byte[] swVersion, int length);

    public static native int SGetLastErrorCode();

    public static native String SGetLastErrorStr(byte[] errStr, int length);

    public static native boolean SStart();

    public static native boolean SStop();

    public static native boolean ScanIsComplete();

    public static native boolean ScannerIsReady();

    public static native boolean SGetOriginImageSize(Integer width, Integer height, Integer bufsize);

    public static native int SGetOriginImage(byte[] image, Integer bufferLen);

    public static native int SGetTicketInfo(byte[] ticketInfo, Integer bufferLen);

    public static native int SPrintBrandImage(byte[] image, int index, int xpos, int ypos);

    public static native int SPrintSelfDefBrandImage(byte[] image, int xpos, int ypos);

    public static native boolean SRollBack();

    public static native boolean SRecognizeItem(int posX, int posY, int width, int height, byte[] image, byte[] result);

    public static native boolean SAdjustSensibility(Integer currentSens, Integer adjustSens);

}
