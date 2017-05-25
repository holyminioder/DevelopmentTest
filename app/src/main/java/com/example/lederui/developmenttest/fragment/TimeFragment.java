package com.example.lederui.developmenttest.fragment;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lederui.developmenttest.R;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 时间Fragment
 */
public class TimeFragment extends Fragment {

    @BindView(R.id.et_year)
    EditText mEtYear;
    @BindView(R.id.et_month)
    EditText mEtMonth;
    @BindView(R.id.et_day)
    EditText mEtDay;
    @BindView(R.id.et_hour)
    EditText mEtHour;
    @BindView(R.id.et_minute)
    EditText mEtMinute;
    @BindView(R.id.et_second)
    EditText mEtSecond;
    @BindView(R.id.btn_ensure)
    Button mBtnEnsure;
    Unbinder unbinder;

    private String mTimeZone = "GMT+08:00";

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            long val = data.getLong("value");
            Date date = new Date(val);
            DateFormat timeFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.ENGLISH);
            timeFormat.setTimeZone(TimeZone.getTimeZone(mTimeZone));
            String time = timeFormat.format(date);
            String[] times = time.split(":");
            mEtYear.setText(times[0]);
            mEtMonth.setText(times[1]);
            mEtDay.setText(times[2]);
            mEtHour.setText(times[3]);
            mEtMinute.setText(times[4]);
            mEtSecond.setText(times[5]);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time, container, false);
        unbinder = ButterKnife.bind(this, view);
        new Thread(runnable).start();
        return view;
    }

    public void setSystemTime(Context cxt, String datetimes) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            String datetime = "";
            datetime = datetimes.toString();
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT+16:00\n");
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            Log.e("异常信息", e.toString());
            Toast.makeText(cxt, "请获取Root权限", Toast.LENGTH_SHORT).show();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url=new URL("http://www.baidu.com");//取得资源对象
                URLConnection uc=url.openConnection();//生成连接对象
                uc.connect(); //发出连接
                long ld=uc.getDate(); //取得网站日期时间
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putLong("value", ld);
                msg.setData(data);
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @OnClick({R.id.btn_ensure, R.id.btn_netTime})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btn_ensure:
                String year = mEtYear.getText().toString();
                String month = mEtMonth.getText().toString();
                String day = mEtDay.getText().toString();
                String hour = mEtHour.getText().toString();
                String minute = mEtMinute.getText().toString();
                String second = mEtSecond.getText().toString();
                if (TextUtils.isEmpty(year)||TextUtils.isEmpty(month)||TextUtils.isEmpty(day)||
                        TextUtils.isEmpty(hour)|| TextUtils.isEmpty(minute)||TextUtils.isEmpty(second)){
                    Toast.makeText(getContext(), "请确认是否留空", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                buffer.append(year).append(month).append(day).append(".").append(hour)
                        .append(minute).append(second);
                setSystemTime(getContext(), buffer.toString());
                break;
            case R.id.btn_netTime:
                new Thread(runnable).start();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
