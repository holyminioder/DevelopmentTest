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

    private static final int NETWORKTYPE_WIFI = 0;
    private static final int NETWORKTYPE_4G = 1;
    private static final int NETWORKTYPE_2G = 2;
    private static final int NETWORKTYPE_NONE = 3;
    public TelephonyManager mTelephonyManager;
    public PhoneStatListener mListener;
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
//                    Toast.makeText(WIFIActivity.this,
//                            "信号强度：" + level + " 无信号", Toast.LENGTH_SHORT)
//                            .show();
                    break;
                default:
                    //以防万一
                    wifiImage.setImageResource(R.drawable.id_default1_grey);
                    strength.setText("信号强度：" + level);
//                    Toast.makeText(WIFIActivity.this, "无信号",
//                            Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signal_test, container, false);
        unbinder = ButterKnife.bind(this, view);
        //获取telephonyManager
        mTelephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        //开始监听
        mListener = new PhoneStatListener();
        //监听信号强度
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
    }
    @Override
    public void onPause() {
        super.onPause();
        //用户不在当前页面时，停止监听
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_NONE);
    }
    private class PhoneStatListener extends PhoneStateListener {
        //获取信号强度
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            //获取网络信号强度
            //获取0-4的5种信号级别，越大信号越好,但是api23开始才能用
            // int level = signalStrength.getLevel();
            int gsmSignalStrength = signalStrength.getGsmSignalStrength();
            //获取网络类型
            int netWorkType = getNetWorkType(getContext());
            switch (netWorkType) {
                case NETWORKTYPE_WIFI:
                    getWiFiStrenght();
                    break;
                case NETWORKTYPE_2G:
                    strength.setText("当前网络为2G移动网络,信号强度为：" + gsmSignalStrength);
                    break;
                case NETWORKTYPE_4G:
                    strength.setText("当前网络为4G移动网络,信号强度为：" + gsmSignalStrength);
                    break;
                case NETWORKTYPE_NONE:
                    strength.setText("当前没有网络,信号强度为：" + gsmSignalStrength);
                    break;
                case -1:
                    strength.setText("当前网络错误,信号强度为：" + gsmSignalStrength);
                    break;
            }
        }
    }
    public static int getNetWorkType(Context context) {
        int mNetWorkType = -1;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                return isFastMobileNetwork(context) ? NETWORKTYPE_4G : NETWORKTYPE_2G;
            }
        } else {
            mNetWorkType = NETWORKTYPE_NONE;//没有网络
        }
        return mNetWorkType;
    }
    /**判断网络类型*/
    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
            //这里只简单区分两种类型网络，认为4G网络为快速，但最终还需要参考信号值
            return true;
        }
        return false;
    }

    private void getWiFiStrenght(){
        // 获得WifiManager
        wifiManager = (WifiManager) getContext().getSystemService(WIFI_SERVICE);
        // 使用定时器,每隔5秒获得一次信号强度值
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
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
