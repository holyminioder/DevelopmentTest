package com.example.lederui.developmenttest.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.data.MainBoardMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 系统信息Fragment
 */
public class SystemFragment extends Fragment {

    @BindView(R.id.versions)
    EditText mVersions;
    @BindView(R.id.jurisdiction)
    EditText mJurisdiction;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mVersions.setText(Build.VERSION.RELEASE);
        mJurisdiction.setText(MainBoardMessage.getSEAndroid());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
