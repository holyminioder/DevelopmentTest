package com.example.lederui.developmenttest.fragment;


import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;




import java.util.Set;

import butterknife.BindView;


import com.example.lederui.developmenttest.R;
import com.ztec.bcr.BarcoderReaderService;
import com.ztec.bcr.TBarcoderReader;

/**
 * Created by holyminier on 2017/4/21.
 */

/** 条码识读Fragment */
public class BarcodeReaderFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.leix)
    TextView mleix;

    TextView display;

    @BindView(R.id.button_init)
    TextView btn_1;

    @BindView(R.id.button_sp)
    TextView btn_6;
    @BindView(R.id.button_lianx)
    TextView btn_7;
    @BindView(R.id.button_duanx)
    TextView btn_8;
    @BindView(R.id.connect_text)
    TextView tv;




//    static {
//        System.loadLibrary("bcr");
//    }

    /*
    * Notifications from BarcoderReaderService will be received here.
    */
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
    private BarcoderReaderService bcrService;

    private TBarcoderReader tbcr;

    private Spinner spn_list;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     */
   // private GoogleApiClient client;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            bcrService = ((BarcoderReaderService.UsbBinder) arg1).getService();
            tbcr.setService(bcrService);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bcrService = null;
        }
    };
    private int mode;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barcode_reader, container, false);



        mleix = (TextView)view.findViewById(R.id.leix);
        btn_1 = (Button) view.findViewById(R.id.button_init);
        //btn_2 = (Button) findViewById(R.id.button_close);
        // btn_5 = (Button) findViewById(R.id.button_exec);
        btn_6 = (Button) view.findViewById(R.id.button_sp);
        tv = (TextView) view.findViewById(R.id.connect_text);
        btn_7 = (Button) view.findViewById(R.id.button_lianx);
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonzd();//自动
            }
        });

        btn_8 = (Button) view.findViewById(R.id.button_duanx);
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button01();
            }
        });
        //spn_list = (Spinner) findViewById(R.id.spinner);
        tbcr = new TBarcoderReader();

        display = (TextView) view.findViewById(R.id.textView_func);
        // dataView =(TextView) findViewById(R.id.textView1);
        // Example of a call to a native method
        //TextView tv = (TextView) view.findViewById(R.id.textViewTitle);
        //tv.setText("测试体彩通信接口");

        View.OnClickListener cl_handler = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_init:
                        int ret = tbcr.BCRInit("", "");
                        if (ret == TBarcoderReader.NO_ERROR) {
                            tv.setText("已连接");
                            display.setText("初始化" +" "+ "OK");
                        } else {
                            tv.setText("未连接");
                        }
                        break;

                    case R.id.button_close:
                        tbcr.BCRClose();
                        // tv = (TextView) findViewById(R.id.connect_text);
                        //tv.setText("未连接");
                        break;


                    case R.id.button_sp:

                        display.setText(""+buttonsp());


                }

            }
        };
        btn_1.setOnClickListener(cl_handler);
        //btn_2.setOnClickListener(cl_handler);
        //btn_5.setOnClickListener(cl_handler);
        btn_6.setOnClickListener(cl_handler);





        return  view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        //tbcr.BCRInit("", "");
//
////        if (ret == TBarcoderReader.NO_ERROR) {
////            tv.setText("已连接");
////            display.setText("初始化" +" "+ "OK");
////        } else {
////            tv.setText("未连接");
////        }
//
//
//    }

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
        getContext().bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
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

    public void OnDestroy(){
        tbcr.release();
    }


    public  String buttonsp(){

        /**汝嫣**/
        byte[]  TicketInfo= tbcr.BCRGetTicketInfo();
        String sp = null;
        try {
            byte D = TicketInfo[0];
            int i = D;
            i = D & 0xff;
            Log.e("eeee", TicketInfo.toString());
            sp = new String(TicketInfo);

            mleix.setText(""+i);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return sp;
    }

    public  void  buttonzd(){


        tbcr.BCRSetScanMode(2);



    }

    public void button01(){
        tbcr.BCRSetScanMode(1);
    }

    public void execBCRFunction() {
        boolean ret;
        String s = spn_list.getSelectedItem().toString();
        // TextView tv = (TextView) findViewById(R.id.textView_func);


        switch (s) {
            case "BCRGetLastErrorCode":
                int errcode = tbcr.BCRGetLastErrorCode();
                // tv.setText(s + " " + Integer.toString(errcode));
                break;

            case "BCRQueryCapability":
                int cap = tbcr.BCRQueryCapability();
                //tv.setText(s + " " + Integer.toString(cap, 10));
                break;

            case "BCRGetLastErrorStr":
                String errStr = tbcr.BCRGetLastErrorStr();
                //tv.setText(s + " " + errStr);
                break;

            case "BCRAimOn":
                ret = tbcr.BCRAimOn();
                //tv.setText(s + " " + Boolean.toString(ret));
                break;

            case "BCRAimOff":
                ret = tbcr.BCRAimOff();
                //tv.setText(s + " " + Boolean.toString(ret));
                break;

            case "BCREnable":
                ret = tbcr.BCREnable();
                //tv.setText(s + " " + Boolean.toString(ret));
                break;

            case "BCRDisable":
                ret = tbcr.BCRDisable();
                //tv.setText(s + " " + Boolean.toString(ret));
                break;


            default:
                break;

        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     */
//    public Action getIndexApiAction() {
//        Thing object = new Thing.Builder()
//                .setName("Main Page") // TODO: Define a title for the content shown.
//                // TODO: Make sure this auto-generated URL is correct.
//                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
//                .build();
//        return new Action.Builder(Action.TYPE_VIEW)
//                .setObject(object)
//                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
//                .build();
//    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        //client.connect();
        //AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // client.disconnect();
    }

    @Override
    public void onClick(View view) {
        //tbcr.BCRInit("", "");
    }

//
//    public static void wshow(Context context){
//        TBarcoderReader ww = null;
//        Activity act = (Activity) context;
//        ww.BCRInit("", "");
//
//    }

    public static void wshow(AdapterView.OnItemClickListener onItemClickListener) {
        TBarcoderReader ww = null;
//       Activity act = (Activity) ;
        ww.BCRInit("", "");
    }
}
