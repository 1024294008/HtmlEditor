package com.hp.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hp.util.DateTransferUtil;
import com.hp.vo.HtmlFile;

import java.util.ArrayList;
import java.util.List;

public class HtmlTemplateDAO {
    private SQLiteDatabase htmlTemplateRead;
    private SQLiteDatabase htmlTemplateWrite;

    public HtmlTemplateDAO(HtmlDatabaseHelper htmlDatabaseHelper){
        htmlTemplateRead = htmlDatabaseHelper.getReadableDatabase();
        htmlTemplateWrite = htmlDatabaseHelper.getWritableDatabase();
    }

    //新增一个html模板文件
    public void doCreate(HtmlFile htmlFile){
        String sql = "INSERT INTO HtmlTemplate(htmlname, createdate, modifieddate, storagepath)VALUES(?, ?, ?, ?);";
        htmlTemplateWrite.execSQL(sql, new String[]{htmlFile.getHtmlname(), DateTransferUtil.toDateStr(htmlFile.getCreatedate()), DateTransferUtil.toDateStr(htmlFile.getModifieddate()), htmlFile.getStoragepath()});
    }

    //更新一个html模板文件
    public void doUpdate(HtmlFile htmlFile){
        String sql = "UPDATE HtmlTemplate SET htmlname = ?, createdate = ?, modifieddate = ?, storagepath = ? WHERE htmlno = ?";
        htmlTemplateWrite.execSQL(sql, new String[]{htmlFile.getHtmlname(), DateTransferUtil.toDateStr(htmlFile.getCreatedate()), DateTransferUtil.toDateStr(htmlFile.getModifieddate()), htmlFile.getStoragepath(),htmlFile.getHtmlno().toString()});
    }

    //通过name删除一个html模板文件
    public void doRemoveByName(String htmlname){
        String sql = "DELETE FROM HtmlTemplate WHERE htmlname = ?";
        htmlTemplateWrite.execSQL(sql, new String[]{htmlname});
    }

    //通过no找到html模板
    public HtmlFile findByNo(Integer htmlno){
        String sql = "SELECT * FROM HtmlTemplate WHERE htmlno = ?";
        Cursor cursor = htmlTemplateRead.rawQuery(sql, new String[]{htmlno.toString()});
        if(cursor.moveToFirst()){
            HtmlFile htmlFile = new HtmlFile();
            htmlFile.setHtmlno(cursor.getInt(cursor.getColumnIndex("htmlno")));
            htmlFile.setHtmlname(cursor.getString(cursor.getColumnIndex("htmlname")));
            htmlFile.setCreatedate(DateTransferUtil.toDate(cursor.getString(cursor.getColumnIndex("createdate"))));
            htmlFile.setModifieddate(DateTransferUtil.toDate(cursor.getString(cursor.getColumnIndex("modifieddate"))));
            htmlFile.setStoragepath(cursor.getString(cursor.getColumnIndex("storagepath")));
            cursor.close();
            return  htmlFile;
        }
        cursor.close();
        return null;
    }

    //通过文件名找到html模板文件
    public HtmlFile findByName(String htmlname){
        String sql = "SELECT * FROM HtmlTemplate WHERE htmlname = ?";
        Cursor cursor = htmlTemplateRead.rawQuery(sql, new String[]{htmlname});
        if(cursor.moveToFirst()){
            HtmlFile htmlFile = new HtmlFile();
            htmlFile.setHtmlno(cursor.getInt(cursor.getColumnIndex("htmlno")));
            htmlFile.setHtmlname(cursor.getString(cursor.getColumnIndex("htmlname")));
            htmlFile.setCreatedate(DateTransferUtil.toDate(cursor.getString(cursor.getColumnIndex("createdate"))));
            htmlFile.setModifieddate(DateTransferUtil.toDate(cursor.getString(cursor.getColumnIndex("modifieddate"))));
            htmlFile.setStoragepath(cursor.getString(cursor.getColumnIndex("storagepath")));
            cursor.close();
            return  htmlFile;
        }
        cursor.close();
        return null;
    }

    //找到所有的html模板文件
    public HtmlFile[] findAll(){
        List<HtmlFile> list = new ArrayList<>();
        HtmlFile[] htmlFiles;
        String sql = "SELECT * FROM HtmlTemplate";
        Cursor cursor = htmlTemplateRead.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                HtmlFile htmlFile = new HtmlFile();
                htmlFile.setHtmlno(cursor.getInt(cursor.getColumnIndex("htmlno")));
                htmlFile.setHtmlname(cursor.getString(cursor.getColumnIndex("htmlname")));
                htmlFile.setCreatedate(DateTransferUtil.toDate(cursor.getString(cursor.getColumnIndex("createdate"))));
                htmlFile.setModifieddate(DateTransferUtil.toDate(cursor.getString(cursor.getColumnIndex("modifieddate"))));
                htmlFile.setStoragepath(cursor.getString(cursor.getColumnIndex("storagepath")));
                list.add(htmlFile);
            }while (cursor.moveToNext());
        }
        cursor.close();
        htmlFiles = new HtmlFile[list.size()];
        list.toArray(htmlFiles);
        return htmlFiles;
    }
}
