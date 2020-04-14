package com.hp.htmleditor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.factory.ServiceFactory;
import com.hp.util.NearestCharacterSearchUtil;
import com.hp.vo.HtmlFile;
import com.hp.vo.LabelInfo;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{
    private ImageView editorFunctionSet;
    private EditText htmlEditor;
    private HtmlFile htmlFile;//编辑器正在处理的html文件
    private PopupWindow editorFunctionSetDialog;
    private PopupWindow editorCodeTipsDialog;
    private PopupWindow editorCodeAttrsTipsDialog;
    private PopupWindow editorFunctionSetSearchWordsDialog;
    private Dialog editorFunctionSetBrowserViewDialog;
    private WebView runWebView;
    private Integer htmlFileModifiedState;//保存当前html文件的修改状态，0表示未修改，1表示被修改
    private TextView editorHtmlFileNameTitle;
    private Editable editable;
    private EditorEditOperationTextWatcher editorEditOperationTextWatcher;//编辑器操作监听器，用于撤销和恢复
    private EditorCodeTipsTextWatcher editorCodeTipsTextWatcher;//编辑器代码提示监听器
    private List<Map<String, String>> editorCodeTipsList;//代码提示列表
    private List<Map<String, String>> editorCodeAttrsTipsList;//标签属性提示列表
    private View renameView;
    private Dialog renameDialog;
    private EditText searchWordsContent;
    private CheckBox searchWordsMode;
    private TextView searchWordsTip;
    private List<Map<String, Integer>> searchWordsList;
    private Integer currentSearchWordsCursor;
    private String lastSearchWord;
    private Matcher matcher;
    private TextView editorFunctionSetButtonDefineSelect;
    private LinearLayout buttonDefineBar;
    private LinearLayout editorFunctionQuick;
    private LinearLayout editorFunctionQuick2;
    private LinearLayout editorFunctionQuick3;
    private Integer editorFunctionQuickSign;
    private EditorTagAddDialog editorTagAddDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        initIntentData();
        initView();
    }

    //处理intent传递的数据
    private void initIntentData(){
        Intent intent = getIntent();
        externalHtmlManager(intent);
        htmlEditor = findViewById(R.id.htmlEditor);
        htmlFile = ServiceFactory.getHtmlFileServiceInstance(this).getByName(intent.getStringExtra("htmlFileOriginName"));

        if(htmlFile == null){
            Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
            this.finish();
        }
        if(intent.getIntExtra("htmlFileOriginType", 0) == 0){
            //文件为新文件
            switch (intent.getIntExtra("initFileTemplate", 0)){
                case 0:
                    htmlEditor.setText(getResources().getText(R.string.html_file_template_default));
                    htmlEditor.setSelection(72, 77);
                    break;
                case 1:
                    htmlEditor.setText("");
                    break;
                case 2:
                    String initFileTemplateName = intent.getStringExtra("initFileTemplateName");
                    if(initFileTemplateName.equals("<空>")){
                        htmlEditor.setText("");
                        break;
                    }
                    htmlEditor.setText(ServiceFactory.getHtmlTemplateServiceInstance(EditorActivity.this).getContentByName(initFileTemplateName));
                    if(htmlEditor.getText().toString().length() > 1)
                        htmlEditor.setSelection(1);
                    break;
            }
            ServiceFactory.getHtmlFileServiceInstance(EditorActivity.this).updateContent(htmlFile.getHtmlname(), htmlEditor.getText().toString());
        } else {
            //文件为已存在文件
            htmlEditor.setText(ServiceFactory.getHtmlFileServiceInstance(EditorActivity.this).getContentByName(htmlFile.getHtmlname()));
            if(htmlEditor.getText().toString().length() > 1)
                htmlEditor.setSelection(1);
        }
    }

    //处理来自其他应用程序的html文件
    private void externalHtmlManager(Intent intent){
        if(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)){
            Uri uri = intent.getData();
            if(uri == null) return;
            Cursor cursor = getContentResolver().query(uri, null, "mime_type=\"text/html\"", null, null);
            if(cursor == null) return;
            if(cursor.moveToFirst()){
                intent.putExtra("htmlFileOriginType", 1);
                HtmlFile htmlFile = new HtmlFile();
                String htmlFileName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
                StringBuilder sb = new StringBuilder(htmlFileName.substring(0, htmlFileName.lastIndexOf(".html")));
                for(int i= 1; ServiceFactory.getHtmlFileServiceInstance(EditorActivity.this).getByName(sb.toString() + ".html") != null; i++){
                    if(i == 1){
                        sb.append("1");
                        continue;
                    }
                    sb.replace(sb.length() - 1, sb.length(), i + "");
                }
                htmlFileName =  sb.append(".html").toString();
                htmlFile.setHtmlname(htmlFileName);
                htmlFile.setCreatedate(new Date());
                htmlFile.setModifieddate(new Date());
                intent.putExtra("htmlFileOriginName", htmlFileName);
                ServiceFactory.getHtmlFileServiceInstance(EditorActivity.this).insert(htmlFile);
                //读取内容
                BufferedInputStream br = null;
                StringBuilder content = new StringBuilder("");
                try{
                    int flag;
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    if (inputStream == null) return;
                    byte[] buffer = new byte[1024];
                    br = new BufferedInputStream(inputStream);
                    while ((flag = br.read(buffer)) != -1){
                        content.append(new String(buffer, 0, flag));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try{
                        if(br != null) br.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                ServiceFactory.getHtmlFileServiceInstance(EditorActivity.this).updateContent(htmlFileName,content.toString());
            } else return;
            cursor.close();
        }
    }

    private void initView(){
        //设置系统状态栏UI
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //获取控件
        ImageButton back = findViewById(R.id.back);
        LinearLayout editorLinearLayoutGroup = findViewById(R.id.editorLinearLayoutGroup);
        View editorFunctionSetDialogView = getLayoutInflater().inflate(R.layout.dialog_editor_funciton_set, editorLinearLayoutGroup, false);
        View editorCodeTipsDialogView = getLayoutInflater().inflate(R.layout.dialog_editor_code_tips_list, editorLinearLayoutGroup, false);
        View editorCodeAttrsTipsDialogView = getLayoutInflater().inflate(R.layout.dialog_editor_code_tips_list, editorLinearLayoutGroup, false);
        View editorFunctionSetBrowserView =getLayoutInflater().inflate(R.layout.dialog_editor_function_set_browser_view, editorLinearLayoutGroup, false);
        View editorFunctionSetSearchWordsView = getLayoutInflater().inflate(R.layout.dialog_editor_function_set_search_words, editorLinearLayoutGroup, false);
        ImageView closeWebView = editorFunctionSetBrowserView.findViewById(R.id.closeWebView);

        editorCodeTipsDialog = new PopupWindow(editorCodeTipsDialogView, 350, 370, false);
        editorCodeAttrsTipsDialog = new PopupWindow(editorCodeAttrsTipsDialogView, 350, 370, false);
        editorCodeTipsList = new ArrayList<>();
        editorCodeAttrsTipsList = new ArrayList<>();
        EditorCodeTipsAdapter editorCodeTipsAdapter = new EditorCodeTipsAdapter(this, editorCodeTipsList, R.layout.dialog_editor_code_tips,0);
        EditorCodeTipsAdapter editorCodeAttrsTipsAdapter = new EditorCodeTipsAdapter(this, editorCodeAttrsTipsList, R.layout.dialog_editor_code_tips,1);

        editorHtmlFileNameTitle = findViewById(R.id.editorHtmlFileNameTitle);
        editorFunctionSet = findViewById(R.id.editorFunctionSet);
        editable = htmlEditor.getText();
        editorTagAddDialog = new EditorTagAddDialog(EditorActivity.this, htmlEditor);
        editorFunctionSetDialog = new PopupWindow(editorFunctionSetDialogView, 500, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        editorFunctionSetSearchWordsDialog = new PopupWindow(editorFunctionSetSearchWordsView, 500, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        editorFunctionSetBrowserViewDialog = new Dialog(EditorActivity.this);
        runWebView = editorFunctionSetBrowserView.findViewById(R.id.runWebView);
        editorFunctionSetButtonDefineSelect = editorFunctionSetDialogView.findViewById(R.id.editorFunctionSetButtonDefineSelect);
        renameView = getLayoutInflater().inflate(R.layout.dialog_loader_load, null);
        renameDialog = new Dialog(EditorActivity.this);
        searchWordsContent = editorFunctionSetSearchWordsView.findViewById(R.id.searchWordsContent);
        searchWordsMode = editorFunctionSetSearchWordsView.findViewById(R.id.searchWordsMode);
        searchWordsTip = editorFunctionSetSearchWordsView.findViewById(R.id.searchWordsTip);
        searchWordsList = new ArrayList<>();
        buttonDefineBar = findViewById(R.id.buttonDefineBar);
        editorFunctionQuick = findViewById(R.id.editorFunctionQuick);
        editorFunctionQuick2 = findViewById(R.id.editorFunctionQuick2);
        editorFunctionQuick3 = findViewById(R.id.editorFunctionQuick3);

        editorEditOperationTextWatcher = new EditorEditOperationTextWatcher(htmlEditor);
        editorCodeTipsTextWatcher = new EditorCodeTipsTextWatcher(EditorActivity.this, editorFunctionSet, editorCodeTipsDialog,editorCodeAttrsTipsDialog, editorCodeTipsList,editorCodeAttrsTipsList, editorCodeTipsAdapter, editorCodeAttrsTipsAdapter);

        //初始化数据
        htmlFileModifiedState = 0;
        htmlFile.setModifieddate(new Date());
        ServiceFactory.getHtmlFileServiceInstance(EditorActivity.this).updateMetaData(htmlFile);

        //设置控件
        String htmlFileName = htmlFile.getHtmlname();
        back.setOnClickListener(this);
        editorHtmlFileNameTitle.setText(htmlFileName.substring(0, htmlFileName.lastIndexOf(".html")));

        findViewById(R.id.editorTagAdd).setOnClickListener(this);

        editorFunctionQuickDisplayManager(1);
        findViewById(R.id.editorFunctionQuickGroup).setTranslationZ(10);
        findViewById(R.id.editorFunctionQuickTab).setOnClickListener(new EditorTabCharacterListener(htmlEditor));
        findViewById(R.id.editorFunctionQuickLt).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuickGt).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuickSlash).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuickUndo).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuickRedo).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuickDelLine).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick2Key1).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick2Key2).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick2Key3).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick2Key4).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick2Key5).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick2Key6).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick2Undo).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick2Redo).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick2DelLine).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick3Key1).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick3Key2).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick3Key3).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick3Key4).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick3Key5).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick3Key6).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick3Undo).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick3Redo).setOnClickListener(this);
        findViewById(R.id.editorFunctionQuick3DelLine).setOnClickListener(this);

        editorFunctionSetDialogView.setTranslationZ(10);
        editorFunctionSetDialog.setOutsideTouchable(true);
        editorFunctionSet.setOnClickListener(this);
        editorFunctionSetDialogView.findViewById(R.id.editorFunctionSetBrowserView).setOnClickListener(this);
        editorFunctionSetDialogView.findViewById(R.id.editorFunctionSetSwitchToolBar).setOnClickListener(this);
        editorFunctionSetDialogView.findViewById(R.id.editorFunctionSetButtonDefine).setOnClickListener(this);
        editorFunctionSetDialogView.findViewById(R.id.editorFunctionSetRename).setOnClickListener(this);
        editorFunctionSetDialogView.findViewById(R.id.editorFunctionSetSearchWords).setOnClickListener(this);
        editorFunctionSetDialogView.findViewById(R.id.editorFunctionSetSaveFile).setOnClickListener(this);

        editorCodeTipsDialogView.setTranslationZ(10);
        editorCodeAttrsTipsDialogView.setTranslationZ(10);
        editorCodeTipsDialog.setOutsideTouchable(true);
        editorCodeAttrsTipsDialog.setOutsideTouchable(true);
        ((ListView)editorCodeTipsDialogView.findViewById(R.id.editorCodeTips)).setAdapter(editorCodeTipsAdapter);
        ((ListView)editorCodeTipsDialogView.findViewById(R.id.editorCodeTips)).setOnItemClickListener(this);
        ((ListView)editorCodeAttrsTipsDialogView.findViewById(R.id.editorCodeTips)).setAdapter(editorCodeAttrsTipsAdapter);
        ((ListView)editorCodeAttrsTipsDialogView.findViewById(R.id.editorCodeTips)).setOnItemClickListener(this);

        editorFunctionSetBrowserViewDialog.setContentView(editorFunctionSetBrowserView);
        editorFunctionSetBrowserViewDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        android.view.WindowManager.LayoutParams p = editorFunctionSetBrowserViewDialog.getWindow().getAttributes();
        p.width = (int)((getResources().getDisplayMetrics()).widthPixels * 0.9);
        p.height = (int)((getResources().getDisplayMetrics()).heightPixels * 0.75);
        editorFunctionSetBrowserViewDialog.setCanceledOnTouchOutside(true);
        editorFunctionSetBrowserViewDialog.getWindow().setAttributes(p);
        closeWebView.setOnClickListener(this);
        runWebView.clearCache(true);
        WebSettings webSettings = runWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        runWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(runWebView.getContext());
                localBuilder.setMessage(message).setPositiveButton("确定",null);
                localBuilder.setCancelable(false);
                localBuilder.create().show();
                result.confirm();
                return true;
            }
        });
        runWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(!url.equals("")){
                    view.loadUrl(url);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        editorFunctionSetButtonDefineSelect.setText("");
        buttonDefineBar.setVisibility(View.INVISIBLE);
        buttonDefineBar.setTranslationZ(10);
        findViewById(R.id.buttonDefineF1).setOnClickListener(this);
        findViewById(R.id.buttonDefineF2).setOnClickListener(this);
        findViewById(R.id.buttonDefineF3).setOnClickListener(this);
        findViewById(R.id.buttonDefineF4).setOnClickListener(this);
        findViewById(R.id.buttonDefineF5).setOnClickListener(this);

        renameDialog.setContentView(renameView);
        renameDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        android.view.WindowManager.LayoutParams p1 = renameDialog.getWindow().getAttributes();
        p1.width = (int)((getResources().getDisplayMetrics()).widthPixels * 0.8);
        p1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        renameDialog.setCanceledOnTouchOutside(true);
        renameDialog.getWindow().setAttributes(p1);
        renameDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //重置
                TextView htmlFileNameTip = renameView.findViewById(R.id.loaderHtmlFileNameTip);
                ((TextView)(renameView.findViewById(R.id.loaderHtmlFileName))).setText("");
                ViewGroup.LayoutParams lp = htmlFileNameTip.getLayoutParams();
                lp.height = 0;
                htmlFileNameTip.setLayoutParams(lp);
            }
        });
        ((TextView)(renameView.findViewById(R.id.loaderHtmlFileTitle))).setText("重命名");
        renameView.findViewById(R.id.loaderHtmlFileConfirm).setOnClickListener(this);
        renameView.findViewById(R.id.loaderHtmlFileCancel).setOnClickListener(this);

        editorFunctionSetSearchWordsView.setTranslationZ(10);
        editorFunctionSetSearchWordsDialog.setOutsideTouchable(false);
        editorFunctionSetSearchWordsDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                editorFunctionQuickDisplayManager(editorFunctionQuickSign);
                searchWordsContent.setText("");
                searchWordsTip.setText("");
            }
        });
        searchWordsMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) searchWordsContent.setHint("正则表达式");
                else searchWordsContent.setHint("关键字");
                lastSearchWord = "";
            }
        });
        editorFunctionSetSearchWordsView.findViewById(R.id.searchWordsPre).setOnClickListener(this);
        editorFunctionSetSearchWordsView.findViewById(R.id.searchWordsNext).setOnClickListener(this);

        htmlEditor.setHorizontallyScrolling(true);
        htmlEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(htmlFileModifiedState == 0){
                    String title = "*" + editorHtmlFileNameTitle.getText();
                    htmlFileModifiedState = 1;
                    editorHtmlFileNameTitle.setText(title);
                }
            }
        });//监听文本是否改变
        htmlEditor.addTextChangedListener(editorEditOperationTextWatcher);//设置撤销恢复监听
        htmlEditor.addTextChangedListener(editorCodeTipsTextWatcher);//设置代码提示监听
        htmlEditor.setOnKeyListener(new EditorEnterCharacterListener(htmlEditor));//设置回车键监听，控制缩进与对齐
    }

    //处理点击事件
    @Override
    public void onClick(View v) {
        String keyword;
        switch (v.getId()){
            //主界面点击事件
            case R.id.back:
                showSaveDialog();
                break;
            case R.id.editorTagAdd:
                editorTagAddDialog.show();
                break;
            case R.id.editorFunctionSet:
                editorFunctionSetDialog.showAsDropDown(editorFunctionSet, -100, 0);
                break;
            //编辑器功能集点击事件
            case R.id.editorFunctionSetBrowserView:
                runWebView.loadDataWithBaseURL(null, editable.toString(), "text/html", "utf-8", null);
                editorFunctionSetDialog.dismiss();
                editorFunctionSetBrowserViewDialog.show();
                break;
            case R.id.closeWebView:
                editorFunctionSetBrowserViewDialog.dismiss();
                break;
            case R.id.editorFunctionSetSwitchToolBar:
                editorFunctionQuickDisplayManager(editorFunctionQuickSign % 3 + 1);
                break;
            case R.id.editorFunctionSetButtonDefine:
                if(buttonDefineBar.getVisibility() == View.VISIBLE){
                    buttonDefineBar.setVisibility(View.INVISIBLE);
                    editorFunctionSetButtonDefineSelect.setText("");
                }
                else {
                    buttonDefineBar.setVisibility(View.VISIBLE);
                    editorFunctionSetButtonDefineSelect.setText("√");
                }
                break;
            case R.id.buttonDefineF1:
                insertFunctionCode(getSharedPreferences("button_define_contents", MODE_PRIVATE).getString("F1", ""));
                break;
            case R.id.buttonDefineF2:
                insertFunctionCode(getSharedPreferences("button_define_contents", MODE_PRIVATE).getString("F2", ""));
                break;
            case R.id.buttonDefineF3:
                insertFunctionCode(getSharedPreferences("button_define_contents", MODE_PRIVATE).getString("F3", ""));
                break;
            case R.id.buttonDefineF4:
                insertFunctionCode(getSharedPreferences("button_define_contents", MODE_PRIVATE).getString("F4", ""));
                break;
            case R.id.buttonDefineF5:
                insertFunctionCode(getSharedPreferences("button_define_contents", MODE_PRIVATE).getString("F5", ""));
                break;
            case R.id.editorFunctionSetRename:
                editorFunctionSetDialog.dismiss();
                EditText htmlFileName = renameView.findViewById(R.id.loaderHtmlFileName);
                htmlFileName.setText(htmlFile.getHtmlname().substring(0, htmlFile.getHtmlname().lastIndexOf(".html")));
                renameDialog.show();
                break;
            case R.id.loaderHtmlFileConfirm:
                startRename();
                break;
            case R.id.loaderHtmlFileCancel:
                renameDialog.dismiss();
                break;
            case R.id.editorFunctionSetSearchWords:
                editorFunctionSetDialog.dismiss();
                editorFunctionQuickDisplayManager(0);
                searchWordsList.clear();
                currentSearchWordsCursor = -1;
                lastSearchWord = "";
                editorFunctionSetSearchWordsDialog.showAsDropDown(editorFunctionSet);
                break;
            case R.id.searchWordsPre:
                searchWordsTip.setText("");
                keyword = searchWordsContent.getText().toString();
                if(keyword.equals("") || !keyword.equals(lastSearchWord)){
                    searchWordsList.clear();
                    currentSearchWordsCursor = -1;
                    lastSearchWord = "";
                    return;
                }
                if(currentSearchWordsCursor == 0)
                    searchWordsTip.setText("* 当前为第一个");
                if(currentSearchWordsCursor > 0 && currentSearchWordsCursor < searchWordsList.size()){
                    currentSearchWordsCursor --;
                    Map<String, Integer> currentSearchWord = searchWordsList.get(currentSearchWordsCursor);
                    htmlEditor.setSelection(currentSearchWord.get("start"), currentSearchWord.get("end"));
                }
                break;
            case R.id.searchWordsNext:
                searchWordsTip.setText("");
                keyword = searchWordsContent.getText().toString();
                if(keyword.equals("")){
                    searchWordsList.clear();
                    currentSearchWordsCursor = -1;
                    lastSearchWord = "";
                    return;
                }
                if(!keyword.equals(lastSearchWord)){
                    searchWordsList.clear();
                    currentSearchWordsCursor = -1;
                    lastSearchWord = keyword;
                    if(searchWordsMode.isChecked()){
                        matcher = Pattern.compile(keyword).matcher(editable.toString());
                    } else {
                        matcher = Pattern.compile(Pattern.quote(keyword)).matcher(editable.toString());
                    }
                }
                if(currentSearchWordsCursor + 1 == searchWordsList.size()){
                    if (matcher.find()){
                        currentSearchWordsCursor ++;
                        Map<String, Integer> currentSearchWord = new HashMap<>();
                        currentSearchWord.put("start", matcher.start());
                        currentSearchWord.put("end", matcher.end());
                        searchWordsList.add(currentSearchWord);
                        htmlEditor.setSelection(matcher.start(), matcher.end());
                    } else if(currentSearchWordsCursor == -1){
                        searchWordsTip.setText("* 没有匹配项");
                    } else {
                        searchWordsTip.setText("* 当前为最后一个");
                    }
                } else {
                    currentSearchWordsCursor ++;
                    Map<String, Integer> currentSearchWord = searchWordsList.get(currentSearchWordsCursor);
                    htmlEditor.setSelection(currentSearchWord.get("start"), currentSearchWord.get("end"));
                }
                break;
            case R.id.editorFunctionSetSaveFile:
                ServiceFactory.getHtmlFileServiceInstance(EditorActivity.this).updateContent(htmlFile.getHtmlname(), htmlEditor.getText().toString());
                if(htmlFileModifiedState == 1){
                    String title = editorHtmlFileNameTitle.getText().toString().substring(1);
                    htmlFileModifiedState = 0;
                    editorHtmlFileNameTitle.setText(title);
                }
                editorFunctionSetDialog.dismiss();
                Toast.makeText(EditorActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                break;
            //编辑器快捷栏点击事件
            case R.id.editorFunctionQuickLt:
                editable.insert(htmlEditor.getSelectionStart(), "<");
                break;
            case R.id.editorFunctionQuickGt:
                editable.insert(htmlEditor.getSelectionStart(), ">");
                break;
            case R.id.editorFunctionQuickSlash:
                editable.insert(htmlEditor.getSelectionStart(), "/");
                break;
            case R.id.editorFunctionQuick2Key1:
                editable.insert(htmlEditor.getSelectionStart(), "#");
                break;
            case R.id.editorFunctionQuick2Key2:
                editable.insert(htmlEditor.getSelectionStart(), ">");
                break;
            case R.id.editorFunctionQuick2Key3:
                editable.insert(htmlEditor.getSelectionStart(), ",");
                break;
            case R.id.editorFunctionQuick2Key4:
                editable.insert(htmlEditor.getSelectionStart(), "{}");
                htmlEditor.setSelection(htmlEditor.getSelectionStart() - 1);
                break;
            case R.id.editorFunctionQuick2Key5:
                editable.insert(htmlEditor.getSelectionStart(), ":");
                break;
            case R.id.editorFunctionQuick2Key6:
                editable.insert(htmlEditor.getSelectionStart(), ";");
                break;
            case R.id.editorFunctionQuick3Key1:
                editable.insert(htmlEditor.getSelectionStart(), "function (){}");
                htmlEditor.setSelection(htmlEditor.getSelectionStart() - 1);
                break;
            case R.id.editorFunctionQuick3Key2:
                editable.insert(htmlEditor.getSelectionStart(), "{}");
                htmlEditor.setSelection(htmlEditor.getSelectionStart() - 1);
                break;
            case R.id.editorFunctionQuick3Key3:
                editable.insert(htmlEditor.getSelectionStart(), "()");
                htmlEditor.setSelection(htmlEditor.getSelectionStart() - 1);
                break;
            case R.id.editorFunctionQuick3Key4:
                editable.insert(htmlEditor.getSelectionStart(), "=");
                break;
            case R.id.editorFunctionQuick3Key5:
                editable.insert(htmlEditor.getSelectionStart(), ",");
                break;
            case R.id.editorFunctionQuick3Key6:
                editable.insert(htmlEditor.getSelectionStart(), ";");
                break;
            case R.id.editorFunctionQuickUndo:
            case R.id.editorFunctionQuick2Undo:
            case R.id.editorFunctionQuick3Undo:
                editorCodeTipsTextWatcher.setEnable(false);//屏蔽代码提示
                editorEditOperationTextWatcher.undo();
                editorCodeTipsTextWatcher.setEnable(true);
                break;
            case R.id.editorFunctionQuickRedo:
            case R.id.editorFunctionQuick2Redo:
            case R.id.editorFunctionQuick3Redo:
                editorCodeTipsTextWatcher.setEnable(false);//屏蔽代码提示
                editorEditOperationTextWatcher.redo();
                editorCodeTipsTextWatcher.setEnable(false);//屏蔽代码提示
                break;
            case R.id.editorFunctionQuickDelLine:
            case R.id.editorFunctionQuick2DelLine:
            case R.id.editorFunctionQuick3DelLine:
                editable.delete(NearestCharacterSearchUtil.getCurrentLineStart(editable, htmlEditor.getSelectionStart()), NearestCharacterSearchUtil.getCurrentLineEnd(editable, htmlEditor.getSelectionStart()));
                break;
        }
    }

    //处理代码提示框中的点击事件，提供代码补全
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(editorCodeTipsDialog.isShowing()){//标签列表点击事件
            Map<String, String> label = editorCodeTipsList.get(position);
            String labelName = label.get("labelName");
            StringBuilder keyLabel = new StringBuilder(labelName);
            LabelInfo labelInfo = ServiceFactory.getLabelInfoServiceInstance(EditorActivity.this).getByName(labelName);
            List<Map<String, String>> labelPrivateAttrsList = ServiceFactory.getLabelInfoServiceInstance(EditorActivity.this).getPrivateAttributeListByName(labelName);
            if(labelInfo == null) return;
            for(Map<String, String> labelPrivateAttr:labelPrivateAttrsList){
                if(labelPrivateAttr.get("labelAttrRecmd").equals("X"))
                    continue;
                keyLabel.append(" ").append(labelPrivateAttr.get("labelAttrName")).append("=\"\"");
            }
            if(labelInfo.getLabelType().equals("D")){
                keyLabel.append("></").append(labelName).append(">");//调整格式
            }else {
                keyLabel.append(" />");
            }
            int currentCursor = htmlEditor.getSelectionStart();
            int keyLabelStart = currentCursor;//替换起点位置
            int keyLabelEnd = currentCursor;//替换终点位置
            for (; keyLabelStart >= 0; keyLabelStart --)
                if(editable.charAt(keyLabelStart) == '<')
                    break;
            if((keyLabelEnd != editable.length()) && (editable.charAt(keyLabelEnd) == '>')) keyLabelEnd ++;
            editable.replace(keyLabelStart + 1, keyLabelEnd, keyLabel.toString());
        } else if(editorCodeAttrsTipsDialog.isShowing()){//属性列表点击事件
            Map<String, String> labelAttr = editorCodeAttrsTipsList.get(position);
            String labelAttrName = labelAttr.get("labelAttrName") + "=\"\"";
            int currentCursor = htmlEditor.getSelectionStart();
            int keyLabelStart = currentCursor - 1;//替换起点位置
            for (; keyLabelStart >= 0; keyLabelStart --)
                if(editable.charAt(keyLabelStart) == ' ' || editable.charAt(keyLabelStart) == '\t')
                    break;
            editable.replace(keyLabelStart + 1, currentCursor, labelAttrName);
            htmlEditor.setSelection(htmlEditor.getSelectionStart() - 1);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            showSaveDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    //插入快捷按钮生成的代码
    private void insertFunctionCode(String data){
        int currentCursor = htmlEditor.getSelectionStart();
        StringBuilder sb = new StringBuilder(data);
        //获取插入位置所在行的前导空格
        int lineStart = NearestCharacterSearchUtil.getCurrentLineStart(editable, currentCursor);
        lineStart = (lineStart == 0)?(lineStart):(lineStart + 1);//调整行起始坐标

        //回车对齐
        CharSequence keyLabel = editable.subSequence(lineStart, currentCursor);//获取当前行字符串
        Pattern pattern = Pattern.compile("(\\s+)?.*");
        Matcher matcher = pattern.matcher(keyLabel);
        String forwardsSpace = "";
        if(matcher.matches())
            forwardsSpace = (matcher.group(1) == null)?(""):(matcher.group(1));//前导空格，用于调整格式
        if(!forwardsSpace.equals(""))
            for(int i = 0; i < sb.length(); i ++){
                if(sb.charAt(i) == '\n'){
                    sb.insert(++ i, forwardsSpace);
                    i += forwardsSpace.length();
                }
            }
        editable.insert(currentCursor, sb.toString());
    }

    //进行重命名
    private void startRename(){
        EditText newHtmlFileName = renameView.findViewById(R.id.loaderHtmlFileName);
        TextView htmlFileNameTip = renameView.findViewById(R.id.loaderHtmlFileNameTip);
        ViewGroup.LayoutParams lp = htmlFileNameTip.getLayoutParams();

        lp.height = 50;
        switch(HtmlListFragment.checkFileNameFormat(newHtmlFileName.getText().toString(), EditorActivity.this)){
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
                renameDialog.dismiss();
                htmlFile.setHtmlname(newHtmlFileName.getText().toString()  + ".html");
                if(ServiceFactory.getHtmlFileServiceInstance(EditorActivity.this).updateMetaData(htmlFile)) {
                    String title;
                    if(editorHtmlFileNameTitle.getText().toString().startsWith("*"))
                        title = "*" + htmlFile.getHtmlname().substring(0, htmlFile.getHtmlname().lastIndexOf(".html"));
                    else
                        title = htmlFile.getHtmlname().substring(0, htmlFile.getHtmlname().lastIndexOf(".html"));
                    editorHtmlFileNameTitle.setText(title);
                    Toast.makeText(EditorActivity.this, "重命名成功", Toast.LENGTH_LONG).show();
                } else {
                    String title = editorHtmlFileNameTitle.getText().toString();
                    htmlFile.setHtmlname((title.startsWith("*"))?(title.substring(1)):(title) + ".html");
                    Toast.makeText(EditorActivity.this, "重命名失败", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    //显示提示保存对话框
    private void showSaveDialog(){
        if(htmlFileModifiedState == 0){
            EditorActivity.this.finish();
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setMessage("是否保存?");
        builder.setTitle("提示");

        //添加AlertDialog.Builder对象的setPositiveButton()方法
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ServiceFactory.getHtmlFileServiceInstance(EditorActivity.this).updateContent(htmlFile.getHtmlname(), htmlEditor.getText().toString());
                Toast.makeText(EditorActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                EditorActivity.this.finish();
            }
        });

        //添加AlertDialog.Builder对象的setNegativeButton()方法
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditorActivity.this.finish();
            }
        });

        builder.create().show();
    }

    //控制工具栏的显示，sign为0:全部隐藏, 1:显示editorFunctionQuick, 2:显示editorFunctionQuick2, 3:显示editorFunctionQuick3
    private void editorFunctionQuickDisplayManager(Integer sign){
        editorFunctionQuick.setVisibility(View.INVISIBLE);
        editorFunctionQuick2.setVisibility(View.INVISIBLE);
        editorFunctionQuick3.setVisibility(View.INVISIBLE);
        switch (sign){
            case 0:
                break;
            case 1:
                editorFunctionQuick.setVisibility(View.VISIBLE);
                editorFunctionQuickSign = 1;
                break;
            case 2:
                editorFunctionQuick2.setVisibility(View.VISIBLE);
                editorFunctionQuickSign = 2;
                break;
            case 3:
                editorFunctionQuick3.setVisibility(View.VISIBLE);
                editorFunctionQuickSign = 3;
                break;
        }
    }
}
