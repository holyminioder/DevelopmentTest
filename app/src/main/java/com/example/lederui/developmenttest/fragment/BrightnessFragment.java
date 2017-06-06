package com.example.lederui.developmenttest.fragment;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.lederui.developmenttest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 亮度Fragment
 */
public class BrightnessFragment extends Fragment {

    @BindView(R.id.btn_sub)
    Button mBtnSub;
    @BindView(R.id.seek_bar)
    SeekBar mSeekBar;
    @BindView(R.id.btn_add)
    Button mBtnAdd;
    @BindView(R.id.text_progress)
    TextView mTextProgress;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brightness, container, false);
        unbinder = ButterKnife.bind(this, view);
        initSet();
        mSeekBar.setOnSeekBarChangeListener(mListener);
        return view;
    }

    private void initSet() {
        setScrennManualMode();
        try {
            int systemLight = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            int light = (int) ((double) systemLight/255 * 100);
            mSeekBar.setProgress(systemLight);
            mTextProgress.setText("当前亮度：" + light + "%");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private SeekBar.OnSeekBarChangeListener mListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int value = (int) ((double) progress/255 * 100);
            mTextProgress.setText("当前亮度：" + value + "%");
            Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_sub, R.id.btn_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sub:
                try {
                    int subLight = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                    int num = 10;
                    if (subLight - num >= 0){
                        int bad = subLight - num;
                        int light = (int) ((double) bad/255 * 100);
                        mSeekBar.setProgress(bad);
                        mTextProgress.setText("当前亮度：" + light + "%");
                        Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, bad);
                    }else {
                        subLight = 0;
                        mSeekBar.setProgress(subLight);
                        mTextProgress.setText("当前亮度：0%");
                        Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, subLight);
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_add:
                try {
                    int addLight = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                    int num = 20;
                    if (addLight + num <= 255){
                        int sum = addLight + num;
                        int light = (int) ((double) sum/255 * 100);
                        mSeekBar.setProgress(sum);
                        mTextProgress.setText("当前亮度：" + light + "%");
                        Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, sum);
                    }else {
                        addLight = 255;
                        mSeekBar.setProgress(addLight);
                        mTextProgress.setText("当前亮度：100%");
                        Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, addLight);
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void setScrennManualMode() {
        ContentResolver contentResolver = getContext().getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }
}
