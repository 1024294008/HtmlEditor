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

public class HtmlFileService {
    private HtmlDatabaseHelper htmlDatabaseHelper;
    private final String HTML_FILE_EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory() + "/HtmlEditor" + "/HtmlFile";

    public HtmlFileService(Context context){
        htmlDatabaseHelper = new HtmlDatabaseHelper(context);
    }

    //新增一个html文件
    public boolean insert(HtmlFile htmlFile){
        if(DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).findByName(htmlFile.getHtmlname()) == null){
            File directory = new File(HTML_FILE_EXTERNAL_STORAGE_DIRECTORY);
            File file = new File(HTML_FILE_EXTERNAL_STORAGE_DIRECTORY, "/" + htmlFile.getHtmlname());
            if(!directory.exists()){
                if(!directory.mkdirs()) return false;
            }
            FileWriter fw = null;
            try{
                if(!file.exists()) if(!file.createNewFile()) return false;
                fw = new FileWriter(file);
                fw.write("");
                htmlFile.setStoragepath(file.getAbsolutePath());
                DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).doCreate(htmlFile);
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

    //更新一个html文件元数据
    public boolean updateMetaData(HtmlFile htmlFile){
        if(DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).findByNo(htmlFile.getHtmlno()) != null){
            String newStoragePath = htmlFile.getStoragepath().substring(0, htmlFile.getStoragepath().lastIndexOf("/") + 1) + htmlFile.getHtmlname();
            File file = new File(htmlFile.getStoragepath());
            if(!file.renameTo(new File(newStoragePath))) return false;
            htmlFile.setStoragepath(newStoragePath);
            DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).doUpdate(htmlFile);
            htmlDatabaseHelper.close();
            return true;
        }
        htmlDatabaseHelper.close();
        return false;
    }

    //更新一个html文件内容
    public boolean updateContent(String htmlname, String content){
        HtmlFile htmlFile;
        if((htmlFile = DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).findByName(htmlname)) != null){
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

    //通过no删除一个html文件
    public boolean deleteByNo(Integer htmlno){
        HtmlFile htmlFile;
        if((htmlFile = DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).findByNo(htmlno)) != null){
            File file = new File(htmlFile.getStoragepath());
            if(file.exists()){
                if(!file.delete()){
                    return false;
                }
            }
            DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).doRemoveByNo(htmlno);
            htmlDatabaseHelper.close();
            return true;
        }
        htmlDatabaseHelper.close();
        return false;
    }

    //通过name删除一个html文件
    public boolean deleteByName(String htmlname){
        HtmlFile htmlFile;
        if((htmlFile = DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).findByName(htmlname)) != null){
            File file = new File(htmlFile.getStoragepath());
            if(file.exists()){
                if(!file.delete()){
                    return false;
                }
            }
            DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).doRemoveByName(htmlname);
            htmlDatabaseHelper.close();
            return true;
        }
        htmlDatabaseHelper.close();
        return false;
    }

    //通过no找到html文件
    public HtmlFile getByNo(Integer htmlno){
        HtmlFile htmlFile = DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).findByNo(htmlno);
        htmlDatabaseHelper.close();
        return htmlFile;
    }

    //通过文件名找到html文件
    public HtmlFile getByName(String htmlname){
        HtmlFile htmlFile = DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).findByName(htmlname);
        htmlDatabaseHelper.close();
        return htmlFile;
    }

    //找到所有的html文件
    public HtmlFile[] getList(){
        HtmlFile[] htmlFiles = DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).findAll();
        htmlDatabaseHelper.close();
        return htmlFiles;
    }

    //通过文件名获得html文件内容
    public String getContentByName(String htmlname){
        HtmlFile htmlFile;
        StringBuffer content;
        if ((htmlFile = DAOFactory.getHtmlFileDAOInstance(htmlDatabaseHelper).findByName(htmlname)) == null){
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
