package com.example.lederui.developmenttest.view;

import android.app.Presentation;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.utils.GetVideoUri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class DifferentDisplay extends Presentation implements View.OnClickListener{
    public DifferentDisplay(Context outerContext, Display display) {
        super(outerContext, display);
    }

    private List fileNames = new ArrayList();
    private VideoView mSimpleVideoPlayer;
//    private MediaController mController;
    private boolean isPlay = true;
    private boolean isShow = false;
    private ImageButton mPlay;
    private ImageButton mRewind;
    private ImageButton mSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_asyn_display);
        mSimpleVideoPlayer = (VideoView) findViewById(R.id.asyn_simpleVideoPlayer);
        mPlay = (ImageButton) findViewById(R.id.syn_play);
        mRewind = (ImageButton) findViewById(R.id.rewind);
        mSpeed = (ImageButton) findViewById(R.id.speed);
        mPlay.setOnClickListener(this);
        mRewind.setOnClickListener(this);
        mSpeed.setOnClickListener(this);
//        mController = new MediaController(getContext());
        fileNames.clear();
        Cursor cursor = getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            fileNames.add(new String(data, 0, data.length - 1));
        }
        for (int i = 0; i < fileNames.size(); i++) {
            String str = (String) fileNames.get(i);
            String[] name = str.split("/");
            if (name[name.length - 1].equals("御龙在天avi格式_标清.avi")) {
                Uri videoUri = GetVideoUri.getMediaUriFromPath(getContext(), fileNames.get(i) + "");
                mSimpleVideoPlayer.setVideoURI(Uri.parse(videoUri + ""));
//                mSimpleVideoPlayer.setMediaController(mController);
                mSimpleVideoPlayer.start();
                mSimpleVideoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.start();
                        mp.setLooping(true);
                    }
                });
            }
        }
//        mSimpleVideoPlayer.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (mController != null){
//                    if (isShow){
//                        mController.show();
//                        isShow = true;
//                    }else {
//                        mController.hide();
//                        isShow = false;
//                    }
//                }
//                return true;
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.syn_play:
                if (isPlay){
                    mSimpleVideoPlayer.pause();
                    isPlay = false;
                    mPlay.setBackgroundResource(R.drawable.pause);
                }else {
                    mSimpleVideoPlayer.start();
                    isPlay = true;
                    mPlay.setBackgroundResource(R.drawable.play);
                }
                break;
            case R.id.rewind:
                int currentPosition = mSimpleVideoPlayer.getCurrentPosition();
                if (currentPosition > 0){
                    int rewindPosition = currentPosition - 2000;
                    if (rewindPosition <= 0){
                        currentPosition = 0;
                        mSimpleVideoPlayer.seekTo(currentPosition);
                    }else {
                        currentPosition = rewindPosition;
                        mSimpleVideoPlayer.seekTo(currentPosition);
                    }
                }
                break;
            case R.id.speed:
                Toast.makeText(getContext(), "暂未实现", Toast.LENGTH_SHORT).show();
//                int curPosition = mSimpleVideoPlayer.getCurrentPosition();
//                Log.e("curPosition", curPosition + "");
//                int lengthOfTime = mSimpleVideoPlayer.getDuration();
//                Log.e("lengthOfTime", lengthOfTime + "");
//                if (curPosition < lengthOfTime){
//                    int speedPosition = curPosition + 3000;
//                    Log.e("speedPosition", speedPosition + "");
//                    if (speedPosition > lengthOfTime){
//                        curPosition = lengthOfTime;
//                        Log.e("curPosition", curPosition + "");
//                        mSimpleVideoPlayer.seekTo(curPosition);
//                    }else {
//                        curPosition = speedPosition;
//                        Log.e("curPosition", curPosition + "");
//                        mSimpleVideoPlayer.seekTo(curPosition);
//                    }
//                }
                break;
        }
    }
}
