package com.example.lederui.developmenttest.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.activity.MainActivity;
import com.example.lederui.developmenttest.data.ScannerInterface;
import com.example.lederui.developmenttest.utils.BitmapConvertor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    CheckBox mBtSignPrint;
    @BindView(R.id.sign_spinner)
    Spinner mSignSpinner;
    @BindView(R.id.selfdefine_edit)
    EditText mSelfDefEditText;

    Unbinder unbinder;
    String mSelfdefStr = "自定义标记测试";
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
        mSelfDefEditText.addTextChangedListener(textWatcher);
        mSignSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 2){
                    String editstr = mSelfDefEditText.getText().toString().trim();
                    if(editstr.isEmpty()) {
                        mSelfdefStr = "自定义标记测试";
                        creatBMP(mSelfdefStr);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    TextWatcher textWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart ;
        private int editEnd ;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mSelfdefStr = String.valueOf(s);
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = mSelfDefEditText.getSelectionStart();
            editEnd = mSelfDefEditText.getSelectionEnd();
            if (mSelfdefStr.length() > 15) {
                Toast.makeText(getContext(),
                        "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT)
                        .show();
                s.delete(editStart-1, editEnd);
                int tempSelection = editStart;
                mSelfDefEditText.setText(s);
                mSelfDefEditText.setSelection(tempSelection);
            }

            if(mSelfDefEditText.getText().toString().trim().isEmpty()) {
                mSelfdefStr = "自定义标记测试";
            }
            creatBMP(mSelfdefStr);
        }
    };
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
                            if (mBtSignPrint.isChecked()){
                                int value = 1;
                                String str = mSignSpinner.getSelectedItem().toString();
                                if ("已兑奖".equals(str)){
                                    value = 1;
                                }else if ("已取消".equals(str)){
                                    value = 3;
                                }else if("自定义".equals(str)){
                                    mScanner.SPrintSelfDefBrandImage(null, 320, 300);
                                    mScanner.SRollBack();
                                    break;
                                }
                                mScanner.SPrintBrandImage(null, value, 340, 200);
                                mScanner.SRollBack();
                            }else {
                                mScanner.SRollBack();
                            }
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

                    byte[] info = new byte[1024];
                    boolean flag = mScanner.SGetHWInformation(info, 1024);
                    if (flag) {
                        try {
                            String hwinfo = new String(info, "gbk");
                            String[] str;
                            if (hwinfo != "") {
                                str = hwinfo.split("\n");
                                hwinfo = "";//清空 ，排版
                                for (int i = 0; i < str.length; i++) {
                                    hwinfo += str[i] + "\n" + "\n";
                                }
                            }
                            SharedPreferences shared = getActivity().getSharedPreferences("message", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString("scanInfo", hwinfo);
                            editor.putBoolean("init", isScan);
                            editor.commit();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }

    private void creatBMP(String str){

        //创建空bitmap
        Bitmap bitmap = Bitmap.createBitmap(256, 90, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //ANTI_ALIAS_FLAG 抗锯齿
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setFlags(1);
        mPaint.setTextSize(30);

        int width = 256;
        int height = 90;

        mPaint.setStyle(Paint.Style.FILL);//填充
        mPaint.setFakeBoldText(true);//加粗
        mPaint.setStrokeWidth((float) 10.0);//设置线宽
        canvas.drawColor(Color.WHITE);
        canvas.drawLine(0,0,0,height,mPaint);
        canvas.drawLine(width,0,width,height,mPaint);
        canvas.drawLine(0,0,width,0,mPaint);
        canvas.drawLine(0,height,width,height,mPaint);

        canvas.drawText(str,20,50,mPaint);

        canvas.save();
        canvas.restore();

        BitmapConvertor bitmapConvertor = new BitmapConvertor(getContext());
        bitmapConvertor.convertBitmap(bitmap,"BrandImage");

    }




    private void savePic(Bitmap bitmap,String path) {
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
