package com.hp.htmleditor;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ButtonDefineListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private final String[] function = {"F1", "F2", "F3", "F4", "F5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_define_list);
        initView();
    }

    private void initView(){
        //设置系统状态栏UI
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ListView buttonDefineListView = findViewById(R.id.buttonDefineListView);

        buttonDefineListView.setAdapter(new ArrayAdapter<>(ButtonDefineListActivity.this, R.layout.activity_button_define, R.id.buttonDefineName, function));
        buttonDefineListView.setEmptyView(findViewById(R.id.emptyView));
        buttonDefineListView.setOnItemClickListener(this);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonDefineListActivity.this.finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(ButtonDefineListActivity.this, ButtonDefineEditorActivity.class);
        intent.putExtra("functionOrigin", position);
        ButtonDefineListActivity.this.startActivity(intent);
    }
}
