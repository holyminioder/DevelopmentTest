package com.example.lederui.developmenttest.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
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
import android.widget.Toast;

import com.example.lederui.developmenttest.R;
import com.example.lederui.developmenttest.activity.VideoViewActivity;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 主屏测试Fragment
 */
public class MainScreenTestFragment extends Fragment {

    @BindView(R.id.et_path)
    EditText mEtPath;
    @BindView(R.id.btn_browse)
    Button mBtnBrowse;
    @BindView(R.id.btn_play)
    Button mBtnPlay;
    @BindView(R.id.et_frame_rate)
    EditText mEtFrameRate;
    @BindView(R.id.et_performance)
    Button mEtPerformance;
    Unbinder unbinder;

    private PackageManager mPackageManager;
    private List<ResolveInfo> mAllApps;
    private Context context = getContext();
    private static boolean isOk = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_screen_test, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_browse, R.id.btn_play, R.id.et_performance})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_browse:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
                break;
            case R.id.btn_play:
                String path = mEtPath.getText().toString();
                VideoViewActivity.open(getContext(), path);
                break;
            case R.id.et_performance:
                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                mPackageManager = getActivity().getPackageManager();
                mAllApps = mPackageManager.queryIntentActivities(mainIntent,0);
                Collections.sort(mAllApps, new ResolveInfo.DisplayNameComparator(mPackageManager));
                for (ResolveInfo res : mAllApps){
                    String pkg = res.activityInfo.packageName;
                    String cls = res.activityInfo.name;

                    if (pkg.contains("com.aatt.fpsm")){
                        isOk = true;
                        ComponentName component = new ComponentName(pkg, cls);
                        Intent jumpIntent = new Intent();
                        jumpIntent.setComponent(component);
                        jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(jumpIntent);
                    }
                }
                if (isOk){
                    Toast.makeText(getActivity(),"跳转成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(),"您未能成功打开FPS Meter,请下载或手动进入FPS Meter进行业务操作",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    String path;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())){
                path = uri.getPath();
                mEtPath.setText(path);
                Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                path = getPath(getContext(),uri);
                mEtPath.setText(path);
                Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
            }else {
                path = getRealPathFromURI(uri);
                mEtPath.setText(path);
                Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri){
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    public String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)){
            if (isExternalStorageDocument(uri)){
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)){
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)){
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contenUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contenUri, null, null);
            }
            else if (isMediaDocument(uri)){
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)){
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }else if ("video".equals(type)){
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }else if ("audio".equals(type)){
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())){
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs){
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()){
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null){
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
}
