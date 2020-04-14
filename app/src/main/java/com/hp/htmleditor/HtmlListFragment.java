package com.hp.htmleditor;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.factory.ServiceFactory;
import com.hp.util.DateTransferUtil;
import com.hp.vo.HtmlFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class HtmlListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, RadioGroup.OnCheckedChangeListener{
    private View view;
    private AppCompatActivity activity;
    private List<Map<String, String>> htmlFileList;
    private HtmlListAdapter htmlListAdapter;
    private PopupWindow htmlOptionsDialog;
    private Dialog createHtmlDialog;
    private View currentSelectItemView;
    private View createHtmlView;
    private Integer currentSelectItemPosition;
    private Spinner templateDefineSpinner;
    private List<String> templateDefineSpinnerList;
    private ArrayAdapter<String> templateDefineSpinnerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_html_list, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getHtmlFileList();
    }

    private void initView(){
        //获取父activity
        activity = (AppCompatActivity) getActivity();

        //获取控件
        ImageButton createHtml = view.findViewById(R.id.createHtml);
        ListView htmlFileListView = view.findViewById(R.id.htmlFileListView);
        View htmlOptionsDialogView = activity.getLayoutInflater().inflate(R.layout.dialog_html_options, (FrameLayout)view.findViewById(R.id.htmlListFrameLayoutGroup), false);

        htmlFileList = new ArrayList<>();
        createHtmlView = activity.getLayoutInflater().inflate(R.layout.dialog_html_create,null);
        createHtmlDialog = new Dialog(activity);
        htmlListAdapter = new HtmlListAdapter(activity, htmlFileList, R.layout.fragment_html);
        htmlOptionsDialog = new PopupWindow(htmlOptionsDialogView, 500, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        templateDefineSpinner = createHtmlView.findViewById(R.id.createHtmlFileTemplateDefineSpinner);
        templateDefineSpinnerList = new ArrayList<>();
        templateDefineSpinnerAdapter = new ArrayAdapter<>(activity, R.layout.dialog_html_create_spanner_item, R.id.htmlTemplateName, templateDefineSpinnerList);

        //设置控件
        createHtml.setOnClickListener(this);
        createHtmlDialog.setContentView(createHtmlView);
        createHtmlDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        android.view.WindowManager.LayoutParams p = createHtmlDialog.getWindow().getAttributes();
        p.width = (int)((activity.getResources().getDisplayMetrics()).widthPixels * 0.8);
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        createHtmlDialog.setCanceledOnTouchOutside(true);
        createHtmlDialog.getWindow().setAttributes(p);
        createHtmlDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //重置
                TextView htmlFileNameTip =createHtmlView.findViewById(R.id.createHtmlFileNameTip);
                ((TextView)(createHtmlView.findViewById(R.id.createHtmlFileName))).setText("");
                ((RadioGroup)createHtmlView.findViewById(R.id.createHtmlFileTemplate)).check(R.id.createHtmlFileTemplateDefault);
                ViewGroup.LayoutParams lp = htmlFileNameTip.getLayoutParams();
                lp.height = 0;
                htmlFileNameTip.setLayoutParams(lp);
            }
        });
        createHtmlView.findViewById(R.id.createHtmlFileConfirm).setOnClickListener(this);
        createHtmlView.findViewById(R.id.createHtmlFileCancel).setOnClickListener(this);
        ((RadioGroup)createHtmlView.findViewById(R.id.createHtmlFileTemplate)).setOnCheckedChangeListener(this);

        templateDefineSpinner.setAdapter(templateDefineSpinnerAdapter);

        htmlFileListView.setAdapter(htmlListAdapter);
        htmlFileListView.setEmptyView(view.findViewById(R.id.emptyView));
        htmlFileListView.setOnItemLongClickListener(this);
        htmlFileListView.setOnItemClickListener(this);

        htmlOptionsDialogView.setTranslationZ(10);
        htmlOptionsDialog.setOutsideTouchable(true);
        htmlOptionsDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                currentSelectItemView.setBackgroundResource(R.drawable.htmlist_xml_item_bg);
            }
        });
        htmlOptionsDialogView.findViewById(R.id.htmlFileDescription).setOnClickListener(this);
        htmlOptionsDialogView.findViewById(R.id.htmlFileShare).setOnClickListener(this);
        htmlOptionsDialogView.findViewById(R.id.htmlFileDelete).setOnClickListener(this);
    }

    //处理按钮点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //主界面点击事件
            case R.id.createHtml:
                createHtmlDialog.show();
                break;
            //新建对话框点击事件
            case R.id.createHtmlFileConfirm:
                createHtmlFile();
                break;
            case R.id.createHtmlFileCancel:
                createHtmlDialog.dismiss();
                break;
            //html选项点击事件
            case R.id.htmlFileDescription:
                htmlOptionsDialog.dismiss();
                showHtmlFileDescription(currentSelectItemPosition);
                break;
            case R.id.htmlFileShare:
                htmlOptionsDialog.dismiss();
                shareHtmlFile(currentSelectItemPosition);
                break;
            case R.id.htmlFileDelete:
                htmlOptionsDialog.dismiss();
                deleteHtmlFile(currentSelectItemPosition);
                break;
        }
    }

    //处理list长按事件
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        htmlOptionsDialog.showAsDropDown(view, 500, -300);
        currentSelectItemView = view;
        currentSelectItemPosition = position;
        view.setBackgroundColor(getResources().getColor(R.color.standardSelect));
        return true;
    }

    //处理list点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(activity, EditorActivity.class);
        intent.putExtra("htmlFileOriginType", 1);//表示此文件为已存在文件
        intent.putExtra("htmlFileOriginName", htmlFileList.get(position).get("htmlFileName"));
        activity.startActivity(intent);
    }

    //处理单选按钮切换事件
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        ViewGroup.LayoutParams lp = templateDefineSpinner.getLayoutParams();
        if(checkedId == R.id.createHtmlFileTemplateDefine){
            HtmlFile[] htmlFiles = ServiceFactory.getHtmlTemplateServiceInstance(activity).getList();
            templateDefineSpinnerList.clear();
            if(htmlFiles.length == 0) templateDefineSpinnerList.add("<空>");
            for (HtmlFile htmlFile : htmlFiles){
                templateDefineSpinnerList.add(htmlFile.getHtmlname());
            }
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            templateDefineSpinnerAdapter.notifyDataSetChanged();
        } else {
            lp.height = 0;
        }
        templateDefineSpinner.setLayoutParams(lp);
    }

    //从数据库获取所有html文件
    public void getHtmlFileList() {
        HtmlFile[] htmlFiles = ServiceFactory.getHtmlFileServiceInstance(activity).getList();
        htmlFileList.clear();
        for (HtmlFile htmlFile : htmlFiles) {
            Map<String, String> showitem = new HashMap<>();
            showitem.put("htmlFileName", htmlFile.getHtmlname());
            showitem.put("htmlFileModifiedDate", DateTransferUtil.toDateStr(htmlFile.getModifieddate()));
            htmlFileList.add(showitem);
        }
        Collections.reverse(htmlFileList);
        Collections.sort(htmlFileList, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                Date date1 = DateTransferUtil.toDate(o1.get("htmlFileModifiedDate"));
                Date date2 = DateTransferUtil.toDate(o2.get("htmlFileModifiedDate"));
                if((date1.getTime() - date2.getTime()) > 0){
                    return -1;
                } else{
                    return 1;
                }
            }
        });
        htmlListAdapter.notifyDataSetChanged();
    }

    //新建一个html空文件
    private void createHtmlFile(){
        EditText htmlFileName = createHtmlView.findViewById(R.id.createHtmlFileName);
        TextView htmlFileNameTip =createHtmlView.findViewById(R.id.createHtmlFileNameTip);
        RadioGroup createHtmlFileTemplate = createHtmlView.findViewById(R.id.createHtmlFileTemplate);
        ViewGroup.LayoutParams lp = htmlFileNameTip.getLayoutParams();

        lp.height = 50;
        switch(checkFileNameFormat(htmlFileName.getText().toString(), activity)){
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
                createHtmlDialog.dismiss();
                HtmlFile htmlFile = new HtmlFile();
                String name = htmlFileName.getText().toString() + ".html";
                htmlFile.setHtmlname(name);
                htmlFile.setCreatedate(new Date());
                htmlFile.setModifieddate(new Date());
                ServiceFactory.getHtmlFileServiceInstance(activity).insert(htmlFile);
                Intent intent = new Intent(activity, EditorActivity.class);
                intent.putExtra("htmlFileOriginType",0);//表示此文件为新建文件
                intent.putExtra("htmlFileOriginName", name);//表示此文件为新建文件
                switch (createHtmlFileTemplate.getCheckedRadioButtonId()){
                    case R.id.createHtmlFileTemplateDefault:
                        intent.putExtra("initFileTemplate",0);
                        break;
                    case R.id.createHtmlFileTemplateBlank:
                        intent.putExtra("initFileTemplate",1);
                        break;
                    case R.id.createHtmlFileTemplateDefine:
                        intent.putExtra("initFileTemplate",2);
                        intent.putExtra("initFileTemplateName", (String)templateDefineSpinner.getSelectedItem());
                        break;
                }
                activity.startActivity(intent);
        }
    }

    /**
     * @param htmlFileName 文件名
     * @return 0表示文件名为空，1表示文件名包含特殊字符或格式不正确，2表示文件名重复，3表示正确格式且不重复
     */
    public static Integer checkFileNameFormat(String htmlFileName, Context context){
        if(htmlFileName.equals("")){
            return 0;
        }
        if(!Pattern.compile("[\\w\\u4E00-\\u9FA5]+").matcher(htmlFileName).matches()){
            return 1;
        }
        if(ServiceFactory.getHtmlFileServiceInstance(context).getByName(htmlFileName + ".html") != null){
            return 2;
        }
        return 3;
    }

    //显示指定的html文件信息
    private void showHtmlFileDescription(final int position){
        //获取指定的html文件对象
        HtmlFile htmlFile = ServiceFactory.getHtmlFileServiceInstance(activity).getByName(htmlFileList.get(position).get("htmlFileName"));
        //获取对话框视图
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_html_options_description,null);
        //初始化对话框
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //设置控件
        ((TextView)(dialogView.findViewById(R.id.htmlFileNameDescription))).setText(htmlFile.getHtmlname());
        ((TextView)(dialogView.findViewById(R.id.htmlFilePathDescription))).setText(htmlFile.getStoragepath());
        ((TextView)(dialogView.findViewById(R.id.htmlFileCreateDateDescription))).setText(DateTransferUtil.toDateStr(htmlFile.getCreatedate()));
        ((TextView)(dialogView.findViewById(R.id.htmlFleModifiedDateDescription))).setText(DateTransferUtil.toDateStr(htmlFile.getModifieddate()));

        //设置对话框属性
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
        p.width = (int)(displayWidth * 0.8);
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setAttributes(p);
        dialog.show();
    }

    //分享指定的html文件
    private void shareHtmlFile(final int position){
        //获取指定的html文件对象
        HtmlFile htmlFile = ServiceFactory.getHtmlFileServiceInstance(activity).getByName(htmlFileList.get(position).get("htmlFileName"));

        //使用Intent.ACTION_SEND实现分享
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(htmlFile.getStoragepath())));
        startActivity(Intent.createChooser(intent, "分  享"));
    }

    //删除指定的html文件
    private void deleteHtmlFile(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("确定删除?");
        builder.setTitle("提示");

        //添加AlertDialog.Builder对象的setPositiveButton()方法
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, String> htmlFileDel;
                if ((htmlFileDel = htmlFileList.remove(position)) != null) {
                    //从数据库中删除对应数据
                    ServiceFactory.getHtmlFileServiceInstance(activity).deleteByName(htmlFileDel.get("htmlFileName"));
                }
                htmlListAdapter.notifyDataSetChanged();
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
