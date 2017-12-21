package com.example.lederui.developmenttest.activity;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.adapter.ListViewAdapter;
import com.example.lederui.developmenttest.data.MainBoardMessage;
import com.example.lederui.developmenttest.data.PrinterInterface;
import com.example.lederui.developmenttest.data.StringManager;
import com.example.lederui.developmenttest.fragment.BakingMachineFragment;
import com.example.lederui.developmenttest.fragment.BarcodeReaderFragment;
import com.example.lederui.developmenttest.fragment.BatteryFragment;
import com.example.lederui.developmenttest.fragment.BrightnessFragment;
import com.example.lederui.developmenttest.fragment.EditionFragment;
import com.example.lederui.developmenttest.fragment.HardwareFragment;
import com.example.lederui.developmenttest.fragment.LocationFragment;
import com.example.lederui.developmenttest.fragment.MainScreenTestFragment;
import com.example.lederui.developmenttest.fragment.NetworkFragment;
import com.example.lederui.developmenttest.fragment.PrinterFragment;
import com.example.lederui.developmenttest.fragment.ResolvingPowerFragment;
import com.example.lederui.developmenttest.fragment.ScanningGunFragment;
import com.example.lederui.developmenttest.fragment.SecoScreenSetFragment;
import com.example.lederui.developmenttest.fragment.SignalTestFragment;
import com.example.lederui.developmenttest.fragment.SoundFragment;
import com.example.lederui.developmenttest.fragment.SoundTestFragment;
import com.example.lederui.developmenttest.fragment.SystemFragment;
import com.example.lederui.developmenttest.fragment.TicketReaderFragment;
import com.example.lederui.developmenttest.fragment.TimeFragment;
import com.example.lederui.developmenttest.fragment.TouchScreenTestFragment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.printer)
    TextView mPrinter;
    @BindView(R.id.scanner_gun)
    TextView mScannerGun;
    @BindView(R.id.toast_chance)
    TextView mToastChance;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.container)
    FrameLayout mContainer;
    @BindView(R.id.exit)
    TextView mExit;
    @BindView(R.id.restart)
    TextView mRestart;
    @BindView(R.id.shutdown)
    TextView mShutdown;
    @BindView(R.id.diagnose_procedure)
    TextView mDiagnoseProcedure;
    @BindView(R.id.ticket_reader)
    TextView mTicketReader;
    @BindView(R.id.setting)
    TextView mSetting;
//    @BindView(R.id.keyboard)
//    TextView mKeyboard;
    @BindView(R.id.printer_state)
    TextView mPrinterState;
    @BindView(R.id.cpu_occupancy)
    TextView mCpuOccupancy;
    @BindView(R.id.cpu_temperature)
    TextView mCpuTemperature;
    @BindView(R.id.voltage)
    TextView mVoltage;


    private FragmentTransaction mFt;
    private ListViewAdapter mAdapter;


    private Handler mHandler2 = null;
    private Handler mHandler = null;
    private Typeface mTypeFace;


    private TicketReaderFragment mTicketFragment;
    private BakingMachineFragment mMachineFragment;
    private BarcodeReaderFragment mBarcodeFragment;
    private BrightnessFragment mBrightnessFragment;
    private EditionFragment mEditionFragment;
    private HardwareFragment mHardwareFragment;
    private LocationFragment mLocationFragment;
    private MainScreenTestFragment mMainScreenFragment;
    private NetworkFragment mNetworkFragment;
    private PrinterFragment mPrinterFragment;
    private PrinterInterface mPrinterLib;
    private ResolvingPowerFragment mPowerFragment;
    private ScanningGunFragment mScanningGunFragment;
    private SecoScreenSetFragment mSecoScreenFragment;
    private SignalTestFragment mSignalFragment;
    private SoundTestFragment mSoundTestFragment;
    private SystemFragment mSystemFragment;
    private TimeFragment mTimeFragment;
    private TouchScreenTestFragment mTouchScreenFragment;
    private BatteryFragment mBatteryFragment;
    private SoundFragment mSoundFragment;
    public static String TAG = "DEVELOPMENTLOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPrinterLib = new PrinterInterface();
        initDev();
        initDate();


    }
    //   界面初始化设置
    private void initDate() {
        //CPU温度
        mHandler = new Handler();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCpuTemperature.setText("CPU温度：" + MainBoardMessage.getCpuTemp() + " ℃");
                    }
                });
            }
        }, 2000, 2000);

        mTypeFace = Typeface.createFromAsset(getAssets(), "fonts/simhei.ttf");
        mDiagnoseProcedure.setTypeface(mTypeFace);
        mPrinter.setTypeface(mTypeFace);
        mScannerGun.setTypeface(mTypeFace);
        mTicketReader.setTypeface(mTypeFace);
//        mKeyboard.setTypeface(mTypeFace);
        mToastChance.setTypeface(mTypeFace);
        mSetting.setTypeface(mTypeFace);
        mExit.setTypeface(mTypeFace);
        mRestart.setTypeface(mTypeFace);
        mShutdown.setTypeface(mTypeFace);
        mPrinterState.setTypeface(mTypeFace);
        mCpuOccupancy.setTypeface(mTypeFace);
        mCpuTemperature.setTypeface(mTypeFace);
        mVoltage.setTypeface(mTypeFace);

        mCpuOccupancy.setVisibility(View.INVISIBLE);
//        mPrinterState.setVisibility(View.INVISIBLE);

        //默认选中诊断程序
        initBackground(mDiagnoseProcedure);
        mListView.setVisibility(View.VISIBLE);
        setListViewAdapter(StringManager.diagnosis);
        onDiagnosisItemClick();
    }

    private void initDev() {
        //打开打印机usb权限
        File file = new File("/system/xbin/xbsu");
        File fileBCR = new File("/dev/ttyACM1");
        if(fileBCR.exists() && file.exists()){
            //打印机设备权限
//            try {
//                Process su;
//                su = Runtime.getRuntime().exec("/system/xbin/su");
//                String cmd = "chmod  777 /dev/bus/usb/* \n"
//                        + "exit\n";
//                String cmd2 = "chmod  777 /dev/bus/usb/*/* \n"
//                        + "exit\n";
//                su.getOutputStream().write(cmd.getBytes());
//                su.getOutputStream().write(cmd2.getBytes());
//                if (su.waitFor() != 0) {
//                    throw new SecurityException();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new SecurityException();
//            }

            try {
                Process su;
                su = Runtime.getRuntime().exec("/system/xbin/xbsu");
                String cmd ="chmod  777 /dev/ttyACM1\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if (su.waitFor() != 0) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }
    }

    //主分类的点击事件
    @OnClick({R.id.diagnose_procedure, R.id.printer, R.id.ticket_reader, R.id.scanner_gun, R.id.toast_chance, R.id.setting, R.id.exit, R.id.restart, R.id.shutdown})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.diagnose_procedure://诊断程序
                mFt = getSupportFragmentManager().beginTransaction();
                hideFragment(mFt);
                mFt.commit();
                initBackground(mDiagnoseProcedure);
                mListView.setVisibility(View.VISIBLE);
                setListViewAdapter(StringManager.diagnosis);
                onDiagnosisItemClick();
                break;
            case R.id.printer://打印机
                initBackground(mPrinter);
                mListView.setVisibility(View.INVISIBLE);
                initPrinterFragment();
                break;
            case R.id.scanner_gun://扫描枪
                initBackground(mScannerGun);
                mListView.setVisibility(View.INVISIBLE);
                initScanningGunFragment();
                break;
            case R.id.ticket_reader://读票机
                initBackground(mTicketReader);
                mListView.setVisibility(View.INVISIBLE);
                initTicketReaderFragment();
                break;
//            case R.id.keyboard://键盘
//                initBackground(mKeyboard);
//                mListView.setVisibility(View.INVISIBLE);
//                initKeyBoardFragment();
//                break;
            case R.id.toast_chance://烤机程序
                initBackground(mToastChance);
                mListView.setVisibility(View.INVISIBLE);
                initBakingMachineFragment();
                break;
            case R.id.setting://设置
                mFt = getSupportFragmentManager().beginTransaction();
                hideFragment(mFt);
                mFt.commit();
                initBackground(mSetting);
                mListView.setVisibility(View.VISIBLE);
                setListViewAdapter(StringManager.setting);
                onSettingItemClick();
                break;
            case R.id.exit://退出
                new AlertDialog.Builder(MainActivity.this).setTitle("温馨提示：")
                        .setMessage("确定要关闭应用程序吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        }).setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.restart://重启
                new AlertDialog.Builder(MainActivity.this).setTitle("温馨提示：")
                        .setMessage("确定要重启系统吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Runtime.getRuntime().exec("xbsu -c \"/system/bin/reboot\"");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.shutdown://开机
                new AlertDialog.Builder(MainActivity.this).setTitle("温馨提示：")
                        .setMessage("确定要关机吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Process process = Runtime.getRuntime().exec("xbsu");
                                    DataOutputStream out = new DataOutputStream(process.getOutputStream());
                                    out.writeBytes("reboot -p\n");
                                    out.writeBytes("exit\n");
                                    out.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
        }
    }

    //添加listView的数据
    private void setListViewAdapter(String[] strings) {

        List<String> data = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            data.add(strings[i]);
        }
        mAdapter = new ListViewAdapter(this, data, mTypeFace);
        mListView.setAdapter(mAdapter);
        mListView.setItemsCanFocus(true);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    //诊断程序Item点击事件
    private void onDiagnosisItemClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://硬件自检
                        mListView.setItemChecked(position, true);
                        initHardwareFragment();
                        break;
                    case 1://主显测试
                        mListView.setItemChecked(position, true);
                        initMainScreenTestFragment();
                        break;
                    case 2://触屏测试
                        mListView.setItemChecked(position, true);
                        initTouchScreenTestFragment();
                        break;
                    case 3://副屏设置
                        mListView.setItemChecked(position, true);
                        initSecoScreenSetFragment();
                        break;
                    case 4://声音测试
                        mListView.setItemChecked(position, true);
                        initSoundTestFragment();
                        break;
                    case 5://网络通信
                        mListView.setItemChecked(position, true);
                        initNetworkFragment();
                        break;
                    case 6://信号测试
                        mListView.setItemChecked(position, true);
                        initSignalTestFragment();
                        break;
                    case 7://定位测试
                        mListView.setItemChecked(position, true);
                        initLocationFragment();
                        break;
//                    case 8://条码识读
//                        mListView.setItemChecked(position, true);
//                        initBarcodeReaderFragment();
//                        break;
                    case 8://系统信息
                        mListView.setItemChecked(position, true);
                        initSystemFragment();
                        break;
                }
            }
        });
    }

    //设置的Item点击事件
    private void onSettingItemClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://分辨率
                        mListView.setItemChecked(position, true);
                        initResolvingPowerFragment();
                        break;
                    case 1://亮度
                        mListView.setItemChecked(position, true);
                        initBrightnessFragment();
                        break;
                    case 2://音量
                        mListView.setItemChecked(position, true);
                        initSoundFragment();
                        break;
                    case 3://时间
                        mListView.setItemChecked(position, true);
                        initTimeFragment();
                        break;
                    case 4://版本
                        mListView.setItemChecked(position, true);
                        initEditionFragment();
                        break;
                }
            }
        });
    }

    //初始化并更改按钮背景
    private void initBackground(TextView textView) {
        mDiagnoseProcedure.setBackgroundResource(R.drawable.btn_color_normal);
        mPrinter.setBackgroundResource(R.drawable.btn_color_normal);
        mTicketReader.setBackgroundResource(R.drawable.btn_color_normal);
        mScannerGun.setBackgroundResource(R.drawable.btn_color_normal);
        mToastChance.setBackgroundResource(R.drawable.btn_color_normal);
        mSetting.setBackgroundResource(R.drawable.btn_color_normal);
//        mKeyboard.setBackgroundResource(R.drawable.btn_color_normal);
        textView.setBackgroundResource(R.drawable.btn_color_pressed);
    }

    //隐藏所有fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (mMachineFragment != null) {
            transaction.hide(mMachineFragment);
        }
        if (mBarcodeFragment != null) {
            transaction.hide(mBarcodeFragment);
        }
        if (mBrightnessFragment != null) {
            transaction.hide(mBrightnessFragment);
        }
        if (mEditionFragment != null) {
            transaction.hide(mEditionFragment);
        }
        if (mHardwareFragment != null) {
            transaction.hide(mHardwareFragment);
        }
        if (mLocationFragment != null) {
            transaction.hide(mLocationFragment);
        }
        if (mMainScreenFragment != null) {
            transaction.hide(mMainScreenFragment);
        }
        if (mNetworkFragment != null) {
            transaction.hide(mNetworkFragment);
        }
        if (mPrinterFragment != null) {
            transaction.hide(mPrinterFragment);
        }
        if (mPowerFragment != null) {
            transaction.hide(mPowerFragment);
        }
        if (mScanningGunFragment != null) {
            transaction.hide(mScanningGunFragment);
        }
        if (mSecoScreenFragment != null) {
            transaction.hide(mSecoScreenFragment);
        }
        if (mSignalFragment != null) {
            transaction.hide(mSignalFragment);
        }
        if (mSoundTestFragment != null) {
            transaction.hide(mSoundTestFragment);
        }
        if (mSystemFragment != null) {
            transaction.hide(mSystemFragment);
        }
        if (mTicketFragment != null) {
            transaction.hide(mTicketFragment);
        }
        if (mTimeFragment != null) {
            transaction.hide(mTimeFragment);
        }
        if (mTouchScreenFragment != null) {
            transaction.hide(mTouchScreenFragment);
        }
        if (mBatteryFragment != null) {
            transaction.hide(mBatteryFragment);
        }
        if (mSoundFragment != null) {
            transaction.hide(mSoundFragment);
        }
    }

    //初始化BakingMachineFragment
    private void initBakingMachineFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mMachineFragment == null) {
            mMachineFragment = new BakingMachineFragment();
            mFt.add(R.id.container, mMachineFragment);
        }
        hideFragment(mFt);
        mFt.show(mMachineFragment);
        mFt.commit();
    }

    //初始化BarcodeReaderFragment
    private void initBarcodeReaderFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mBarcodeFragment == null) {
            mBarcodeFragment = new BarcodeReaderFragment();
            mFt.add(R.id.container, mBarcodeFragment);
        }
        hideFragment(mFt);
        mFt.show(mBarcodeFragment);
        mFt.commit();
    }

    //初始化BrightnessFragment
    private void initBrightnessFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mBrightnessFragment == null) {
            mBrightnessFragment = new BrightnessFragment();
            mFt.add(R.id.container, mBrightnessFragment);
        }
        hideFragment(mFt);
        mFt.show(mBrightnessFragment);
        mFt.commit();
    }

    //初始化EditionFragment
    private void initEditionFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mEditionFragment == null) {
            mEditionFragment = new EditionFragment();
            mFt.add(R.id.container, mEditionFragment);
        }
        hideFragment(mFt);
        mFt.show(mEditionFragment);
        mFt.commit();
    }

    //初始化HardwareFragment
    private void initHardwareFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mHardwareFragment == null) {
            mHardwareFragment = new HardwareFragment();
            mFt.add(R.id.container, mHardwareFragment);
        }
        hideFragment(mFt);
        mFt.show(mHardwareFragment);
        mFt.commit();
    }

    //初始化LocationFragment
    private void initLocationFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mLocationFragment == null) {
            mLocationFragment = new LocationFragment();
            mFt.add(R.id.container, mLocationFragment);
        }
        hideFragment(mFt);
        mFt.show(mLocationFragment);
        mFt.commit();
    }

    //初始化MainScreenTestFragment
    private void initMainScreenTestFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mMainScreenFragment == null) {
            mMainScreenFragment = new MainScreenTestFragment();
            mFt.add(R.id.container, mMainScreenFragment);
        }
        hideFragment(mFt);
        mFt.show(mMainScreenFragment);
        mFt.commit();
    }

    //初始化NetworkFragment
    private void initNetworkFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mNetworkFragment == null) {
            mNetworkFragment = new NetworkFragment();
            mFt.add(R.id.container, mNetworkFragment);
        }
        hideFragment(mFt);
        mFt.show(mNetworkFragment);
        mFt.commit();
    }

    //初始化PrinterFragment
    private void initPrinterFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mPrinterFragment == null) {
            mPrinterFragment = new PrinterFragment();
            mFt.add(R.id.container, mPrinterFragment);
        }
        hideFragment(mFt);
        mFt.show(mPrinterFragment);
        mFt.commit();
    }

    //初始化ResolvingPowerFragment
    private void initResolvingPowerFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mPowerFragment == null) {
            mPowerFragment = new ResolvingPowerFragment();
            mFt.add(R.id.container, mPowerFragment);
        }
        hideFragment(mFt);
        mFt.show(mPowerFragment);
        mFt.commit();
    }

    //初始化ScanningGunFragment
    private void initScanningGunFragment() {
//        mFt = getSupportFragmentManager().beginTransaction();
//        if (mScanningGunFragment == null) {
//            mScanningGunFragment = new ScanningGunFragment();
//            mFt.add(R.id.container, mScanningGunFragment);
//        }
//        hideFragment(mFt);
//        mFt.show(mScanningGunFragment);
//        mFt.commit();
        //用同一个fragment
        mFt = getSupportFragmentManager().beginTransaction();
        if (mBarcodeFragment == null) {
            mBarcodeFragment = new BarcodeReaderFragment();
            mFt.add(R.id.container, mBarcodeFragment);
        }
        hideFragment(mFt);
        mFt.show(mBarcodeFragment);
        mFt.commit();
    }

    //初始化SecoScreenSetFragment
    private void initSecoScreenSetFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mSecoScreenFragment == null) {
            mSecoScreenFragment = new SecoScreenSetFragment();
            mFt.add(R.id.container, mSecoScreenFragment);
        }
        hideFragment(mFt);
        mFt.show(mSecoScreenFragment);
        mFt.commit();
    }

    //初始化SignalTestFragment
    private void initSignalTestFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mSignalFragment == null) {
            mSignalFragment = new SignalTestFragment();
            mFt.add(R.id.container, mSignalFragment);
        }
        hideFragment(mFt);
        mFt.show(mSignalFragment);
        mFt.commit();
    }

    //初始化SoundTestFragment
    private void initSoundTestFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mSoundTestFragment == null) {
            mSoundTestFragment = new SoundTestFragment();
            mFt.add(R.id.container, mSoundTestFragment);
        }
        hideFragment(mFt);
        mFt.show(mSoundTestFragment);
        mFt.commit();
    }

    //初始化SoundFragment
    private void initSoundFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mSoundFragment == null) {
            mSoundFragment = new SoundFragment();
            mFt.add(R.id.container, mSoundFragment);
        }
        hideFragment(mFt);
        mFt.show(mSoundFragment);
        mFt.commit();
    }

    private void initKeyBoardFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mBatteryFragment == null) {
            mBatteryFragment = new BatteryFragment();
            mFt.add(R.id.container, mBatteryFragment);
        }
        hideFragment(mFt);
        mFt.show(mBatteryFragment);
        mFt.commit();
    }

    //初始化SystemFragment
    private void initSystemFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mSystemFragment == null) {
            mSystemFragment = new SystemFragment();
            mFt.add(R.id.container, mSystemFragment);
        }
        hideFragment(mFt);
        mFt.show(mSystemFragment);
        mFt.commit();
    }

    //初始化TicketReaderFragment
    private void initTicketReaderFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mTicketFragment == null) {
            mTicketFragment = new TicketReaderFragment();
            mFt.add(R.id.container, mTicketFragment);
        }
        hideFragment(mFt);
        mFt.show(mTicketFragment);
        mFt.commit();
    }

    //初始化TimeFragment
    private void initTimeFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mTimeFragment == null) {
            mTimeFragment = new TimeFragment();
            mFt.add(R.id.container, mTimeFragment);
        }
        hideFragment(mFt);
        mFt.show(mTimeFragment);
        mFt.commit();
    }

    //初始化TouchScreenTestFragment
    private void initTouchScreenTestFragment() {
        mFt = getSupportFragmentManager().beginTransaction();
        if (mTouchScreenFragment == null) {
            mTouchScreenFragment = new TouchScreenTestFragment();
            mFt.add(R.id.container, mTouchScreenFragment);
        }
        hideFragment(mFt);
        mFt.show(mTouchScreenFragment);
        mFt.commit();
    }
}
