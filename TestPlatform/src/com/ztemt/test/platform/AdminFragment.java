package com.ztemt.test.platform;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

public class AdminFragment extends WebViewFragment {

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    };

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getWebView().getSettings().setJavaScriptEnabled(true);
        getWebView().getSettings().setBuiltInZoomControls(true);
        getWebView().setWebViewClient(mWebViewClient);
        getWebView().loadUrl(getString(R.string.admin_url));
        return view;
    }
}
