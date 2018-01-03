package com.example.lederui.developmenttest.fragment;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lederui.developmenttest.R;
import com.zhanshow.mylibrary.phonestate.MyPhoneStateListener;
import com.zhanshow.mylibrary.phonestate.PhoneStateUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 信号测试Fragment
 */
public class SignalTestFragment extends Fragment {

    @BindView(R.id.signal_strength)
    TextView strength;
    Unbinder unbinder;

    public TelephonyManager mTelephonyManager;
    @BindView(R.id.phone_strength)
    TextView mPhoneStrength;
    @BindView(R.id.operation)
    TextView mOperation;
    private WifiInfo wifiInfo = null;       //获得的Wifi信息
    private WifiManager wifiManager = null; //Wifi管理器
    private int level;                      //信号强度值
    private Handler mHandler = null;
    private Timer mTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signal_test, container, false);
        unbinder = ButterKnife.bind(this, view);
        //获取telephonyManager
        mTelephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        mOperation.setText(getOperations(getContext()) + "：");
        //开始监听
        getWiFiStrenght();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mTimer.cancel();
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mTimer.cancel();
        } else {
            getWiFiStrenght();
        }
    }

    public String getOperations(Context context){
        String opreator = null;
        String IMSI = mTelephonyManager.getSubscriberId();
        if (IMSI == null || IMSI.equals("")){
            return opreator;
        }
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")){
            opreator = "中国移动";
        }else if (IMSI.startsWith("46001")){
            opreator = "中国联通";
        }else if (IMSI.startsWith("46003")){
            opreator = "中国电信";
        }
        return opreator;
    }

    private void getWiFiStrenght() {
        // 获得WifiManager
        wifiManager = (WifiManager) getContext().getSystemService(WIFI_SERVICE);
        // 使用定时器,每隔5秒获得一次信号强度值
        mHandler = new Handler();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        wifiInfo = wifiManager.getConnectionInfo();
                        //获得信号强度值
                        level = wifiInfo.getRssi();
                        strength.setText("WIFI信号强度值为：" + level);
                    }
                });
            }
        }, 1000, 1000 * 3);
        if (hasSimCard()) {
            PhoneStateUtils.registerPhoneStateListener(getActivity(), new MyPhoneStateListener.MyPhoneStateListenerListener() {
                @Override
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    String str = String.valueOf(signalStrength);
                    String[] strings = str.split(" ");
                    String signal = strings[9];
                    mPhoneStrength.setText("4G信号强度值为：" + signal);
                }
            });
        } else {
            mOperation.setVisibility(View.INVISIBLE);
            mPhoneStrength.setText("未找到sim卡");
        }
    }

    public boolean hasSimCard() {
        int simState = mTelephonyManager.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false;
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        return result;
    }
}
