package com.example.lederui.developmenttest.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lederui.developmenttest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/21.
 */

/** 打印机Fragment */
public class PrinterFragment extends Fragment  implements View.OnClickListener{

    static{
        System.loadLibrary("usb1.0");
        System.loadLibrary("ConfigFileINI");
        System.loadLibrary("flog");
        System.loadLibrary("bitmap");
        System.loadLibrary("scanprn");
        System.loadLibrary("ScanPrnWrap");
        System.loadLibrary("native-printer");
    }

    private native boolean PrintInit();

    private native  String GetLastErrStr();

    private native boolean SetCutMode(int mode);

    //change ini file , set allcut or halfcut
    private native void SetAllcutOrHalfcut(int mode);

    private native boolean PrintSample(int cutmode);

    private native boolean PrintAllString(int cutmode);

    private native boolean PrintImage(int cutmode);

    private native boolean PrintBlackBarcode(int cutmode,int size);

    private native String PrintCodePaper(int cutmode,int code);

    private native String PrintContinue(int cutmode , int time);

    private native boolean PrintPaperMode(int cutmode);

    private native String PrintSpeedTest(int cutmode);

    private native String PrintCutPaperSpeed(int cutmode);

    private native String PrintBarCode(int cutmode ,int codeType);

    private native  boolean PrintString(String str);

    @BindView(R.id.btn_printSample) Button mBtPrintSample;
    @BindView(R.id.btn_printString) Button mBtPrintStringBt;
    @BindView(R.id.btn_printImage) Button mBtPrintImage;
    @BindView(R.id.btn_printSmallBlackBar) Button mBtPrintSamllBlackBar;
    @BindView(R.id.btn_printBigBlackBar) Button mBtPrintBigBlackBar;
    @BindView(R.id.btn_printContinue) Button mBtPrintContinue;
    @BindView(R.id.btn_printCode) Button mBtPrintCodeBar;
    @BindView(R.id.btn_printPageMode) Button mBtPrintPageMode;
    @BindView(R.id.btn_printSpeedtest) Button mBtPrintSpeedTest;
    @BindView(R.id.btn_CutPaperSpeedtest) Button mBtCutPaperSpeedTest;
    @BindView(R.id.riobtn_halfcut) RadioButton mRioHalfCut;
    @BindView(R.id.riobtn_allcut) RadioButton mRioAllCut;
    @BindView(R.id.riogroup_cut) RadioGroup mRioGroupCut;
    @BindView(R.id.riogroup_blackmark) RadioGroup mRioGroupBlackMark;
    @BindView(R.id.printSpeedTextView) TextView mPrintSpeedView;
    @BindView(R.id.cutSpeedTextView) TextView mCutSpeedView;

    private View mView;
    Unbinder binder;
    private int mCutMode;
    private static boolean isInit = false;
    private Spinner mSpinner;
    private int mCodetype;
    private CountDownTimer mCountDownTimer;
    private ToggleButton mToggleBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_printer, container, false);
        binder = ButterKnife.bind(this, view);
        mView = view;
        InitView();
        InitDev();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binder.unbind();
    }

    private void InitView(){
        mCutMode = 0;
        mCodetype = 0;


        mRioGroupCut.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.riobtn_halfcut){
                    mCutMode = 0;
                    SetCutMode(mCutMode);
                }else {
                    mCutMode = 1;
                    SetCutMode(mCutMode);
                }
            }
        });

        mToggleBtn = (ToggleButton) mView.findViewById(R.id.btn_printContinue);
        mCountDownTimer = new CountDownTimer(3*1000,500) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                Log.d("runable", "running");
                PrintAllString(mCutMode);
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                Log.d("runable", "stop");
                mToggleBtn.setChecked(true);
            }
        };

        final String[] mCodeItems = {"PDF417","QR","EAN","code39","code128"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, mCodeItems);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner = (Spinner)mView.findViewById(R.id.spinner_codeType);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCodetype = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void InitDev() {
        if(PrintInit()) {
            isInit = true;
        }else {
            isInit = false;
        }
    }


    @OnClick({R.id.riobtn_halfcut,R.id.riobtn_allcut,R.id.btn_printSample,R.id.btn_printString,R.id.btn_printImage
    ,R.id.btn_printSmallBlackBar,R.id.btn_printBigBlackBar,R.id.btn_printContinue,R.id.btn_printCode
    ,R.id.btn_printPageMode,R.id.btn_printSpeedtest, R.id.btn_CutPaperSpeedtest})
    @Override
    public void onClick(View v) {
        if(!isInit) {
            Toast.makeText(getContext(),"printer init failed: "+GetLastErrStr(),Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        switch (v.getId()) {
            case R.id.riobtn_halfcut:
                mCutMode = 0;
                break;
            case R.id.riobtn_allcut:
                mCutMode = 1;
            case R.id.btn_printSample:
                if(!PrintSample(mCutMode)) {
                    String str = GetLastErrStr();
                    Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_printString:
                if(!PrintAllString(mCutMode)){
                    String str = GetLastErrStr();
                    Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_printImage:
                if(!PrintImage(mCutMode)){
                    String str = GetLastErrStr();
                    Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_printSmallBlackBar:
                if(!PrintBlackBarcode(mCutMode,0)) {
                    String str = GetLastErrStr();
                    Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_printBigBlackBar:
                if(!PrintBlackBarcode(mCutMode,1)) {
                    String str = GetLastErrStr();
                    Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_printContinue:
                if(!mToggleBtn.isChecked()){ // 第一次点击响应
                    mCountDownTimer.start();

                } else {
                    mCountDownTimer.cancel();
                }

                break;
            case R.id.btn_printCode:
                PrintBarCode(mCutMode,mCodetype);
                break;
            case R.id.btn_printPageMode:
                PrintPaperMode(mCutMode);
                break;
            case R.id.btn_printSpeedtest:
                TestPrinterSpeed();
//                PrintSpeedTest(mCutMode);
                break;
            case R.id.btn_CutPaperSpeedtest:
                TestCutPaperSpeed();
//                PrintCutPaperSpeed(mCutMode);
                break;
            default:
                break;
        }

    }

    private void TestPrinterSpeed() {
        long timer1 = System.currentTimeMillis();
        Log.i("test", "time1=" + timer1);
        int count = 0;
        boolean stop = false;
        while (count <10){
            PrintSample(mCutMode);
            count++;
        }
        long timer2 = System.currentTimeMillis();
        Log.i("test", "time2=" + timer2);
        long diff = timer2 - timer1;
        Log.i("test", "difftime=" + diff);
        int speed =  102*10*1000/(int)diff; // 102*15*1000/diff;
        Log.i("test", "speed = " + speed);

        mPrintSpeedView.setText("打印速度="+speed+"mm/s\n" + "计算公式=102*张数*1000/时间间隔(ms)");
    }

    private void TestCutPaperSpeed() {
        long timer1 = System.currentTimeMillis();
        Log.i("test", "time1=" + timer1);
        int count = 0;
        boolean stop = false;
        SetCutMode(1);
        while (count < 4){
            PrintString("s");
            count++;
//            delay(500);

        }
        long timer2 = System.currentTimeMillis();
        Log.i("test", "time2=" + timer2);
        long diff = timer2 - timer1;
        Log.i("test", "difftime=" + diff);
        int speed =  20*60*1000/(int)diff; // 102*15*1000/diff;
        Log.i("test", "speed = " + speed);

        mPrintSpeedView.setText("切纸速度="+speed+"切/分钟\n" );
    }


    private void delay(int ms){
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
