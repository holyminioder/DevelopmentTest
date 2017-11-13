package com.example.lederui.developmenttest.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lederui.developmenttest.R;

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

    @BindView(R.id.wifi_image)
    ImageView wifiImage; //信号图片显示
    @BindView(R.id.signal_strength)
    TextView strength;
    Unbinder unbinder;

    public TelephonyManager mTelephonyManager;
    private WifiInfo wifiInfo = null;       //获得的Wifi信息
    private WifiManager wifiManager = null; //Wifi管理器
    private int level;                      //信号强度值

    // 使用Handler实现UI线程与Timer线程之间的信息传递,每3秒告诉UI线程获得wifiInto
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 如果收到正确的消息就获取WifiInfo，改变图片并显示信号强度
                case 1:
                    wifiImage.setImageResource(R.drawable.id_default);
                    strength.setText("信号强度：" + level);
                    break;
                case 2:
                    wifiImage.setImageResource(R.drawable.id_default3);
                    strength.setText("信号强度：" + level);
                    break;
                case 3:
                    wifiImage.setImageResource(R.drawable.id_default2);
                    strength.setText("信号强度：" + level);
                    break;
                case 4:
                    wifiImage.setImageResource(R.drawable.id_default1);
                    strength.setText("信号强度：" + level);
                    break;
                case 5:
                    wifiImage.setImageResource(R.drawable.id_default1_grey);
                    strength.setText("信号强度：" + level);
                    break;
                default:
                    //以防万一
                    wifiImage.setImageResource(R.drawable.id_default1_grey);
                    strength.setText("信号强度：" + level);
            }
        }
    };
    private Timer mTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signal_test, container, false);
        unbinder = ButterKnife.bind(this, view);
        //获取telephonyManager
        mTelephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
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
        if (hidden){
            mTimer.cancel();
        }else {
            getWiFiStrenght();
        }
    }

    private void getWiFiStrenght(){
        // 获得WifiManager
        wifiManager = (WifiManager) getContext().getSystemService(WIFI_SERVICE);
        // 使用定时器,每隔5秒获得一次信号强度值
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                wifiInfo = wifiManager.getConnectionInfo();
                //获得信号强度值
                level = wifiInfo.getRssi();
                //根据获得的信号强度发送信息
                if (level <= 0 && level >= -55) {
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } else if (level < -55 && level >= -65) {
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                } else if (level < -65 && level >= -75) {
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                } else if (level < -75 && level >= -85) {
                    Message msg = new Message();
                    msg.what = 4;
                    handler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = 5;
                    handler.sendMessage(msg);
                }
            }
        }, 1000, 3000);
    }
}
