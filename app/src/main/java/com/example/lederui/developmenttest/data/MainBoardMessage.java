package com.example.lederui.developmenttest.data;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Created by holyminier on 2017/5/2.
 */

public class MainBoardMessage {

    private Handler mHandler;
    private UsbManager mManager;
    private UsbDevice mUsbDevice;
    private HashMap<String, UsbDevice> deviceList;
    private UsbInterface mInterface;
    private int mStatus;

    //获取cpu型号
    public static String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuinfo = {"", ""};
        String[] arrayOfString;
        try {
            FileReader reader = new FileReader(str1);
            BufferedReader buffer = new BufferedReader(reader, 8192);
            str2 = buffer.readLine();
            arrayOfString = str2.split("s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuinfo[0] = cpuinfo[0] + arrayOfString[i] + "";
            }
            reader.close();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpuinfo[0];
    }

    //获取cpu核心数
    public static int getNumCores() {
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    //获取屏幕分辨率
    public static String getMetris(Activity activity) {
        String result = "";
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        result = dm.widthPixels + "*" + dm.heightPixels;
        return result;
    }

    //获取设备USB接口数
    public static int getUsbInterface(Context context) {
        UsbDevice usbDevice = null;
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            usbDevice = deviceIterator.next();
        }
        Log.e("InterfaceCount", String.valueOf(usbDevice.getInterfaceCount()));
        return usbDevice.getInterfaceCount();
    }
    // 实时获取CPU当前频率（单位KHZ）
    public static double getCurCpuFreq() {
        String result = "N/A";
        try {
            FileReader fr = new FileReader(
                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long frequency = Integer.parseInt(result);
        double mainFrequency = (double)frequency / 1000 / 1000;
        return mainFrequency;
    }

    //获取运行内存大小
    public static String getTotalRam() {
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (firstLine != null) {
            totalRam = (int) Math.ceil(new Float(Float.valueOf(firstLine) / (1024 * 1024)).doubleValue());
        }
        return totalRam + "GB";
    }

    //存储容量
    public static String getStorageSize(){
        long rom = getTotalInternalMemorySize();
        long sdCard = getSDCardMemory();
        long storage = rom + sdCard;
        String storageSize = formatFileSize(storage, true);
        return storageSize;
    }

    private static DecimalFormat fileIntegerFormat = new DecimalFormat("#0");
    private static DecimalFormat fileDecimalFormat = new DecimalFormat("#0.#");

    public static String formatFileSize(long size, boolean isInteger) {
        DecimalFormat df = isInteger ? fileIntegerFormat : fileDecimalFormat;
        String fileSizeString = "0M";
        if (size < 1024 && size > 0) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1024 * 1024) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1024 * 1024 * 1024) {
            fileSizeString = df.format((double) size / (1024 * 1024)) + "M";
        } else {
            fileSizeString = df.format((double) size / (1024 * 1024 * 1024)) + "G";
        }
        return fileSizeString;
    }

    //获取ROM总内存大小
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    //sdCard大小
    public static long getSDCardMemory() {
        long sdCardInfo= 0;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long bSize = sf.getBlockSize();
            long bCount = sf.getBlockCount();
            sdCardInfo = bSize * bCount;//总大小
        }
        return sdCardInfo;
    }
}
