package com.example.lederui.developmenttest.fragment;

import android.content.Context;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void setSystemTime(Context cxt, String datetimes) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            String datetime = "";
            datetime = datetimes.toString();
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT+08:00\n");
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            Log.e("异常信息", e.toString());
            Toast.makeText(cxt, "请获取Root权限", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_ensure)
    public void onViewClicked() {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
