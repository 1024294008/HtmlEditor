package com.hp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HtmlDatabaseHelper extends SQLiteOpenHelper {
    private final String CREATE_TABLE_HTML_FILE_SQL = "CREATE TABLE HtmlFile(htmlno INTEGER PRIMARY KEY AUTOINCREMENT, htmlname TEXT NOT NULL UNIQUE, createdate TEXT, modifieddate TEXT, storagepath TEXT)";
    private final String CREATE_TABLE_HTML_TEMPLATE_SQL = "CREATE TABLE HtmlTemplate(htmlno INTEGER PRIMARY KEY AUTOINCREMENT, htmlname TEXT NOT NULL UNIQUE, createdate TEXT, modifieddate TEXT, storagepath TEXT)";

    public HtmlDatabaseHelper(Context context){
        super(context, "htmleditor.db3", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建html文件表
        db.execSQL(CREATE_TABLE_HTML_FILE_SQL);
        //创建html模板表
        db.execSQL(CREATE_TABLE_HTML_TEMPLATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
