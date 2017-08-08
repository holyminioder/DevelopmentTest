package com.example.lederui.developmenttest.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lederui.developmenttest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_FIRST_USER;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * Created by holyminier on 2017/4/21.
 */

/**
 * 网络通信Fragment
 */
public class NetworkFragment extends Fragment {

    @BindView(R.id.toWeb)
    WebView mToWeb;
    @BindView(R.id.ll_progress)
    LinearLayout mLlProgress;
    @BindView(R.id.ll_fail)
    LinearLayout mLlFail;
    @BindView(R.id.et_url)
    EditText mEtUrl;
    @BindView(R.id.ll_net)
    LinearLayout mLlNet;
    Unbinder unbinder;
    @BindView(R.id.img_exit)
    ImageButton mImgExit;
    @BindView(R.id.btn_skip_web)
    Button mBtnSkipWeb;
    @BindView(R.id.btn_bluetooth)
    Button mBtnBlueTooth;

    private static final int BLUE_OK_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mToWeb.destroy();
    }



    @OnClick({R.id.img_exit, R.id.btn_skip_web, R.id.btn_bluetooth})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_exit:
                mToWeb.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                mImgExit.setBackgroundResource(R.drawable.lucency);
                break;
            case R.id.btn_skip_web:
                mLlFail.setVisibility(View.INVISIBLE);
                String url = mEtUrl.getText().toString();
                mToWeb.loadUrl("https://" + url);

                mToWeb.getSettings().setSupportZoom(true);
                mToWeb.getSettings().setBuiltInZoomControls(true);
                mToWeb.getSettings().setDomStorageEnabled(true);

                mToWeb.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        handler.proceed();
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        mImgExit.setBackgroundResource(R.drawable.exit_webview);
                        return true;
                    }
                });

                mToWeb.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        if (newProgress == 100) {
                            mLlProgress.setVisibility(View.INVISIBLE);
                            mImgExit.setVisibility(View.VISIBLE);
                        } else {
                            ConnectivityManager connectivity = (ConnectivityManager) getContext().getApplicationContext()
                                    .getSystemService(Context.CONNECTIVITY_SERVICE);
                            if (connectivity != null) {
                                NetworkInfo info = connectivity.getActiveNetworkInfo();
                                if (info != null && info.isConnected()) {
                                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                                        mLlProgress.setVisibility(View.VISIBLE);
                                        mImgExit.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    mLlFail.setVisibility(View.VISIBLE);
                                    mImgExit.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                });
                break;
            case R.id.btn_bluetooth:
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(!mBluetoothAdapter.isEnabled()){
                    Toast.makeText(getContext(),"请开启蓝牙功能",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent,BLUE_OK_CODE);
                }else {
                    showFileChooser();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(intent);

                }
                break;
            case BLUE_OK_CODE:
                Log.d("test", "blue code =" + BLUE_OK_CODE);
                if(requestCode == RESULT_FIRST_USER) {
                    Toast.makeText(getContext(), "已开启蓝牙功能,请选择文件", Toast.LENGTH_SHORT).show();
                    showFileChooser();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static final int FILE_SELECT_CODE = 0;
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult( Intent.createChooser(intent, "请选择文件进行传输"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }
}
