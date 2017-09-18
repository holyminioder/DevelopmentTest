package com.example.lederui.developmenttest.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.data.MainBoardMessage;
import com.example.lederui.developmenttest.data.PrinterInterface;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ztec.bcr.BarcoderReaderService;
import com.ztec.bcr.TBarcoderReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 硬件信息Fragment
 */
public class HardwareFragment extends Fragment {

    static {
        System.loadLibrary("bcr");
    }

    @BindView(R.id.main_board_message)
    TextView mMainMessage;
    @BindView(R.id.printer_hw_info)
    TextView mPrinterHwInfoView;

    Unbinder unbinder;
    @BindView(R.id.main_board_status)
    TextView mMainBoardStatus;
    @BindView(R.id.bcr_hwinfo_view)
    TextView mBCRHwInfoView;
    @BindView(R.id.bcr_status_view)
    TextView mBCRStatus;
    @BindView(R.id.print_status_view)
    TextView mPrinterStatusView;
    @BindView(R.id.net_card_speed)
    TextView netCardSpeed;

    private PrinterInterface mPrinterLib;
    private BarcoderReaderService bcrService;
    private TBarcoderReader tbcr;
    private GoogleApiClient client;
    private static int first = 0;
    private Timer timer;
    private TimerTask task;

    BufferedReader reader = null;
    BufferedReader statuReader = null;
    String ethSpeed = "最大传输速度";
    String netStatus = "运行状态";
    StringBuffer buffer;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String speed = msg.getData().getString("speed");
                String state = msg.getData().getString("state");
                buffer = new StringBuffer();
                buffer.append("以太网状态：").append(state + "\n" + "\n");
                if ("down".equals(state)){
                    buffer.append("网卡速度：100M");
                }else {
                    buffer.append("网卡速度：").append(speed).append("M");
                }
                netCardSpeed.setText(buffer.toString());
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hardware, container, false);
        unbinder = ButterKnife.bind(this, view);
        MainBoardMessage.getContext(getContext());

        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
        tbcr = new TBarcoderReader();
        mPrinterLib = new PrinterInterface();

        getMainBoardInfo();
        startTimer();
        getBCRHWInfo();

        //调用接口返回 errDevice data line error
        getPrinterHWInfo();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
        startService(BarcoderReaderService.class, usbConnection, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unbindService(usbConnection);
    }


    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    private void getMainBoardInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("CPU型号：");
        sb.append(MainBoardMessage.getCpuInfo() + "\n" + "\n");
        sb.append("CPU主频：");
        sb.append(MainBoardMessage.getCurCpuFreq() + " GHZ" + "\n" + "\n");
        sb.append("CPU核心数：");
        sb.append(MainBoardMessage.getNumCores() + "\n" + "\n");
        sb.append("内存容量：");
        sb.append(MainBoardMessage.getTotalRam() + "\n" + "\n");
        sb.append("存储容量：");
        sb.append(MainBoardMessage.getStorageSize() + "\n" + "\n");
        sb.append("USB接口数量：");
        sb.append(MainBoardMessage.getUsbInterface(getContext()) + "\n" + "\n");
        sb.append("屏幕分辨率：");
        sb.append(MainBoardMessage.getMetris(getActivity()) + "\n" + "\n");
        sb.append("网卡数量：");
        sb.append(MainBoardMessage.getNetworkCardCount() + "");

        mMainMessage.setText(sb.toString());
        if (TextUtils.isEmpty(sb)) {
            mMainBoardStatus.setText("异常");
            mMainBoardStatus.setTextColor(getResources().getColor(R.color.read_color));
        }


    }

    private void getBCRHWInfo() {
        //开启条码枪服务 获取硬件信息
        startService(BarcoderReaderService.class, usbConnection, null);
    }

    //获取打印机硬件信息
    private void getPrinterHWInfo() {
        if (mPrinterLib.PrintInit()) {
            mPrinterStatusView.setText("正常");
            String[] str;
            String hwinfo = mPrinterLib.GetPrintHwInfo();
            if (hwinfo != "") {
                str = hwinfo.split("\n");
                hwinfo = "";//清空 ，排版
                for (int i = 0; i < str.length; i++) {
                    hwinfo += str[i] + "\n" + "\n";
                }

            }

            mPrinterHwInfoView.setText(hwinfo + "");
        } else {
            mPrinterStatusView.setText("异常");
        }
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
            if (ret == TBarcoderReader.NO_ERROR) {
                String hwinfo = "";
                String[] str;
                hwinfo = tbcr.BCRGetHWInformation();
                if (hwinfo != "") {
                    str = hwinfo.split("\n");
                    hwinfo = "";//清空 ，排版
                    for (int i = 0; i < str.length; i++) {
                        hwinfo += str[i] + "\n" + "\n";
                    }

                }
                mBCRHwInfoView.setText(hwinfo);
                mBCRStatus.setText("正常");
            } else {
                String errStr = tbcr.BCRGetLastErrorStr();
                mBCRStatus.setText(errStr + " ");
            }

        }

        //解除绑定后调用
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bcrService = null;

        }
    };


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

    private void startTimer(){
        if (timer == null){
            timer = new Timer();
        }
        if (task == null){
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        reader = new BufferedReader(new FileReader("sys/class/net/eth0/speed"));
                        ethSpeed = reader.readLine();
                        statuReader = new BufferedReader(new FileReader("sys/class/net/eth0/operstate"));
                        netStatus = statuReader.readLine();
                        Log.e("EthSpeed and NetState", ethSpeed + " , " + netStatus);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    message.what = 1;
                    bundle.putString("speed", ethSpeed);
                    bundle.putString("state", netStatus);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            };
        }
        if (timer != null && task != null) {
            timer.schedule(task, 100, 1000);
        }
    }

    private void stopTimer(){
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        if (task != null){
            task.cancel();
            task = null;
        }
        first = 3;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        first++;
        if (hidden){
            if (first == 3) {
                stopTimer();
            }
        }
        else {
            if (first == 4){
                startTimer();
                first = 2;
            }
        }
    }
}
