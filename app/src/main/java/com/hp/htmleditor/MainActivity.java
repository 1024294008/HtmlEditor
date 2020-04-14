package com.hp.htmleditor;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hp.dao.LabelDatabaseManager;

import java.io.File;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    private final int REQUEST_EXTERNAL_STORAGE = 1;//SD卡存储请求码
    private final String[] PERMISSIONS_STORAGE = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};//SD卡存储请求权限
    private HtmlListFragment htmlListFragment;
    private BrowserFragment browserFragment;
    private SetFragment setFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions();
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        //设置系统状态栏UI
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //获取控件
        RadioButton editTab = findViewById(R.id.editTab);
        RadioButton browserTab = findViewById(R.id.browserTab);
        RadioButton setTab = findViewById(R.id.setTab);
        RadioGroup mainTab = findViewById(R.id.mainTab);

        htmlListFragment = new HtmlListFragment();
        browserFragment = new BrowserFragment();
        setFragment = new SetFragment();

        //导入标签信息数据库
        if(!new File(LabelDatabaseManager.DB_PATH + "/" + LabelDatabaseManager.DB_NAME).exists())
            new LabelDatabaseManager(this).manage();

        //调整tab图片
        Drawable drawables[] = editTab.getCompoundDrawables();
        drawables[1].setBounds(new Rect(0, 0, drawables[1].getMinimumWidth()/8, drawables[1].getMinimumHeight()/8));
        editTab.setCompoundDrawables(null,drawables[1],null,null);
        drawables = browserTab.getCompoundDrawables();
        drawables[1].setBounds(new Rect(0, 0, drawables[1].getMinimumWidth()/8, drawables[1].getMinimumHeight()/8));
        browserTab.setCompoundDrawables(null,drawables[1],null,null);
        drawables = setTab.getCompoundDrawables();
        drawables[1].setBounds(new Rect(0, 0, drawables[1].getMinimumWidth()/8, drawables[1].getMinimumHeight()/8));
        setTab.setCompoundDrawables(null,drawables[1],null,null);

        //添加fragment
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.content, htmlListFragment);
        transaction.add(R.id.content, browserFragment);
        transaction.add(R.id.content, setFragment);
        transaction.hide(browserFragment);
        transaction.hide(setFragment);
        transaction.commit();

        //设置tab选项卡切换事件
        mainTab.setOnCheckedChangeListener(this);
    }

    //处理tab选项卡切换事件
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (checkedId){
            case R.id.editTab:
                transaction.show(htmlListFragment);
                transaction.hide(browserFragment);
                transaction.hide(setFragment);
                break;
            case R.id.browserTab:
                transaction.hide(htmlListFragment);
                transaction.show(browserFragment);
                transaction.hide(setFragment);
                break;
            case R.id.setTab:
                transaction.hide(htmlListFragment);
                transaction.hide(browserFragment);
                transaction.show(setFragment);
                break;
        }
        transaction.commit();
    }

    //动态申请SD卡存取权限
    private void verifyStoragePermissions(){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(MainActivity.this, "请插SD卡", Toast.LENGTH_LONG).show();
            MainActivity.this.finish();
        }
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE");
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    //授权回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_EXTERNAL_STORAGE){
            if((!permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE))||(grantResults[0] != PackageManager.PERMISSION_GRANTED)){
                MainActivity.this.finish();
            }
        }
    }
}
