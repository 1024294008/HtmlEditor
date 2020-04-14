package com.hp.htmleditor;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.factory.ServiceFactory;
import com.hp.vo.HtmlFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class HtmlTemplateListActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    private List<String> htmlTemplateList;
    private ArrayAdapter<String> htmlTemplateListAdapter;
    private View createHtmlTemplateView;
    private Dialog createHtmlTemplateDialog;
    private PopupWindow htmlOptionsDialog;
    private View currentSelectItemView;
    private Integer currentSelectItemPosition;
    private Dialog renameDialog;
    private View renameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_template_list);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHtmlTemplateList();
    }

    private void initView(){
        //设置系统状态栏UI
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ListView htmlTemplateListView = findViewById(R.id.htmlTemplateListView);
        View htmlOptionsDialogView = getLayoutInflater().inflate(R.layout.dialog_html_template_options, (FrameLayout)findViewById(R.id.htmlListFrameLayoutGroup), false);

        createHtmlTemplateView = getLayoutInflater().inflate(R.layout.dialog_loader_load,null);
        createHtmlTemplateDialog = new Dialog(this);
        renameView = getLayoutInflater().inflate(R.layout.dialog_loader_load, null);
        renameDialog = new Dialog(HtmlTemplateListActivity.this);
        htmlTemplateList = new ArrayList<>();
        htmlTemplateListAdapter = new ArrayAdapter<>(HtmlTemplateListActivity.this, R.layout.activity_html_template, R.id.htmlTemplateName, htmlTemplateList);
        htmlOptionsDialog = new PopupWindow(htmlOptionsDialogView, 500, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        createHtmlTemplateDialog.setContentView(createHtmlTemplateView);
        createHtmlTemplateDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        android.view.WindowManager.LayoutParams p = createHtmlTemplateDialog.getWindow().getAttributes();
        p.width = (int)((getResources().getDisplayMetrics()).widthPixels * 0.8);
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        createHtmlTemplateDialog.setCanceledOnTouchOutside(true);
        createHtmlTemplateDialog.getWindow().setAttributes(p);
        createHtmlTemplateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //重置
                TextView htmlFileNameTip =createHtmlTemplateView.findViewById(R.id.loaderHtmlFileNameTip);
                ((TextView)(createHtmlTemplateView.findViewById(R.id.loaderHtmlFileName))).setText("");
                ViewGroup.LayoutParams lp = htmlFileNameTip.getLayoutParams();
                lp.height = 0;
                htmlFileNameTip.setLayoutParams(lp);
            }
        });
        ((TextView)(createHtmlTemplateView.findViewById(R.id.loaderHtmlFileTitle))).setText("新建模板");
        createHtmlTemplateView.findViewById(R.id.loaderHtmlFileConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createHtmlTemplate();
            }
        });
        createHtmlTemplateView.findViewById(R.id.loaderHtmlFileCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createHtmlTemplateDialog.dismiss();
            }
        });

        htmlTemplateListView.setEmptyView(findViewById(R.id.emptyView));
        htmlTemplateListView.setAdapter(htmlTemplateListAdapter);
        htmlTemplateListView.setOnItemClickListener(this);
        htmlTemplateListView.setOnItemLongClickListener(this);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.createHtmlTemplate).setOnClickListener(this);

        htmlOptionsDialogView.setTranslationZ(10);
        htmlOptionsDialog.setOutsideTouchable(true);
        htmlOptionsDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                currentSelectItemView.setBackgroundResource(R.drawable.htmlist_xml_item_bg);
            }
        });
        htmlOptionsDialogView.findViewById(R.id.htmlTemplateRename).setOnClickListener(this);
        htmlOptionsDialogView.findViewById(R.id.htmlTemplateDelete).setOnClickListener(this);

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
        renameView.findViewById(R.id.loaderHtmlFileConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameHtmlTemplate(currentSelectItemPosition);
            }
        });
        renameView.findViewById(R.id.loaderHtmlFileCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameDialog.dismiss();
            }
        });
    }

    //从数据库获取所有html模板文件
    public void getHtmlTemplateList() {
        HtmlFile[] htmlFiles = ServiceFactory.getHtmlTemplateServiceInstance(HtmlTemplateListActivity.this).getList();
        htmlTemplateList.clear();
        for (HtmlFile htmlFile : htmlFiles) {
            htmlTemplateList.add(htmlFile.getHtmlname());
        }
        Collections.reverse(htmlTemplateList);
        htmlTemplateListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                HtmlTemplateListActivity.this.finish();
                break;
            case R.id.createHtmlTemplate:
                createHtmlTemplateDialog.show();
                break;
            case R.id.htmlTemplateRename:
                htmlOptionsDialog.dismiss();
                renameDialog.show();
                break;
            case R.id.htmlTemplateDelete:
                htmlOptionsDialog.dismiss();
                deleteHtmlTemplate(currentSelectItemPosition);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(HtmlTemplateListActivity.this, HtmlTemplateEditorActivity.class);
        intent.putExtra("htmlFileOriginType", 1);//表示此文件为已存在文件
        intent.putExtra("htmlFileOriginName", htmlTemplateList.get(position));
        HtmlTemplateListActivity.this.startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        htmlOptionsDialog.showAsDropDown(view, 500, -250);
        currentSelectItemView = view;
        currentSelectItemPosition = position;
        view.setBackgroundColor(getResources().getColor(R.color.standardSelect));
        return true;
    }

    //新建一个html模板文件
    private void createHtmlTemplate(){
        EditText htmlFileName = createHtmlTemplateView.findViewById(R.id.loaderHtmlFileName);
        TextView htmlFileNameTip =createHtmlTemplateView.findViewById(R.id.loaderHtmlFileNameTip);
        ViewGroup.LayoutParams lp = htmlFileNameTip.getLayoutParams();

        lp.height = 50;
        switch(checkFileNameFormat(htmlFileName.getText().toString(), HtmlTemplateListActivity.this)){
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
                createHtmlTemplateDialog.dismiss();
                HtmlFile htmlFile = new HtmlFile();
                String name = htmlFileName.getText().toString() + ".html";
                htmlFile.setHtmlname(name);
                htmlFile.setCreatedate(new Date());
                htmlFile.setModifieddate(new Date());
                ServiceFactory.getHtmlTemplateServiceInstance(HtmlTemplateListActivity.this).insert(htmlFile);
                Intent intent = new Intent(HtmlTemplateListActivity.this, HtmlTemplateEditorActivity.class);
                intent.putExtra("htmlFileOriginType",0);//表示此文件为新建文件
                intent.putExtra("htmlFileOriginName", name);//表示此文件为新建文件
                HtmlTemplateListActivity.this.startActivity(intent);
                break;
        }
    }

    public Integer checkFileNameFormat(String htmlFileName, Context context){
        if(htmlFileName.equals("")){
            return 0;
        }
        if(!Pattern.compile("[\\w\\u4E00-\\u9FA5]+").matcher(htmlFileName).matches()){
            return 1;
        }
        if(ServiceFactory.getHtmlTemplateServiceInstance(context).getByName(htmlFileName + ".html") != null){
            return 2;
        }
        return 3;
    }

    //重命名指定的html模板
    private void renameHtmlTemplate(final int position){
        EditText newHtmlFileName = renameView.findViewById(R.id.loaderHtmlFileName);
        TextView htmlFileNameTip = renameView.findViewById(R.id.loaderHtmlFileNameTip);
        ViewGroup.LayoutParams lp = htmlFileNameTip.getLayoutParams();

        lp.height = 50;
        switch(checkFileNameFormat(newHtmlFileName.getText().toString(), HtmlTemplateListActivity.this)){
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
                HtmlFile htmlFile = ServiceFactory.getHtmlTemplateServiceInstance(HtmlTemplateListActivity.this).getByName(htmlTemplateList.get(position));
                htmlFile.setHtmlname(newHtmlFileName.getText().toString() + ".html");
                if(ServiceFactory.getHtmlTemplateServiceInstance(HtmlTemplateListActivity.this).updateMetaData(htmlFile)){
                    htmlTemplateList.set(position, newHtmlFileName.getText().toString() + ".html");
                    htmlTemplateListAdapter.notifyDataSetChanged();
                    Toast.makeText(HtmlTemplateListActivity.this, "重命名成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HtmlTemplateListActivity.this, "重命名失败", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //删除指定的html文件
    private void deleteHtmlTemplate(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(HtmlTemplateListActivity.this);
        builder.setMessage("确定删除?");
        builder.setTitle("提示");

        //添加AlertDialog.Builder对象的setPositiveButton()方法
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String htmlFileDel;
                if ((htmlFileDel = htmlTemplateList.remove(position)) != null) {
                    //从数据库中删除对应数据
                    ServiceFactory.getHtmlTemplateServiceInstance(HtmlTemplateListActivity.this).deleteByName(htmlFileDel);
                }
                htmlTemplateListAdapter.notifyDataSetChanged();
            }
        });

        //添加AlertDialog.Builder对象的setNegativeButton()方法
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.create().show();
    }
}
