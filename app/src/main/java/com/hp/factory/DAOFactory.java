package com.hp.factory;


import com.hp.dao.HtmlFileDAO;
import com.hp.dao.HtmlDatabaseHelper;
import com.hp.dao.HtmlTemplateDAO;
import com.hp.dao.LabelInfoDAO;

public class DAOFactory {
    //访问html文件
    public static HtmlFileDAO getHtmlFileDAOInstance(HtmlDatabaseHelper htmlDatabaseHelper){
        return new HtmlFileDAO(htmlDatabaseHelper);
    }

    //访问html模板文件
    public static HtmlTemplateDAO getHtmlTemplateDAOInstance(HtmlDatabaseHelper htmlDatabaseHelper){
        return new HtmlTemplateDAO(htmlDatabaseHelper);
    }

    //访问标签信息
    public static LabelInfoDAO getLabelInfoDAOInstance(){
        return new LabelInfoDAO();
    }
}
