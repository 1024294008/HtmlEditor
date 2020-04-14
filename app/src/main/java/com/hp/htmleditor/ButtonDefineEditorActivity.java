package com.hp.htmleditor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ButtonDefineEditorActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText buttonDefineEditor;
    private TextView buttonDefineSave;
    private final String[] function = {"F1", "F2", "F3", "F4", "F5"};
    private Integer functionItem;
    private SharedPreferences buttonDefineContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_define_editor);
        initIntentData();
        initView();
    }

    //处理intent传递的数据
    private void initIntentData(){
        Intent intent = getIntent();
        buttonDefineEditor = findViewById(R.id.buttonDefineEditor);

        functionItem = intent.getIntExtra("functionOrigin", -1);
        if(functionItem == -1) ButtonDefineEditorActivity.this.finish();

        buttonDefineContents = getSharedPreferences("button_define_contents", Context.MODE_PRIVATE);
        buttonDefineEditor.setText(buttonDefineContents.getString(function[functionItem], ""));
    }

    private void initView(){
        //设置系统状态栏UI
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        buttonDefineSave = findViewById(R.id.buttonDefineSave);

        buttonDefineSave.setEnabled(false);
        buttonDefineSave.setAlpha((float) 0.4);
        buttonDefineSave.setOnClickListener(this);
        buttonDefineEditor.setHorizontallyScrolling(true);
        buttonDefineEditor.setOnClickListener(this);
        buttonDefineEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                buttonDefineSave.setEnabled(true);
                buttonDefineSave.setAlpha((float) 1.0);
            }
        });
        findViewById(R.id.back).setOnClickListener(this);
        ((TextView)findViewById(R.id.buttonDefineTitle)).setText(function[functionItem]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                ButtonDefineEditorActivity.this.finish();
                break;
            case R.id.buttonDefineSave:
                SharedPreferences.Editor editor = buttonDefineContents.edit();
                editor.putString(function[functionItem], buttonDefineEditor.getText().toString());
                editor.apply();
                Toast.makeText(ButtonDefineEditorActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                ButtonDefineEditorActivity.this.finish();
                break;
        }
    }
}
