package com.hp.htmleditor;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.PopupWindow;

import com.hp.factory.ServiceFactory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorCodeTipsTextWatcher implements TextWatcher {
    private PopupWindow editorCodeTipsDialog;
    private PopupWindow editorCodeAttrsTipsDialog;
    private List<Map<String, String>> labelCacheList;//缓存一类标签
    private List<Map<String, String>> editorCodeTipsList;
    private List<Map<String, String>> labelAttrsCacheList;//缓存某个标签的所有属性
    private List<Map<String, String>> editorCodeAttrsTipsList;
    private String currentLabel;//当前属性缓存列表对应的标签
    private EditorCodeTipsAdapter editorCodeTipsAdapter;
    private EditorCodeTipsAdapter editorCodeAttrsTipsAdapter;
    private View view;
    private Boolean enable;
    private Document labelDocument;
    private Context context;

    EditorCodeTipsTextWatcher(Context context, View view, PopupWindow editorCodeTipsDialog, PopupWindow editorCodeAttrsTipsDialog, List<Map<String, String>> editorCodeTipsList,List<Map<String, String>> editorCodeAttrsTipsList, EditorCodeTipsAdapter editorCodeTipsAdapter, EditorCodeTipsAdapter editorCodeAttrsTipsAdapter){
        this.editorCodeTipsDialog = editorCodeTipsDialog;
        this.editorCodeAttrsTipsDialog = editorCodeAttrsTipsDialog;
        this.editorCodeTipsList = editorCodeTipsList;
        this.editorCodeAttrsTipsList = editorCodeAttrsTipsList;
        this.editorCodeTipsAdapter = editorCodeTipsAdapter;
        this.editorCodeAttrsTipsAdapter = editorCodeAttrsTipsAdapter;
        this.view = view;
        this.enable = true;
        this.labelCacheList = new ArrayList<>();
        this.labelAttrsCacheList = new ArrayList<>();
        this.context = context;
        currentLabel = "";
        SAXReader saxReader = new SAXReader();
        try {
            this.labelDocument = saxReader.read(context.getResources().openRawResource(R.raw.label_simple_info));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setEnable(Boolean enable){
        this.enable = enable;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!enable) return;
        if(count != 1 || s.charAt(start) == '\n'){
            if(editorCodeTipsDialog.isShowing()) editorCodeTipsDialog.dismiss();
            if(editorCodeAttrsTipsDialog.isShowing()) editorCodeAttrsTipsDialog.dismiss();
            return;
        }
        labelCodeTips(s, start);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //标签代码提示
    private void labelCodeTips(CharSequence s, int startCursor){
        if(startCursor == 0) {
            if(editorCodeTipsDialog.isShowing()) editorCodeTipsDialog.dismiss();
            if(editorCodeAttrsTipsDialog.isShowing()) editorCodeAttrsTipsDialog.dismiss();
            return;
        }
        int keyLabelStart = startCursor - 1;
        for(char t; keyLabelStart >= 0; keyLabelStart --){//定位本行距光标最近的"<"
            t = s.charAt(keyLabelStart);
            if(t == '\n') return;
            if(t == '<') break;
        }
        if(keyLabelStart == -1) return;

        CharSequence keyLabel = s.subSequence(keyLabelStart + 1, startCursor + 1);
        //标签名提示
        Pattern pattern = Pattern.compile("\\w+");//匹配标签前缀
        Matcher matcher = pattern.matcher(keyLabel);
        if(matcher.matches()){
            showEditorLabelTips(keyLabel.toString());
        } else if(editorCodeTipsDialog.isShowing()){
            editorCodeTipsDialog.dismiss();
        }

        //标签属性提示
        pattern = Pattern.compile("(\\w+).*\\s([\\w-]+)?");
        matcher = pattern.matcher(keyLabel);
        if(matcher.matches()){
            showEditorLabelAttributeTips( matcher.group(1),matcher.group(2));
        } else if(editorCodeAttrsTipsDialog.isShowing()){
            editorCodeAttrsTipsDialog.dismiss();
        }

    }

    //根据标签前导关键字显示可选标签
    private void showEditorLabelTips(String labelPrefix){
        Map<String, String> label;
        if(labelCacheList.size() != 0){
            char acronym = labelPrefix.charAt(0);
            label = labelCacheList.get(0);
            if(label.get("labelName").charAt(0) != acronym){
                getLabelCacheList(acronym);
            }
        } else {
            getLabelCacheList(labelPrefix.charAt(0));
        }
        editorCodeTipsList.clear();
        //在缓存列表中进行过滤
        for(Map<String, String> labelCache:labelCacheList){
            if(labelCache.get("labelName").startsWith(labelPrefix)){
                editorCodeTipsList.add(labelCache);
            }
        }
        editorCodeTipsAdapter.notifyDataSetChanged();
        if(editorCodeTipsList.size() == 0){
            editorCodeTipsDialog.dismiss();
        }else {
            editorCodeTipsDialog.showAsDropDown(view);
        }
    }

    //根据当前标签名和属性前缀显示可选属性
    private void showEditorLabelAttributeTips(String label, String attrPrefix){
        if(labelAttrsCacheList.size() == 0){
            if(!label.equals(currentLabel))
                labelAttrsCacheList = ServiceFactory.getLabelInfoServiceInstance(context).getAttributeListByName(label);
        } else {
            labelAttrsCacheList = ServiceFactory.getLabelInfoServiceInstance(context).getAttributeListByName(label);
        }
        currentLabel = label;
        if(labelAttrsCacheList.size() == 0){
            if(editorCodeAttrsTipsDialog.isShowing())
                editorCodeAttrsTipsDialog.dismiss();
            return;
        }
        editorCodeAttrsTipsList.clear();
        //在缓存列表中进行过滤
        if(attrPrefix == null)
            editorCodeAttrsTipsList.addAll(labelAttrsCacheList);
        else{
            for(Map<String, String> labelAttrsCache:labelAttrsCacheList){
                if(labelAttrsCache.get("labelAttrName").startsWith(attrPrefix)){
                    editorCodeAttrsTipsList.add(labelAttrsCache);
                }
            }
        }
        editorCodeAttrsTipsAdapter.notifyDataSetChanged();
        if(editorCodeAttrsTipsList.size() == 0){
            editorCodeAttrsTipsDialog.dismiss();
        }else {
            editorCodeAttrsTipsDialog.showAsDropDown(view);
        }
    }

    //根据首字母缩写缓存对应标签集合
    private void getLabelCacheList(char acronym){
        Element root = labelDocument.getRootElement();
        Element labelElement;
        Map<String, String> label;

        labelCacheList.clear();
        List labels = root.element("label_" + acronym).elements("label");
        Iterator it = labels.iterator();
        for(; it.hasNext();){
            label = new HashMap<>();
            labelElement = (Element)it.next();
            label.put("labelName", labelElement.getText());
            label.put("labelVersion", labelElement.attribute("version").getText());
            labelCacheList.add(label);
        }
    }
}
