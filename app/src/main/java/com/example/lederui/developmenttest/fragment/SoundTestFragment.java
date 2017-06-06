package com.example.lederui.developmenttest.fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.lederui.developmenttest.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 声音测试Fragment
 */
public class SoundTestFragment extends Fragment {

    @BindView(R.id.sod_path)
    EditText mSodPath;
    @BindView(R.id.sod_browse)
    Button mSodBrowse;
    @BindView(R.id.sod_play)
    Button mSodPlay;
    @BindView(R.id.sod_stop)
    Button mSodStop;
    @BindView(R.id.sod_pause)
    Button mSodPause;
    @BindView(R.id.sod_goon)
    Button mSodGoon;
    Unbinder unbinder;
    @BindView(R.id.btn_sub)
    Button mBtnSub;
    @BindView(R.id.seek_bar)
    SeekBar mSeekBar;
    @BindView(R.id.btn_add)
    Button mBtnAdd;

    private MediaPlayer mPlayer;
    private String filePath;
    private boolean pause = false;
    private AudioManager mAudioManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound_test, container, false);
        unbinder = ButterKnife.bind(this, view);
        mPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);//初始化音量管理
        mSeekBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));//设置滑动条最大值
        mSeekBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));//设置滑动条当前数值
        mSeekBar.setOnSeekBarChangeListener(mListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //滑动条滑动事件
    private SeekBar.OnSeekBarChangeListener mListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);//修改音量
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @OnClick({R.id.sod_browse, R.id.sod_play, R.id.sod_stop, R.id.sod_pause, R.id.sod_goon, R.id.btn_sub, R.id.btn_add})
    public void onViewClicked(View view) {
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int num = 1;
        switch (view.getId()) {
            case R.id.sod_browse:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case R.id.sod_play:
                String musicPath = mSodPath.getText().toString();
                File audio = new File(musicPath);
                if (audio.exists()) {
                    filePath = audio.getAbsolutePath();
                    play(0);
                    Toast.makeText(getContext(), "开始播放", Toast.LENGTH_LONG).show();
                } else {
                    filePath = null;
                    Toast.makeText(getContext(), "播放失败", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.sod_stop:
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                    Toast.makeText(getContext(), "停止播放", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.sod_pause:
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    pause = true;
                    Toast.makeText(getContext(), "暂停播放", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.sod_goon:
                if (pause) {
                    mPlayer.start();
                    pause = false;
                    Toast.makeText(getContext(), "继续播放", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_sub://减小音量
                if (volume - num >= 0){
                    int bad = volume - num;
                    mSeekBar.setProgress(bad);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, bad, AudioManager.FLAG_PLAY_SOUND);
                }else {
                    volume = 0;
                    mSeekBar.setProgress(volume);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
                }
                break;
            case R.id.btn_add://加大音量
                if (volume + num <= maxVolume){
                    int add = volume + num;
                    mSeekBar.setProgress(add);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, add, AudioManager.FLAG_PLAY_SOUND);
                }else {
                    volume = maxVolume;
                    mSeekBar.setProgress(volume);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
                }
                break;
        }
    }

    //播放音乐
    private void play(int playPosition) {
        try {
            mPlayer.reset();// 把各项参数恢复到初始状态
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();// 进行缓冲
            mPlayer.setOnPreparedListener(new MyPreparedListener(playPosition));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final class MyPreparedListener implements
            MediaPlayer.OnPreparedListener {
        private int playPosition;

        public MyPreparedListener(int playPosition) {
            this.playPosition = playPosition;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mPlayer.start();// 开始播放
            if (playPosition > 0) {
                mPlayer.seekTo(playPosition);
            }
        }

    }

    String path;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                path = uri.getPath();
                mSodPath.setText(path);
                Toast.makeText(getContext(), path + "1111", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                path = getPath(getContext(), uri);
                mSodPath.setText(path);
                Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
            } else {
                path = getRealPathFromURI(uri);
                mSodPath.setText(path);
                Toast.makeText(getContext(), path + "222222", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    public String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contenUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contenUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
        }
    }

    @Override
    public void onDestroy() {
        mPlayer.release();
        mPlayer = null;
        super.onDestroy();
    }
}
