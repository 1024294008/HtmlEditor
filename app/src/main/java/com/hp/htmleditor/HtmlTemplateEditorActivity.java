package com.hp.htmleditor;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.factory.ServiceFactory;
import com.hp.vo.HtmlFile;

public class HtmlTemplateEditorActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText htmlTemplateEditor;
    private TextView htmlTemplateSave;
    private HtmlFile htmlFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_template_editor);
        initIntentData();
        initView();
    }

    //处理intent传递的数据
    private void initIntentData(){
        Intent intent = getIntent();
        htmlTemplateEditor = findViewById(R.id.htmlTemplateEditor);
        htmlFile = ServiceFactory.getHtmlTemplateServiceInstance(this).getByName(intent.getStringExtra("htmlFileOriginName"));

        if(htmlFile == null){
            Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
            this.finish();
        }
        if(intent.getIntExtra("htmlFileOriginType", 0) == 0){
            //文件为新文件
            htmlTemplateEditor.setText("");
        } else {
            //文件为已存在文件
            htmlTemplateEditor.setText(ServiceFactory.getHtmlTemplateServiceInstance(HtmlTemplateEditorActivity.this).getContentByName(htmlFile.getHtmlname()));
            if(htmlTemplateEditor.getText().toString().length() > 1)
                htmlTemplateEditor.setSelection(1);
        }
    }

    private void initView(){
        //设置系统状态栏UI
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        htmlTemplateSave = findViewById(R.id.htmlTemplateSave);

        htmlTemplateSave.setEnabled(false);
        htmlTemplateSave.setAlpha((float) 0.4);
        htmlTemplateSave.setOnClickListener(this);
        htmlTemplateEditor.setHorizontallyScrolling(true);
        htmlTemplateEditor.setOnClickListener(this);
        htmlTemplateEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                htmlTemplateSave.setEnabled(true);
                htmlTemplateSave.setAlpha((float) 1.0);
            }
        });
        findViewById(R.id.back).setOnClickListener(this);
        ((TextView)findViewById(R.id.htmlTemplateTitle)).setText(htmlFile.getHtmlname().substring(0, htmlFile.getHtmlname().lastIndexOf(".html")));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                HtmlTemplateEditorActivity.this.finish();
                break;
            case R.id.htmlTemplateSave:
                ServiceFactory.getHtmlTemplateServiceInstance(HtmlTemplateEditorActivity.this).updateContent(htmlFile.getHtmlname(), htmlTemplateEditor.getText().toString());
                Toast.makeText(HtmlTemplateEditorActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                HtmlTemplateEditorActivity.this.finish();
                break;
        }
    }
}
