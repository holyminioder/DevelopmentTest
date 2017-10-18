package com.example.lederui.developmenttest.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.activity.MainActivity;
import com.example.lederui.developmenttest.data.BCRInterface;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.UnsupportedEncodingException;

/**
 * Created by holyminier on 2017/4/21.
 */

/** 条码识读Fragment */
public class BarcodeReaderFragment extends Fragment {

    static {
        System.loadLibrary("HWICgltBCR");
    }

    private View mView;
    private GoogleApiClient client;
    private TextView mBCRStatus;
    private TextView mBCRCodeType;
    private TextView mScandata_view;
    private Spinner mSprinner;
    private boolean isScan = true;
    private int mScanMode = 2; //2  自动模式 1 手动模式
    private String TICKET_INFO = "ticketInfo";
    private BCRInterface mBCRInerface = new BCRInterface();
    byte[] ScanData = new byte[4096];
    private int ticketLength = 0;
    private String TICKETINFO= "";
    private String DATA_TYPE = "datatype";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_barcode_reader, container, false);
        mView = view;
        initBCR();
        startScan();

        InitView();

        return view;
    }


    private void InitView() {

        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
        mBCRStatus = (TextView) mView.findViewById(R.id.bcr_status);
        mBCRCodeType = (TextView) mView.findViewById(R.id.bcr_codetype);
        mScandata_view = (TextView) mView.findViewById(R.id.bcrscanData_view);

        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.bcrScanModeArr, R.layout.custom_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSprinner = (Spinner) mView.findViewById(R.id.scanmode_spinner);
        mSprinner.setAdapter(spinnerAdapter);

        mSprinner.setSelection(0,true);
        mSprinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    mBCRInerface.BCRStopScan();
                    mScanMode = 2;
                    boolean ret = mBCRInerface.BCRSetScanMode(mScanMode);
                    Log.i("BCR", "BCRSetScanMode return= " +ret + "mScanMode="+ mScanMode);
                    mBCRInerface.BCRStartScan();
                }else if(position == 1){
                    mBCRInerface.BCRStopScan();
                    mScanMode = 1;
                    boolean ret = mBCRInerface.BCRSetScanMode(mScanMode);
                    Log.i("BCR", "BCRSetScanMode return= " +ret + "mScanMode="+ mScanMode);
                    mBCRInerface.BCRStartScan();
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
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String ticketInfo = msg.getData().getString(TICKET_INFO);
                    int type = msg.getData().getInt("info");
                    mScandata_view.setText(ticketInfo);
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
                    break;
                case 2:
                    String err = msg.getData().getString("ErrStr");
                    mBCRStatus.setText(err);

            }

        }
    };

    private void initBCR(){
        int flag = mBCRInerface.BCRInit();
        if(flag != 0){
            String errStr = mBCRInerface.BCRGetLastErrorStr();
            Log.e(MainActivity.TAG,errStr);

            Message message = new Message();
            message.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("ErrStr",errStr);
            message.setData(bundle);
            handler.sendMessage(message);
        }else{
            isScan = true;
            Message message = new Message();
            message.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("ErrStr","OK");
            message.setData(bundle);
            handler.sendMessage(message);
            Log.e(MainActivity.TAG,"init ok");
            mBCRInerface.BCRSetScanMode(2);//自动模式
        }
    }

    private void startScan(){

        mBCRInerface.BCRStartScan();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isScan){
                    while (isScan){
                        if(mBCRInerface.BCRScanIsComplete()){
                            //添加beep会get不到数据？？
                            mBCRInerface.BCRBeep(0);
                            Log.i(MainActivity.TAG, "BCRScanIsComplete");
                            break;
                        }
                        //500ms轮询一次
                        try {
                            Thread.sleep(500);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    byte[] ticketinfo = new byte[4096];
                    Integer len = new Integer(0);
                    mBCRInerface.BCRGetDataLength(len);
                    int length = mBCRInerface.BCRGetTicketInfo(ticketinfo, 4096);
                    ScanData = ticketinfo;
                    ticketLength = len;
                    Log.i(MainActivity.TAG,"BCRGetTicketInfo"+"\nlength="+len);
                    if(len > 0){
                        int type = ticketinfo[0] & 0xff;
                        try {
                            String ticketmsg = new String(ticketinfo, 7, len, "gbk");
                            String tmpstr= new String(ScanData, 7, length, "gbk");
                            Log.i(MainActivity.TAG, "ticketinfo =="+ticketmsg);
                            Message message = new Message();
                            message.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString(TICKET_INFO,ticketmsg);
                            bundle.putInt(DATA_TYPE,type);
                            message.setData(bundle);
                            handler.sendMessage(message);

                        }catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }
                    }

                }
            }
        }).start();

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
