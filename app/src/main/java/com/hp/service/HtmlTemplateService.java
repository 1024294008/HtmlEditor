package com.hp.service;

import android.content.Context;
import android.os.Environment;

import com.hp.dao.HtmlDatabaseHelper;
import com.hp.factory.DAOFactory;
import com.hp.vo.HtmlFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class HtmlTemplateService {
    private HtmlDatabaseHelper htmlDatabaseHelper;
    private final String HTML_TEMPLATE_EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory() + "/HtmlEditor" + "/HtmlTemplate";

    public HtmlTemplateService(Context context){
        htmlDatabaseHelper = new HtmlDatabaseHelper(context);
    }

    //新增一个html模板文件
    public boolean insert(HtmlFile htmlFile){
        if(DAOFactory.getHtmlTemplateDAOInstance(htmlDatabaseHelper).findByName(htmlFile.getHtmlname()) == null){
            File directory = new File(HTML_TEMPLATE_EXTERNAL_STORAGE_DIRECTORY);
            File file = new File(HTML_TEMPLATE_EXTERNAL_STORAGE_DIRECTORY, "/" + htmlFile.getHtmlname());
            if(!directory.exists()){
                if(!directory.mkdirs()) return false;
            }
            FileWriter fw = null;
            try{
                if(!file.exists()) if(!file.createNewFile()) return false;
                fw = new FileWriter(file);
                fw.write("");
                htmlFile.setStoragepath(file.getAbsolutePath());
                DAOFactory.getHtmlTemplateDAOInstance(htmlDatabaseHelper).doCreate(htmlFile);
            }catch (Exception e){
                return false;
            }finally {
                try{
                    if(fw != null) fw.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                htmlDatabaseHelper.close();
            }
            return true;
        }
        htmlDatabaseHelper.close();
        return false;
    }

    //更新一个html模板元数据
    public boolean updateMetaData(HtmlFile htmlFile){
        if(DAOFactory.getHtmlTemplateDAOInstance(htmlDatabaseHelper).findByNo(htmlFile.getHtmlno()) != null){
            String newStoragePath = htmlFile.getStoragepath().substring(0, htmlFile.getStoragepath().lastIndexOf("/") + 1) + htmlFile.getHtmlname();
            File file = new File(htmlFile.getStoragepath());
            if(!file.renameTo(new File(newStoragePath))) return false;
            htmlFile.setStoragepath(newStoragePath);
            DAOFactory.getHtmlTemplateDAOInstance(htmlDatabaseHelper).doUpdate(htmlFile);
            htmlDatabaseHelper.close();
            return true;
        }
        htmlDatabaseHelper.close();
        return false;
    }

    //更新一个html模板内容
    public boolean updateContent(String htmlname, String content){
        HtmlFile htmlFile;
        if((htmlFile = DAOFactory.getHtmlTemplateDAOInstance(htmlDatabaseHelper).findByName(htmlname)) != null){
            File file = new File(htmlFile.getStoragepath());
            FileWriter fw = null;
            try {
                //如果文件不存在则先创建文件再更新内容
                if(!file.exists()) if(!file.createNewFile()) return false;
                fw = new FileWriter(file);
                fw.write(content);
            }catch (Exception e){
                return false;
            }finally {
                try{
                    if(fw != null) fw.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                htmlDatabaseHelper.close();
            }
            return true;
        }
        htmlDatabaseHelper.close();
        return false;
    }

    //通过name删除一个html模板
    public boolean deleteByName(String htmlname){
        HtmlFile htmlFile;
        if((htmlFile = DAOFactory.getHtmlTemplateDAOInstance(htmlDatabaseHelper).findByName(htmlname)) != null){
            File file = new File(htmlFile.getStoragepath());
            if(file.exists()){
                if(!file.delete()){
                    return false;
                }
            }
            DAOFactory.getHtmlTemplateDAOInstance(htmlDatabaseHelper).doRemoveByName(htmlname);
            htmlDatabaseHelper.close();
            return true;
        }
        htmlDatabaseHelper.close();
        return false;
    }

    //通过文件名找到html模板
    public HtmlFile getByName(String htmlname){
        HtmlFile htmlFile = DAOFactory.getHtmlTemplateDAOInstance(htmlDatabaseHelper).findByName(htmlname);
        htmlDatabaseHelper.close();
        return htmlFile;
    }

    //找到所有的html模板
    public HtmlFile[] getList(){
        HtmlFile[] htmlFiles = DAOFactory.getHtmlTemplateDAOInstance(htmlDatabaseHelper).findAll();
        htmlDatabaseHelper.close();
        return htmlFiles;
    }

    //通过文件名获得html文件模板
    public String getContentByName(String htmlname){
        HtmlFile htmlFile;
        StringBuffer content;
        if ((htmlFile = DAOFactory.getHtmlTemplateDAOInstance(htmlDatabaseHelper).findByName(htmlname)) == null){
            htmlDatabaseHelper.close();
            return "";
        }
        File file = new File(htmlFile.getStoragepath());
        BufferedReader br = null;
        if(!file.exists()){
            htmlDatabaseHelper.close();
            return "";
        }
        content = new StringBuffer("");
        try{
            String lineData;
            br = new BufferedReader(new FileReader(file));
            while ((lineData = br.readLine()) != null){
                content.append(lineData);
                content.append('\n');
            }
        }catch (Exception e){
            return "";
        }finally {
            try{
                if(br != null) br.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            htmlDatabaseHelper.close();
        }
        return content.toString();
    }
}
