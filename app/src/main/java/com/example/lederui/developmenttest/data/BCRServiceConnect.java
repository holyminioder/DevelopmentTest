package com.example.lederui.developmenttest.data;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ztec.bcr.BarcoderReaderService;
import com.ztec.bcr.TBarcoderReader;

/**
 * Created by chandler on 2017/6/14.
 */


public class BCRServiceConnect implements ServiceConnection {

    public BarcoderReaderService mBCRReaderService;
    public TBarcoderReader tbcrReader = new TBarcoderReader();

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBCRReaderService = ((BarcoderReaderService.UsbBinder) service).getService();
        tbcrReader.setService(mBCRReaderService);

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }



}
