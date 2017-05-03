package com.example.lederui.developmenttest.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.lederui.developmenttest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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


    @OnClick({R.id.img_exit, R.id.btn_skip_web})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_exit:
                mToWeb.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                mImgExit.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_skip_web:
                mLlFail.setVisibility(View.INVISIBLE);
                String url = mEtUrl.getText().toString();
                mToWeb.loadUrl("http://" + url);

                mToWeb.getSettings().setSupportZoom(true);
                mToWeb.getSettings().setBuiltInZoomControls(true);

                mToWeb.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
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
        }
    }
}
