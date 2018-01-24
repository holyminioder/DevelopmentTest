package com.example.lederui.developmenttest.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.view.VolumeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/26.
 */

public class SoundFragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.sub)
    Button mSub;
    @BindView(R.id.seekBar)
    SeekBar mSeekBar;
    @BindView(R.id.add)
    Button mAdd;
    private AudioManager mAudioManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound, container, false);
        unbinder = ButterKnife.bind(this, view);
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);//初始化音量管理
        mSeekBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));//设置滑动条最大值
        mSeekBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));//设置滑动条当前数值
        mSeekBar.setOnSeekBarChangeListener(mListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //滑动条滑动事件
    private SeekBar.OnSeekBarChangeListener mListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);//修改音量
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @OnClick({R.id.sub, R.id.add})
    public void onViewClicked(View view) {
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int num = 1;
        switch (view.getId()) {
            case R.id.sub:
                if (volume - num >= 0){
                    int bad = volume - num;
                    mSeekBar.setProgress(bad);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, bad, AudioManager.FLAG_PLAY_SOUND);
                }else {
                    volume = 0;
                    mSeekBar.setProgress(volume);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
                }
                break;
            case R.id.add:
                if (volume + num <= maxVolume){
                    int add = volume + num;
                    mSeekBar.setProgress(add);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, add, AudioManager.FLAG_PLAY_SOUND);
                }else {
                    volume = maxVolume;
                    mSeekBar.setProgress(volume);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
                }
                break;
        }
    }
}
