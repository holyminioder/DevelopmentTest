package com.example.lederui.developmenttest.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.view.VolumeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/26.
 */

public class SoundFragment extends Fragment implements VolumeView.MoveInterface{

    @BindView(R.id.move_view)
    VolumeView mMoveView;
    @BindView(R.id.value)
    TextView mValue;
    Unbinder unbinder;
    private AudioManager mAudioManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound, container, false);
        unbinder = ButterKnife.bind(this, view);
        mMoveView.setMoveInterface(this);
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void getCurrentDegrees(int degress) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                degress/6, AudioManager.FLAG_PLAY_SOUND);
        mValue.setText("当前音量：" + degress + "%");
    }
}
