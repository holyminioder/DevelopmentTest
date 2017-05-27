package com.example.lederui.developmenttest.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.activity.TouchReviseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 触摸屏测试Fragment
 */
public class TouchScreenTestFragment extends Fragment {

    @BindView(R.id.touch_revise)
    Button mTouchRevise;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_touch_screen_test, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.touch_revise)
    public void onViewClicked() {
        Intent intent = new Intent(getContext(), TouchReviseActivity.class);
        startActivity(intent);
    }
}
