package com.hp.htmleditor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BrowserFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener{
    private View view;
    private AppCompatActivity activity;
    private WebView loadWebView;
    private EditText loadUrlContent;
    private ProgressBar loadProgress;
    private String validHtmlUrl;
    private ImageButton toHtml;
    private RelativeLayout emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browser, container, false);
        initView();
        return view;
    }

    private void initView(){
        //获取父activity
        activity = (AppCompatActivity) getActivity();

        //获取控件
        ImageButton loadUrlSearch = view.findViewById(R.id.loadUrlSearch);

        toHtml = view.findViewById(R.id.toHtml);
        loadWebView = view.findViewById(R.id.loadWebView);
        loadUrlContent = view.findViewById(R.id.loadUrlContent);
        loadProgress = view.findViewById(R.id.loadProgress);
        emptyView = view.findViewById(R.id.emptyView);

        //设置控件
        validHtmlUrl = null;
        toHtml.setEnabled(false);
        toHtml.setAlpha((float) 0.4);
        loadProgress.setVisibility(View.INVISIBLE);
        loadWebView.setVisibility(View.VISIBLE);
        loadWebView.clearCache(true);
        WebSettings webSettings = loadWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        loadWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadWebView.setVisibility(View.INVISIBLE);
                loadProgress.setVisibility(View.VISIBLE);
                validHtmlUrl = url;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadUrlContent.setText(view.getUrl());
                loadProgress.setVisibility(View.INVISIBLE);
                loadWebView.setVisibility(View.VISIBLE);
            }
        });
        loadUrlContent.setOnEditorActionListener(this);
        loadUrlSearch.setOnClickListener(this);
        toHtml.setOnClickListener(this);
    }

    //处理点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loadUrlSearch:
                startSearch(loadUrlContent.getText().toString());
                break;
            case R.id.toHtml:
                startToHtml();
            default:
                break;
        }
    }

    //处理软键盘事件
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH){
            startSearch(loadUrlContent.getText().toString());
            return true;
        }
        return false;
    }

    //根据url搜索页面
    private void startSearch(String loadUrl){
        if(loadUrl.equals("")){
            return;
        }
        emptyView.setVisibility(View.INVISIBLE);
        toHtml.setEnabled(true);
        toHtml.setAlpha((float) 1);
        if(loadUrl.toLowerCase().startsWith("http://")||loadUrl.toLowerCase().startsWith("https://")){
            loadWebView.loadUrl(loadUrl);
        } else {
            loadWebView.loadUrl("http://" + loadUrl);
        }
        ((InputMethodManager)loadUrlContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);

    }

    //跳转到LoaderActivity并传递url地址
    private void startToHtml(){
        if(validHtmlUrl != null){
            Intent intent = new Intent(activity, LoaderActivity.class);
            intent.putExtra("htmlUrl", validHtmlUrl);
            activity.startActivity(intent);
        }
    }
}
