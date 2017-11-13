package com.example.lederui.developmenttest.fragment;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.view.DifferentDisplay;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 副屏设置Fragment
 */
public class SecoScreenSetFragment extends Fragment {

    @BindView(R.id.syn)
    Button mSyn;
    @BindView(R.id.asyn)
    Button mAsyn;
    Unbinder unbinder;

    private DisplayManager mDisplayManager;
    private DifferentDisplay mPresentation;
    private boolean asyn = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seco_screen_setting, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.syn, R.id.asyn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.syn:
                if (asyn){
                    mPresentation.cancel();
                    mPresentation = null;
                    asyn = false;
                }
                break;
            case R.id.asyn:
                mDisplayManager = (DisplayManager) getActivity().getSystemService(Context.DISPLAY_SERVICE);
                Display[] displays = mDisplayManager.getDisplays();
                if (!asyn){
                    if (mPresentation == null){
                        mPresentation = new DifferentDisplay(getActivity(), displays[displays.length - 1]);
                        mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    }
                    mPresentation.show();
                    asyn = true;
                }
                break;
        }
    }
}
