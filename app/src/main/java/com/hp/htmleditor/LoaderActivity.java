package com.hp.htmleditor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.factory.ServiceFactory;
import com.hp.vo.HtmlFile;

import java.util.Date;
import java.util.regex.Pattern;

public class LoaderActivity extends AppCompatActivity implements View.OnClickListener{
    public TextView displayLoadHtml;
    public ProgressBar loadProgress;
    public ImageButton loadHtml;
    private WebView webView;
    private LoadHtmlHandler loadHtmlHandler;
    private View loaderHtmlView;
    private Dialog loaderHtmlDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        initView();
        displayHtmlContent();
    }

    private void initView(){
        //设置系统状态栏UI
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //获取控件
        ImageButton back = findViewById(R.id.back);

        loadHtml = findViewById(R.id.loadHtml);
        webView = new WebView(getApplicationContext());
        loadHtmlHandler = new LoadHtmlHandler(LoaderActivity.this);
        displayLoadHtml = findViewById(R.id.displayLoadHtml);
        loadProgress = findViewById(R.id.loadProgress);
        loaderHtmlDialog = new Dialog(LoaderActivity.this);
        loaderHtmlView = getLayoutInflater().inflate(R.layout.dialog_loader_load, null);


        //设置控件
        webView.clearCache(true);
        loadProgress.setVisibility(View.INVISIBLE);
        back.setOnClickListener(this);
        loadHtml.setEnabled(false);
        loadHtml.setAlpha((float)0.4);
        loadHtml.setOnClickListener(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new InJsGetHtml(),"get_html_obj");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:window.get_html_obj.getHtml(document.getElementsByTagName('html')[0].innerHTML)");
            }
        });

        loaderHtmlDialog.setContentView(loaderHtmlView);
        loaderHtmlDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        android.view.WindowManager.LayoutParams p = loaderHtmlDialog.getWindow().getAttributes();
        p.width = (int)((getResources().getDisplayMetrics()).widthPixels * 0.8);
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        loaderHtmlDialog.setCanceledOnTouchOutside(true);
        loaderHtmlDialog.getWindow().setAttributes(p);
        loaderHtmlDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //重置
                TextView htmlFileNameTip =loaderHtmlView.findViewById(R.id.loaderHtmlFileNameTip);
                ((TextView)(loaderHtmlView.findViewById(R.id.loaderHtmlFileName))).setText("");
                ViewGroup.LayoutParams lp = htmlFileNameTip.getLayoutParams();
                lp.height = 0;
                htmlFileNameTip.setLayoutParams(lp);
            }
        });
        ((TextView)(loaderHtmlView.findViewById(R.id.loaderHtmlFileTitle))).setText("下  载");
        loaderHtmlView.findViewById(R.id.loaderHtmlFileConfirm).setOnClickListener(this);
        loaderHtmlView.findViewById(R.id.loaderHtmlFileCancel).setOnClickListener(this);
    }

    //处理点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                this.finish();
                break;
            case R.id.loadHtml:
                loaderHtmlDialog.show();
                break;
            //下载对话框点击事件
            case R.id.loaderHtmlFileConfirm:
                startLoad();
                break;
            case R.id.loaderHtmlFileCancel:
                loaderHtmlDialog.dismiss();
                break;
        }
    }

    //下载html文件
    private void startLoad(){
        EditText htmlFileName = loaderHtmlView.findViewById(R.id.loaderHtmlFileName);
        TextView htmlFileNameTip =loaderHtmlView.findViewById(R.id.loaderHtmlFileNameTip);
        ViewGroup.LayoutParams lp = htmlFileNameTip.getLayoutParams();

        lp.height = 50;
        switch(HtmlListFragment.checkFileNameFormat(htmlFileName.getText().toString(), LoaderActivity.this)){
            case 0:
                htmlFileNameTip.setLayoutParams(lp);
                htmlFileNameTip.setText("* 文件名不能为空");
                break;
            case 1:
                htmlFileNameTip.setLayoutParams(lp);
                htmlFileNameTip.setText("* 文件名不能包含特殊字符或格式有误");
                break;
            case 2:
                htmlFileNameTip.setLayoutParams(lp);
                htmlFileNameTip.setText("* 文件已存在");
                break;
            case 3:
                lp.height = 0;
                htmlFileNameTip.setLayoutParams(lp);
                loaderHtmlDialog.dismiss();
                HtmlFile htmlFile = new HtmlFile();
                String name = htmlFileName.getText().toString() + ".html";
                htmlFile.setHtmlname(name);
                htmlFile.setCreatedate(new Date());
                htmlFile.setModifieddate(new Date());
                ServiceFactory.getHtmlFileServiceInstance(LoaderActivity.this).insert(htmlFile);
                if(ServiceFactory.getHtmlFileServiceInstance(LoaderActivity.this).updateContent(htmlFile.getHtmlname(), displayLoadHtml.getText().toString()))
                    Toast.makeText(LoaderActivity.this, "下载成功", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(LoaderActivity.this, "下载失败", Toast.LENGTH_LONG).show();
                loaderHtmlDialog.dismiss();
        }
    }

    //加载由其它页面传递来的URL资源
    private void displayHtmlContent(){
        String url = getIntent().getStringExtra("htmlUrl");
        loadProgress.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    //与js交互的特定类
    final class InJsGetHtml{
        @JavascriptInterface
        public void getHtml(String html){
            Message msg = new Message();
            msg.what = 0;
            msg.obj = "<html>\n" + html + "\n</html>";
            loadHtmlHandler.sendMessage(msg);
        }
    }
}
