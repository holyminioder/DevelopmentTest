package com.example.lederui.developmenttest.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.activity.MainActivity;
import com.example.lederui.developmenttest.data.ScannerInterface;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/20.
 */

/**
 * 读票机Fragment
 */
public class TicketReaderFragment extends Fragment {
    @BindView(R.id.result_view)
    TextView mResultView;
    @BindView(R.id.scanner_status)
    TextView mScannerStatus;
    @BindView(R.id.scanner_codetype)
    TextView mScannerCodetype;
    @BindView(R.id.bt_sign_print)
    Button mBtSignPrint;
    @BindView(R.id.sign_spinner)
    Spinner mSignSpinner;
    Unbinder unbinder;
    private ScannerInterface mScanner = new ScannerInterface();
    private boolean isScan = false;
    byte[] ScanData = new byte[4096];
    private String DATA_TYPE = "datatype";
    private String TICKET_INFO = "ticketInfo";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_reader, container, false);
        unbinder = ButterKnife.bind(this, view);
        scannerInit();
        initView();
        startScan();
        return view;
    }

    private void initView(){
        String[] mItems = getResources().getStringArray(R.array.pattern);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSignSpinner.setAdapter(adapter);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String ticketInfo = msg.getData().getString(TICKET_INFO);
                    int type = msg.getData().getInt(DATA_TYPE);
                    Log.i(MainActivity.TAG, "code type" + type);
                    mScannerCodetype.setText(type+"");
                    mResultView.setText(ticketInfo);
                    switch (type) {
                        case 152:
                            mScannerCodetype.setText("PDF417");
                            break;
                        case 151:
                            mScannerCodetype.setText("DataMatrix");
                            break;
                        case 101:
                            mScannerCodetype.setText("I2of5");
                            break;
                        case 157:
                            mScannerCodetype.setText("QR");
                            break;
                        case 102:
                            mScannerCodetype.setText("EAN");
                            break;
                        case 110:
                            mScannerCodetype.setText("Code39");
                            break;
                        case 107:
                            mScannerCodetype.setText("Code128");
                            break;
                        default:
                            break;
                    }
                    break;
                case 2:
                    String err = msg.getData().getString("ErrStr");
                    mScannerStatus.setText(err);
            }
        }
    };

    private void scannerInit() {
        int flag = mScanner.SInit();
        if (flag != 0) {
            String errStr = mScanner.SGetLastErrorStr(null, 256);
            Log.e(MainActivity.TAG,errStr);

            Message message = new Message();
            message.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("ErrStr",errStr);
            message.setData(bundle);
            handler.sendMessage(message);
        }else {
            isScan = true;
            Message message = new Message();
            message.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("ErrStr","OK");
            message.setData(bundle);
            handler.sendMessage(message);
            Log.e(MainActivity.TAG,"init ok");
        }
    }

    private void startScan(){
        mScanner.SStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isScan){
                    while (isScan){
                        if(mScanner.ScanIsComplete()){
                            //添加beep会get不到数据？？
                            Log.i(MainActivity.TAG, "ScanIsComplete");
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
                    Integer type = new Integer(0);
//                    Integer len = new Integer(0);
//                    mScanner.SGetOriginImageSize(0, 0, len);
//                    Log.e("len_value", len+"");
                    int length = mScanner.SGetTicketInfo(ticketinfo, type);
                    ScanData = ticketinfo;
//                    Log.i(MainActivity.TAG,"BCRGetTicketInfo"+"\nlength="+len);
                    if(type > 0 ){
                        Log.i(MainActivity.TAG,"ticketinfo[0]="+ticketinfo[0]+"\n"+"type="+type);
//                        int type = ticketinfo[0] & 0xff;
                        try {
                            String ticketmsg = new String(ticketinfo, 7, type, "gbk");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.bt_sign_print)
    public void onViewClicked() {
        int value = 1;
        String str = mSignSpinner.getSelectedItem().toString();
        if ("已兑奖".equals(str)){
            value = 1;
        }else if ("已取消".equals(str)){
            value = 3;
        }
        mScanner.SPrintBrandImage(null, value, 340, 200);
        mScanner.SRollBack();
    }

    int num = 0;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        num++;
        if (num == 3){

        }
    }
}
