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
import android.widget.VideoView;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.utils.GetVideoUri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class DifferentDisplay extends Presentation {
    public DifferentDisplay(Context outerContext, Display display) {
        super(outerContext, display);
    }

    private List fileNames = new ArrayList();
    private VideoView mSimpleVideoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_asyn_display);
        mSimpleVideoPlayer = (VideoView) findViewById(R.id.asyn_simpleVideoPlayer);
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
                Log.e("videoUri", videoUri+"");
                mSimpleVideoPlayer.setVideoURI(Uri.parse(videoUri + ""));
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
    }
}
