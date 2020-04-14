package com.hp.htmleditor;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.factory.ServiceFactory;
import com.hp.vo.LabelInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditorTagAddDialog extends Dialog implements View.OnClickListener, TextView.OnEditorActionListener{
    private Context context;
    private EditText htmlEditor;
    private EditorTagAddPageAdapter editorTagAddPageAdapter;
    private List<Map<String, String>> privateAttrList;
    private List<Map<String, String>> globalAttrList;
    private List<Map<String, String>> eventAttrList;
    private EditText editorTagAddLabelName;
    private String labelName = "";
    private LabelInfo labelInfo;

    EditorTagAddDialog(@NonNull Context context, EditText htmlEditor) {
        super(context);
        this.context = context;
        this.htmlEditor =  htmlEditor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_editor_tag_add);
        setCanceledOnTouchOutside(true);
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = (int)((context.getResources().getDisplayMetrics()).widthPixels * 0.9);
        p.height = (int)((context.getResources().getDisplayMetrics()).heightPixels * 0.75);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setAttributes(p);
        initView();
    }

    private void initView(){
        ViewPager viewPager = findViewById(R.id.editorTagAddViewPager);
        ImageView editorTagAddSearch = findViewById(R.id.editorTagAddSearch);
        ImageView editorTagAddConfirm = findViewById(R.id.editorTagAddConfirm);
        ImageView editorTagAddCancel = findViewById(R.id.editorTagAddCancel);
        editorTagAddLabelName = findViewById(R.id.editorTagAddLabelName);

        privateAttrList = new ArrayList<>();
        globalAttrList = new ArrayList<>();
        eventAttrList = new ArrayList<>();
        editorTagAddPageAdapter = new EditorTagAddPageAdapter(context, editorTagAddLabelName, privateAttrList, globalAttrList, eventAttrList);

        viewPager.setAdapter(editorTagAddPageAdapter);
        editorTagAddLabelName.setOnEditorActionListener(this);
        editorTagAddSearch.setOnClickListener(this);
        editorTagAddConfirm.setOnClickListener(this);
        editorTagAddCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editorTagAddSearch:
                startSearch();
                break;
            case R.id.editorTagAddConfirm:
                startAddTag();
                break;
            case R.id.editorTagAddCancel:
                EditorTagAddDialog.this.dismiss();
                break;
        }
    }

    //处理软键盘事件
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH){
            startSearch();
            return true;
        }
        return false;
    }

    //根据标签名更新属性列表
    private void startSearch(){
        labelName = editorTagAddLabelName.getText().toString();
        if(labelName.equals("")){
            Toast.makeText(context, "请输入标签名", Toast.LENGTH_SHORT).show();
            return;
        }
        privateAttrList.clear();
        globalAttrList.clear();
        eventAttrList.clear();
        if((labelInfo = ServiceFactory.getLabelInfoServiceInstance(context).getByName(labelName)) == null){
            labelName = "";
            editorTagAddPageAdapter.notifyDataSetChanged();
            Toast.makeText(context, "标签不存在", Toast.LENGTH_SHORT).show();
        }else {
            privateAttrList.addAll(ServiceFactory.getLabelInfoServiceInstance(context).getPrivateAttributeListByName(labelName));
            globalAttrList.addAll(ServiceFactory.getLabelInfoServiceInstance(context).getGlobalAttributeListByName(labelName));
            eventAttrList.addAll(ServiceFactory.getLabelInfoServiceInstance(context).getEventAttributeListByName(labelName));
            editorTagAddPageAdapter.notifyDataSetChanged();
        }
    }

    //根据勾选信息添加标签
    private void startAddTag(){
        if(labelName.equals("")){
            Toast.makeText(context, "请搜索标签", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder tag = new StringBuilder("<");
        List<Boolean> editorTagAddAttrSelectList;
        tag.append(labelName);
        editorTagAddAttrSelectList = editorTagAddPageAdapter.getEditorTagAddAttrSelectList(0);
        for(int i = 0;i < editorTagAddAttrSelectList.size(); i ++){
            if(editorTagAddAttrSelectList.get(i)){
                tag.append(" ").append(privateAttrList.get(i).get("labelAttrName")).append("=\"\"");
            }
        }
        editorTagAddAttrSelectList = editorTagAddPageAdapter.getEditorTagAddAttrSelectList(1);
        for(int i = 0;i < editorTagAddAttrSelectList.size(); i ++){
            if(editorTagAddAttrSelectList.get(i)){
                tag.append(" ").append(globalAttrList.get(i).get("labelAttrName")).append("=\"\"");
            }
        }
        editorTagAddAttrSelectList = editorTagAddPageAdapter.getEditorTagAddAttrSelectList(2);
        for(int i = 0;i < editorTagAddAttrSelectList.size(); i ++){
            if(editorTagAddAttrSelectList.get(i)){
                tag.append(" ").append(eventAttrList.get(i).get("labelAttrName")).append("=\"\"");
            }
        }
        if(labelInfo.getLabelType().equals("S")){
            tag.append("/>");
        }else {
            tag.append("></").append(labelName).append(">");
        }
        addSpecialAttrInfoForTag(tag);
        EditorTagAddDialog.this.dismiss();
        htmlEditor.getText().insert(htmlEditor.getSelectionStart(), tag.toString());
    }

    //对特殊属性进行复制
    private void addSpecialAttrInfoForTag(StringBuilder tag){
        if(labelName.equals("img") || labelName.equals("audio") || labelName.equals("video")){
            int srcIndex;
            if((srcIndex = tag.indexOf("src=\"\"")) != -1)
                tag.insert(srcIndex + 5, editorTagAddPageAdapter.getMediaStoragePath());
        }
        if(labelName.equals("a")){
            int srcIndex;
            if((srcIndex = tag.indexOf("href=\"\"")) != -1)
                tag.insert(srcIndex + 6, editorTagAddPageAdapter.getMediaStoragePath());
        }
    }

    @Override
    public void dismiss() {
        editorTagAddLabelName.setText("");
        privateAttrList.clear();
        globalAttrList.clear();
        eventAttrList.clear();
        editorTagAddPageAdapter.notifyDataSetChanged();
        super.dismiss();
    }
}
