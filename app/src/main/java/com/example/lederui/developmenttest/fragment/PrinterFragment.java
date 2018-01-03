package com.example.lederui.developmenttest.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.activity.MainActivity;
import com.example.lederui.developmenttest.data.PrinterInterface;

import org.dtools.ini.BasicIniFile;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniFileWriter;
import org.dtools.ini.IniItem;
import org.dtools.ini.IniSection;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.example.lederui.developmenttest.data.PrinterInterface.GetLastErrStr;
import static com.example.lederui.developmenttest.data.PrinterInterface.PrintInit;

/**
 * Created by holyminier on 2017/4/21.
 */

/** 打印机Fragment */
public class PrinterFragment extends Fragment  implements View.OnClickListener{

    private PrinterInterface mPrintInterface = new PrinterInterface();


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
    @BindView(R.id.btn_halfcut) Button mBtnHalfCut;
    @BindView(R.id.btn_allcut) Button mBtnAllCut;
    @BindView(R.id.btn_blackmark) Button mBtnBlackMark;
    @BindView(R.id.btn_noblackmark) Button mBtnNoBlackMark;
    @BindView(R.id.printSpeedTextView) TextView mPrintSpeedView;
    @BindView(R.id.cutSpeedTextView) TextView mCutSpeedView;
    @BindView(R.id.printer_status) TextView mPrinterStatusView;
    @BindView(R.id.choice_imagefile) Button mBtnChoice_imagefile;



    private View mView;
    Unbinder binder;
    private int mCutMode;
    private static boolean isInit = false;
    private Spinner mSpinner;
    private int mCodetype;
    private String mPDFCode;
    private CountDownTimer mCountDownTimer;
    private ToggleButton mToggleBtn;
    private Handler mHandler = null;


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


        mToggleBtn = (ToggleButton) mView.findViewById(R.id.btn_printContinue);
        mCountDownTimer = new CountDownTimer(25*60*1000,500) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                Log.d("runable", "running");
                if(!mPrintInterface.PrintSample(mCutMode)) {
                    String str = GetLastErrStr();
//                    Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                Log.d("runable", "stop");
                mToggleBtn.setChecked(true);
            }
        };




        final String[] mCodeItems = {"PDF417","QR","EAN","code39","code128","Interleaved 2 of 5"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, mCodeItems);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner = (Spinner)mView.findViewById(R.id.spinner_codeType);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCodetype = position;
                Log.i(MainActivity.TAG, "position ="+position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mBtnBlackMark = (Button) mView.findViewById(R.id.btn_blackmark);
        mBtnHalfCut = (Button) mView.findViewById(R.id.btn_halfcut);

        mBtnBlackMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCutMode = 0;
                mBtnNoBlackMark.setEnabled(true);
                mBtnBlackMark.setEnabled(false);
                mPrintInterface.SetCutMode(mCutMode);
            }
        });

        mBtnHalfCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnAllCut.setEnabled(true);
                mBtnHalfCut.setEnabled(false);
                setCutMarkByINI("1");
                PrintInit();
            }
        });

        mBtnBlackMark.performClick();
        mBtnHalfCut.performClick();

        mBtnChoice_imagefile.setVisibility(View.INVISIBLE);


    }

    public void InitDev() {
        if(PrintInit()) {
            isInit = true;
            Log.i("printerfragment","PrinterFragment printer init ok " );

        }else {
            isInit = false;
            Log.i("printerfragment","PrinterFragment printer init false");
            String str = GetLastErrStr();
            Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
        }


        // 打印機狀態
        mHandler = new Handler();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final int printStatus = mPrintInterface.PrinterStatus();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if(printStatus != 0){
                            mPrinterStatusView.setTextColor(Color.RED);
                        }
                        switch (printStatus){
                            case 0:
                                mPrinterStatusView.setTextColor(Color.BLACK);
                                mPrinterStatusView.setText("正常");
                                break;
                            case 1:
                                mPrinterStatusView.setText("找不到打印机");
                                break;
                            case 2:
                                mPrinterStatusView.setText("数据线故障");
                                break;
                            case 3:
                                mPrinterStatusView.setText("电源线故障");
                                break;
                            case 4:
                                mPrinterStatusView.setText("打印机忙");
                                break;
                            case 5:
                                mPrinterStatusView.setText("超时");
                                break;
                            case 8:
                                mPrinterStatusView.setText("打印机上盖被打开");
                                break;
                            case 9:
                                mPrinterStatusView.setText("纸卷错误");
                                break;
                            case 10:
                                mPrinterStatusView.setText("卡纸");
                                break;
                            case 11:
                                mPrinterStatusView.setText("纸将尽");
                                break;
                            case 500:
                                mPrinterStatusView.setText("其他错误");
                                break;
                            default:break;
                        }
                    }
                });
            }
        }, 2000, 1000*2);
    }


    @OnClick({R.id.btn_allcut,R.id.btn_noblackmark,R.id.btn_printSample,R.id.btn_printString,R.id.btn_printImage
    ,R.id.btn_printSmallBlackBar,R.id.btn_printBigBlackBar,R.id.btn_printContinue,R.id.btn_printCode
    ,R.id.btn_printPageMode,R.id.btn_printSpeedtest, R.id.btn_CutPaperSpeedtest, R.id.choice_imagefile })
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //0   全切  1  半切

            case R.id.btn_allcut:
                mBtnAllCut.setEnabled(false);
                mBtnHalfCut.setEnabled(true);
                setCutMarkByINI("0");
                mPrintInterface.PrintInit();
                break;
            case R.id.btn_noblackmark:
                mCutMode = 1;
                mBtnNoBlackMark.setEnabled(false);
                mBtnBlackMark.setEnabled(true);
                mPrintInterface.SetCutMode(mCutMode);
                break;
            case R.id.btn_printSample:
                if(!mPrintInterface.PrintSample(mCutMode)) {
                    String str = GetLastErrStr();
                    Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
                }
//                mPDFCode = mPrintInterface.GetPDFCode();
//                Log.i("printer","btn_printSample mpdfcode = " + mPDFCode);

                break;
            case R.id.btn_printString:
                if(!mPrintInterface.PrintAllString(mCutMode)){
                    String str = GetLastErrStr();
                    Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_printImage:
                if(!mPrintInterface.PrintImage(mCutMode)){
                    String str = GetLastErrStr();
                    Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_printSmallBlackBar:
                if(!mPrintInterface.PrintBlackBarcode(mCutMode,0)) {
                    String str = GetLastErrStr();
                    Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_printBigBlackBar:
                if(!mPrintInterface.PrintBlackBarcode(mCutMode,1)) {
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
                mPrintInterface.PrintBarCode(mCutMode,mCodetype);
                Log.i(MainActivity.TAG,"in PrintBarCode "+mCodetype+" mCutMode"+mCutMode);

                break;
            case R.id.btn_printPageMode:
                if(!mPrintInterface.PrintPaperMode(mCutMode)) {
                    String str = GetLastErrStr();
                    Toast.makeText(getContext(),"errStr:"+str,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_printSpeedtest:
                TestPrinterSpeed();
                break;
            case R.id.btn_CutPaperSpeedtest:
                TestCutPaperSpeed();
                break;
            case  R.id.choice_imagefile:
                showFileChooser();
                break;

            default:
                break;
        }

    }

    //打印速度测试 打印一分钟
    private void TestPrinterSpeed() {
        long timer1 = System.currentTimeMillis();
        long timer2;
        int count = 0;
        boolean stop = false;
        mPrintSpeedView.setText("正在打印...");

        while (true){
            timer2 = System.currentTimeMillis();
            long diff = timer2 - timer1;
            Log.i("printer", "difftime=" + diff);
            if(diff > 60*1000)
                break;

            if(mPrintInterface.PrintSample(mCutMode) == false)
                return;

            count++;
        }

        Log.i("printer", "speed = " + count+"张");

        mPrintSpeedView.setText("打印速度="+count+"张/分钟\n");



    }


    private void TestCutPaperSpeed() {

        long timer2;
        int count = 0;
//        boolean initflag = mPrintInterface.PrintInit();
        boolean flag = mPrintInterface.SetCutMode(1);

        long timer1 = System.currentTimeMillis();
        Log.i("test", "time1=" + timer1);

        while (true){
            timer2 = System.currentTimeMillis();
            long diff = timer2 - timer1;

            if(diff > 60*1000)
                break;


//            mPrintInterface.PrintString("cut test");
            mPrintInterface.CutPaper();
            count++;
        }

        mCutSpeedView.setText("切纸速度="+count+"切/分钟\n" );
    }


    private void delay(int ms){
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

  //set printer halfcut or  allcut  by .INI file
    private void setCutMarkByINI(String mode) {
        IniFile iniFile = new BasicIniFile();
        File file = new File("/sdcard/config/HWILatechPrinter.ini");
        IniFileReader iniFileReader = new IniFileReader(iniFile, file);
        IniFileWriter iniFileWriter = new IniFileWriter(iniFile, file);
        try {
            iniFileReader.read();
            IniSection iniSection = iniFile.getSection("PrinterConfig");
            IniItem iniItem = iniSection.getItem("CutMode");
            String value = 	iniItem.getValue();

            iniItem.setValue(mode);
            iniSection.addItem(iniItem);
            iniFile.addSection(iniSection);
            iniFileWriter.write();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    getPath(getContext(), uri);
//                    Log.i("image path =" , path);
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static final int FILE_SELECT_CODE = 0;
    private void showFileChooser() {

        File file = new File("/mnt/sdcard/");
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "image/*");

        try {
            startActivityForResult( Intent.createChooser(intent, "请选择图像"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }


    private  String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            // MediaStore.Images.Media.DATA = "_data" , Path to the file on disk.
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                Log.i("image", "column_index=" + column_index);
                if (cursor !=null && cursor.moveToFirst()) {
                    Log.i(" path ==",cursor.getString(column_index)+"");
                    return cursor.getString(column_index);
                }else  {
                    Log.i("image","cursor == null");
                }
            } catch (Exception e) {
                // Eat it  Or Log it.
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


}
