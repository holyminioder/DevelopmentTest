package com.example.lederui.developmenttest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.data.MainBoardMessage;
import com.example.lederui.developmenttest.data.PrinterInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 硬件信息Fragment
 */
public class HardwareFragment extends Fragment {

    @BindView(R.id.main_board_message)
    TextView mMainMessage;
    @BindView(R.id.printer_hw_info)
    TextView mPrinterHwInfoView;

    Unbinder unbinder;
    @BindView(R.id.main_board_status)
    TextView mMainBoardStatus;
    private PrinterInterface mPrinterLib;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hardware, container, false);
        unbinder = ButterKnife.bind(this, view);

        getMainBoardInfo();
//        getPrinterHWInfo();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getMainBoardInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("CPU型号：");
        sb.append(MainBoardMessage.getCpuInfo() + "\n" + "\n");
        sb.append("CPU主频：");
        sb.append(MainBoardMessage.getCurCpuFreq() + " GHZ" + "\n" + "\n");
        sb.append("CPU核心数：");
        sb.append(MainBoardMessage.getNumCores() + "\n" + "\n");
        sb.append("内存容量：");
        sb.append(MainBoardMessage.getTotalRam() + "\n" + "\n");
        sb.append("存储容量：");
        sb.append(MainBoardMessage.getStorageSize() + "\n" + "\n");
        sb.append("USB口数量：");
        sb.append(MainBoardMessage.getUsbInterface(getContext()));

        mMainMessage.setText(sb.toString());
        if (TextUtils.isEmpty(sb)) {
            mMainBoardStatus.setText("主板异常");
            mMainBoardStatus.setTextColor(getResources().getColor(R.color.read_color));
        }
    }

    //获取打印机硬件信息
    private void getPrinterHWInfo() {
        String info = mPrinterLib.GetPrintHwInfo();
        mPrinterHwInfoView.setText(info + "");
        Log.i("printer", "hwinfo =" + info);
    }
}
