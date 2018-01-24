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
import android.widget.LinearLayout;
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
    private LinearLayout mControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_asyn_display);
        mSimpleVideoPlayer = (VideoView) findViewById(R.id.asyn_simpleVideoPlayer);
        mPlay = (ImageButton) findViewById(R.id.syn_play);
        mRewind = (ImageButton) findViewById(R.id.rewind);
        mSpeed = (ImageButton) findViewById(R.id.speed);
        mControl = (LinearLayout) findViewById(R.id.ll_control);
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
//                if (isShow){
//                    mControl.setVisibility(View.VISIBLE);
//                    isShow = false;
//                }else{
//                    mControl.setVisibility(View.INVISIBLE);
//                    isShow = true;
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
                int pos = mSimpleVideoPlayer.getCurrentPosition();
                pos -= 5000; // milliseconds
                mSimpleVideoPlayer.seekTo(pos);
                break;
            case R.id.speed:
                int post = mSimpleVideoPlayer.getCurrentPosition();
                post += 10000; // milliseconds
                if (post > mSimpleVideoPlayer.getDuration()){
                    post -= 10000;
                }
                mSimpleVideoPlayer.seekTo(post);
                break;
        }
    }
}
