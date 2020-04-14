package com.hp.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hp.util.DateTransferUtil;
import com.hp.vo.HtmlFile;

import java.util.ArrayList;
import java.util.List;

public class HtmlFileDAO {
    private SQLiteDatabase htmlFileRead;
    private SQLiteDatabase htmlFileWrite;

    public HtmlFileDAO(HtmlDatabaseHelper htmlDatabaseHelper){
        htmlFileRead = htmlDatabaseHelper.getReadableDatabase();
        htmlFileWrite = htmlDatabaseHelper.getWritableDatabase();
    }

    //新增一个html文件
    public void doCreate(HtmlFile htmlFile){
        String sql = "INSERT INTO HtmlFile(htmlname, createdate, modifieddate, storagepath)VALUES(?, ?, ?, ?);";
        htmlFileWrite.execSQL(sql, new String[]{htmlFile.getHtmlname(), DateTransferUtil.toDateStr(htmlFile.getCreatedate()), DateTransferUtil.toDateStr(htmlFile.getModifieddate()), htmlFile.getStoragepath()});
    }

    //更新一个html文件
    public void doUpdate(HtmlFile htmlFile){
        String sql = "UPDATE HtmlFile SET htmlname = ?, createdate = ?, modifieddate = ?, storagepath = ? WHERE htmlno = ?";
        htmlFileWrite.execSQL(sql, new String[]{htmlFile.getHtmlname(), DateTransferUtil.toDateStr(htmlFile.getCreatedate()), DateTransferUtil.toDateStr(htmlFile.getModifieddate()), htmlFile.getStoragepath(),htmlFile.getHtmlno().toString()});
    }

    //通过no删除一个html文件
    public void doRemoveByNo(Integer htmlno){
        String sql = "DELETE FROM HtmlFile WHERE htmlno = ?";
        htmlFileWrite.execSQL(sql, new String[]{htmlno.toString()});
    }

    //通过name删除一个html文件
    public void doRemoveByName(String htmlname){
        String sql = "DELETE FROM HtmlFile WHERE htmlname = ?";
        htmlFileWrite.execSQL(sql, new String[]{htmlname});
    }

    //通过no找到html文件
    public HtmlFile findByNo(Integer htmlno){
        String sql = "SELECT * FROM HtmlFile WHERE htmlno = ?";
        Cursor cursor = htmlFileRead.rawQuery(sql, new String[]{htmlno.toString()});
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

    //通过文件名找到html文件
    public HtmlFile findByName(String htmlname){
        String sql = "SELECT * FROM HtmlFile WHERE htmlname = ?";
        Cursor cursor = htmlFileRead.rawQuery(sql, new String[]{htmlname});
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

    //找到所有的html文件
    public HtmlFile[] findAll(){
        List<HtmlFile> list = new ArrayList<>();
        HtmlFile[] htmlFiles;
        String sql = "SELECT * FROM HtmlFile";
        Cursor cursor = htmlFileRead.rawQuery(sql, null);
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
