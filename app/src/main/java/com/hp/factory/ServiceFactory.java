package com.hp.factory;

import android.content.Context;

import com.hp.service.HtmlFileService;
import com.hp.service.HtmlTemplateService;
import com.hp.service.LabelInfoService;

public class ServiceFactory {
    //访问html文件
    public static HtmlFileService getHtmlFileServiceInstance(Context context){
        return new HtmlFileService(context);
    }

    //访问html模板
    public static HtmlTemplateService getHtmlTemplateServiceInstance(Context context){
        return new HtmlTemplateService(context);
    }

    //访问标签信息
    public static LabelInfoService getLabelInfoServiceInstance(Context context){
        return  new LabelInfoService(context);
    }
}
