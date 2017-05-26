package com.example.lederui.developmenttest.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lederui.developmenttest.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ztec.bcr.BarcoderReaderService;
import com.ztec.bcr.TBarcoderReader;

import java.io.UnsupportedEncodingException;
import java.util.Set;

/**
 * Created by holyminier on 2017/4/21.
 */

/** 条码识读Fragment */
public class BarcodeReaderFragment extends Fragment {

    static {
        System.loadLibrary("bcr");
    }

    private View mView;
    private GoogleApiClient client;
    private TextView mBCRStatus;
    private TextView mBCRCodeType;
    private TextView mScandata_view;
    private BarcoderReaderService bcrService;
    private TBarcoderReader tbcr;
    private Spinner mSprinner;
    private boolean isScan = true;
    private int mScanMode = 2; //2  自动模式 1 手动模式
    private String TICKET_INFO = "ticketInfo";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_barcode_reader, container, false);
        mView = view;
        tbcr = new TBarcoderReader();

        InitView();

        return view;
    }


    private void InitView() {

        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
        mBCRStatus = (TextView) mView.findViewById(R.id.bcr_status);
        mBCRCodeType = (TextView) mView.findViewById(R.id.bcr_codetype);
        mScandata_view = (TextView) mView.findViewById(R.id.bcrscanData_view);
        mSprinner = (Spinner) mView.findViewById(R.id.scanmode_spinner);
        mSprinner.setSelection(0,true);
        mSprinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    stopScan();
                    mScanMode = 2;
                    StartScan();
                }else if(position == 1){
                    stopScan();
                    mScanMode = 1;
                    StartScan();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from BarcoderReaderService
        startService(BarcoderReaderService.class, usbConnection, null); // Start BarcoderReaderService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(mUsbReceiver);
        getContext().unbindService(usbConnection);
    }


    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onDestroy() {
        tbcr.release();
        super.onDestroy();
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BarcoderReaderService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(BarcoderReaderService.ACTION_NO_USB);
        filter.addAction(BarcoderReaderService.ACTION_USB_DISCONNECTED);
        filter.addAction(BarcoderReaderService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(BarcoderReaderService.ACTION_USB_PERMISSION_NOT_GRANTED);
        getContext().registerReceiver(mUsbReceiver, filter);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!BarcoderReaderService.SERVICE_CONNECTED) {
            Intent startService = new Intent(getContext(), service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            getContext().startService(startService);
        }
        Intent bindingIntent = new Intent(getContext(), service);
        //绑定Service
        getContext().bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


        //BCR usb connect
    private final ServiceConnection usbConnection = new ServiceConnection() {

        //成功绑定服务后调用
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            bcrService = ((BarcoderReaderService.UsbBinder) arg1).getService();
            tbcr.setService(bcrService);

                //绑定后初始化
            int ret = tbcr.BCRInit("", "");
            if(ret == TBarcoderReader.NO_ERROR) {
                //开启扫描
                StartScan();
            }else {
                String errStr = tbcr.BCRGetLastErrorStr();
                mBCRStatus.setText(errStr + " ");
                Log.i("BCR", "init return " + ret + " errstr =" + errStr);
            }

        }

        //解除绑定后调用
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bcrService = null;
            stopScan();
        }
    };



    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BarcoderReaderService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case BarcoderReaderService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case BarcoderReaderService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case BarcoderReaderService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case BarcoderReaderService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String ticketInfo = msg.getData().getString(TICKET_INFO);
                    int type = msg.getData().getInt("info");
                    mScandata_view.setText(ticketInfo + " 类型" +type);
                    switch (type) {
                        case 152:
                            mBCRCodeType.setText("PDF417");
                            break;
                        case 151:
                            mBCRCodeType.setText("DataMatrix");
                            break;
                        case 101:
                            mBCRCodeType.setText("I2of5");
                            break;
                        case 157:
                            mBCRCodeType.setText("QR");
                            break;
                        case 102:
                            mBCRCodeType.setText("EAN");
                            break;
                        case 110:
                            mBCRCodeType.setText("Code39");
                            break;
                        case 107:
                            mBCRCodeType.setText("Code128");
                            break;
                        default:
                            break;

                    }
            }

        }
    };

    //init BCR
    private void StartScan(){
        mBCRStatus.setText("正常");
//        tbcr.BCRStartScan();
        boolean ret = tbcr.BCRSetScanMode(mScanMode);
        Log.i("BCR", "BCRSetScanMode return= " +ret + "mScanMode="+ mScanMode);
        isScan = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isScan) {
                    while (tbcr.BCRScanIsComplete() == true) {
                        byte[] ticketInfo = tbcr.BCRGetTicketInfo();
                        int a = ticketInfo[0] & 0xFF;
                        try {

                                String ticketMsg = new String(ticketInfo, 7, ticketInfo.length-7, "ASCII");
                                Message message = new Message();
                                message.what = 1;
                                Bundle bundle=new Bundle();
                                bundle.putString(TICKET_INFO, ticketMsg);
                                bundle.putInt("info",a);
                                message.setData(bundle);
                                handler.sendMessage(message);

                        } catch (UnsupportedEncodingException e) {
                                throw new InternalError();
                            }

                        }
                    }
                }
            }).start();
    }


    private void stopScan() {
        isScan = false;
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }



}
