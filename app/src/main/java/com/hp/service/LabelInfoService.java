package com.hp.service;

import android.content.Context;

import com.hp.factory.DAOFactory;
import com.hp.htmleditor.R;
import com.hp.vo.LabelInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LabelInfoService {
    private static List<Map<String, String>> globalLabelAttrs = new ArrayList<>();
    private static List<Map<String, String>> eventLabelAttrs = new ArrayList<>();

    public LabelInfoService(Context context){
        if(globalLabelAttrs.size() == 0){
            Map<String, String> attr;
            Pattern pattern = Pattern.compile("([\\w-]+):([RGEX])([FNA])");
            Matcher matcher = pattern.matcher(context.getResources().getString(R.string.globalAttrs));
            while (matcher.find()){
                attr = new HashMap<>();
                attr.put("labelAttrName", matcher.group(1));
                attr.put("labelAttrRecmd", matcher.group(2));
                attr.put("labelAttrVersion", matcher.group(3));
                globalLabelAttrs.add(attr);
            }
            matcher = pattern.matcher(context.getResources().getString(R.string.eventAttrs));
            while (matcher.find()){
                attr = new HashMap<>();
                attr.put("labelAttrName", matcher.group(1));
                attr.put("labelAttrRecmd", matcher.group(2));
                attr.put("labelAttrVersion", matcher.group(3));
                eventLabelAttrs.add(attr);
            }
        }
    }

    //通过no找到标签信息
    public LabelInfo getByNo(Integer labelno){
        return DAOFactory.getLabelInfoDAOInstance().findByNo(labelno);
    }

    //通过标签名找到标签信息
    public LabelInfo getByName(String labelname){
        return DAOFactory.getLabelInfoDAOInstance().findByName(labelname);
    }

    //查询指定范围的标签信息，查询的范围为[startIndex, endIndex)
    public LabelInfo[] getListByRange(Integer startIndex, Integer endIndex){
        if(startIndex <= 0 || startIndex >= endIndex){
            return new LabelInfo[0];
        }
        return DAOFactory.getLabelInfoDAOInstance().findListByRange(startIndex - 1, endIndex - startIndex);
    }

    //通过标签名获得格式化后的属性列表
    public List<Map<String, String>> getAttributeListByName(String labelname){
        if(DAOFactory.getLabelInfoDAOInstance().findByName(labelname) == null) return new ArrayList<>();
        List<Map<String, String>> labelAttrsList = getPrivateAttributeListByName(labelname);
        labelAttrsList.addAll(labelAttrsList.size(), globalLabelAttrs);
        labelAttrsList.addAll(labelAttrsList.size(), eventLabelAttrs);
        return labelAttrsList;
    }

    //通过标签名获得格式化后的私有属性列表
    public List<Map<String, String>> getPrivateAttributeListByName(String labelname){
        LabelInfo labelInfo = DAOFactory.getLabelInfoDAOInstance().findByName(labelname);
        if(labelInfo == null) return new ArrayList<>();
        List<Map<String, String>> labelPrivateAttrsList = new ArrayList<>();
        String labelAttrs = labelInfo.getLabelAttributes();
        Map<String, String> attr;
        if(labelAttrs != null){
            Pattern pattern = Pattern.compile("([\\w-]+):([RX])([FNA])");
            Matcher matcher = pattern.matcher(labelAttrs);
            while (matcher.find()) {
                attr = new HashMap<>();
                attr.put("labelAttrName", matcher.group(1));
                attr.put("labelAttrRecmd", matcher.group(2));
                attr.put("labelAttrVersion", matcher.group(3));
                labelPrivateAttrsList.add(attr);
            }
        }
        return labelPrivateAttrsList;
    }

    //通过标签名获得格式化后的全局属性列表
    public List<Map<String, String>> getGlobalAttributeListByName(String labelname){
        return globalLabelAttrs;
    }

    //通过标签名获得格式化后的事件属性列表
    public List<Map<String, String>> getEventAttributeListByName(String labelname){
        return eventLabelAttrs;
    }
}
