package com.example.lederui.developmenttest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lederui.developmenttest.R;

/**
 * Created by holyminier on 2017/4/21.
 */

/** 信号测试Fragment */
public class SignalTestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signal_test, container, false);
    }
}
