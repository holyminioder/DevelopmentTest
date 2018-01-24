package com.example.lederui.developmenttest.fragment;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.data.PrinterInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.lederui.developmenttest.data.PrinterInterface.GetLastErrStr;
import static com.example.lederui.developmenttest.data.PrinterInterface.PrintInit;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 烤机Fragment
 */
public class BakingMachineFragment extends Fragment {
    @BindView(R.id.printer_status)
    TextView printerStatus;
    @BindView(R.id.videoView)
    VideoView mVideoView;
    private PrinterInterface mPrintInterface = new PrinterInterface();

    Unbinder unbinder;
    @BindView(R.id.play)
    Button play;
    @BindView(R.id.print)
    Button print;
    @BindView(R.id.select_num)
    Spinner selectNum;

    private int num = 0;
    private boolean isplaying = true;
    private String mPDFCode;
    private int mCutMode = 0;

    private boolean i = false;
    private Timer timer;
    private String str = "";
    private Timer printInterval;
    private boolean isPrint = false;
    private List fileNames = new ArrayList();
    private boolean normalPlay = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baking_machine, container, false);
        unbinder = ButterKnife.bind(this, view);
        fileNames.clear();
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            fileNames.add(new String(data, 0, data.length - 1));
        }
        playVideo();
        initPrinter();
        errorPrompt();
        return view;
    }

    private void initPrinter() {
        PrintInit();
    }

    private void playVideo() {
        String videoUrl = Environment.getExternalStorageDirectory().getPath() + "/AFD-MP4-800600.mp4";
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
        mVideoView.setVideoPath(videoUrl);
        mVideoView.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        num++;
        if (num == 3) {
            mVideoView.pause();
            isplaying = false;
            play.setText("视频播放");
            timer.cancel();
        }
        if (num == 4) {
            mVideoView.start();
            play.setText("视频暂停");
            isplaying = true;
            errorPrompt();
            num = 2;
        }
    }

    @OnClick({R.id.play, R.id.print})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.play:
                if (isplaying) {
                    mVideoView.pause();
                    play.setText("视频播放");
                    isplaying = false;
                } else {
                    mVideoView.start();
                    play.setText("视频暂停");
                    isplaying = true;
                }
                break;
            case R.id.print:

                if (!PrintInit()) return;
                String strNum = selectNum.getSelectedItem().toString();

                int num = Integer.valueOf(strNum);
                if(num == 5000){
                    num = 10;
                }
                if (!isPrint) {

                    print.setText("停止打印");
                    printInterval = new Timer();
                    printInterval.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!mPrintInterface.PrintSample2(mCutMode)) {
                                if(PrintInit()){
                                    mPrintInterface.PrintSample2(mCutMode);
                                }else{
                                    String str = GetLastErrStr();
                                }

                            } else {
                                mPDFCode = mPrintInterface.GetPDFCode();
                                Log.i("printer", "btn_printSample mpdfcode = " + mPDFCode);
                            }

                        }
                    }, 0, 1000 * num);
                    isPrint = true;
                } else {
                    printInterval.cancel();
                    isPrint = false;
                    print.setText("开始打印");
                }
                break;
        }
    }

    private void errorPrompt() {
        final Handler handler = new Handler();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        final int printStatus = mPrintInterface.PrinterStatus();
                        switch (printStatus) {
                            case 0:

                                break;
                            case 1:
//                                printerStatus.setText("打印机状态：找不到打印机");
                                break;
                            case 2:
//                                printerStatus.setText("打印机状态：数据线故障");
                                break;
                            case 3:
//                                printerStatus.setText("打印机状态：电源线故障");
                                break;
                            case 4:
//                                printerStatus.setText("打印机状态：打印机忙");
                                break;
                            case 5:
//                                printerStatus.setText("打印机状态：超时");
                                break;
                            case 8:
//                                printerStatus.setText("打印机状态：打印机上盖被打卡");
                                break;
                            case 9:
//                                printerStatus.setText("打印机状态：纸卷错误");
                                break;
                            case 10:
//                                printerStatus.setText("打印机状态：纸将尽");
                                break;
                            case 11:
//                                printerStatus.setText("打印机状态：其他错误");
                                break;
                            case 500:
//                                printerStatus.setText("打印机状态：正常");
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        }, 3000, 10000);
    }
}
