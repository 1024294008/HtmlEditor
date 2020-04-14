package com.hp.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hp.vo.LabelInfo;

import java.util.ArrayList;
import java.util.List;

public class LabelInfoDAO {
    private SQLiteDatabase labelInfoRead;

    public LabelInfoDAO() {
        this.labelInfoRead = SQLiteDatabase.openDatabase(LabelDatabaseManager.DB_PATH + "/" + LabelDatabaseManager.DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
    }

    //通过no找到标签信息
    public LabelInfo findByNo(Integer labelno){
        String sql = "SELECT * FROM LabelInfo WHERE labelno = ?";
        Cursor cursor = labelInfoRead.rawQuery(sql, new String[]{labelno.toString()});
        if(cursor.moveToFirst()){
            LabelInfo labelInfo = new LabelInfo();
            labelInfo.setLabelno(cursor.getInt(cursor.getColumnIndex("labelno")));
            labelInfo.setLabelname(cursor.getString(cursor.getColumnIndex("labelname")));
            labelInfo.setLabelType(cursor.getString(cursor.getColumnIndex("labelType")));
            labelInfo.setLabelAttributes(cursor.getString(cursor.getColumnIndex("labelAttributes")));
            labelInfo.setLabelVersion(cursor.getString(cursor.getColumnIndex("labelVersion")));
            labelInfo.setSubLabels(cursor.getString(cursor.getColumnIndex("subLabels")));
            cursor.close();
            return  labelInfo;
        }
        cursor.close();
        return null;
    }

    //通过标签名找到标签信息
    public LabelInfo findByName(String labelname){
        String sql = "SELECT * FROM LabelInfo WHERE labelname = ?";
        Cursor cursor = labelInfoRead.rawQuery(sql, new String[]{labelname});
        if(cursor.moveToFirst()){
           LabelInfo labelInfo = new LabelInfo();
           labelInfo.setLabelno(cursor.getInt(cursor.getColumnIndex("labelno")));
           labelInfo.setLabelname(cursor.getString(cursor.getColumnIndex("labelname")));
           labelInfo.setLabelType(cursor.getString(cursor.getColumnIndex("labelType")));
           labelInfo.setLabelAttributes(cursor.getString(cursor.getColumnIndex("labelAttributes")));
           labelInfo.setLabelVersion(cursor.getString(cursor.getColumnIndex("labelVersion")));
           labelInfo.setSubLabels(cursor.getString(cursor.getColumnIndex("subLabels")));
           cursor.close();
           return  labelInfo;
        }
        cursor.close();
        return null;
    }

    //查询指定范围的标签信息
    public LabelInfo[] findListByRange(Integer offset, Integer length){
        List<LabelInfo> list = new ArrayList<>();
        LabelInfo[] labelInfos;
        String sql = "SELECT * FROM LabelInfo LIMIT ?,?";
        Cursor cursor = labelInfoRead.rawQuery(sql, new String[]{offset.toString(), length.toString()});
        if(cursor.moveToFirst()){
            do{
                LabelInfo labelInfo = new LabelInfo();
                labelInfo.setLabelno(cursor.getInt(cursor.getColumnIndex("labelno")));
                labelInfo.setLabelname(cursor.getString(cursor.getColumnIndex("labelname")));
                labelInfo.setLabelType(cursor.getString(cursor.getColumnIndex("labelType")));
                labelInfo.setLabelAttributes(cursor.getString(cursor.getColumnIndex("labelAttributes")));
                labelInfo.setLabelVersion(cursor.getString(cursor.getColumnIndex("labelVersion")));
                labelInfo.setSubLabels(cursor.getString(cursor.getColumnIndex("subLabels")));
                list.add(labelInfo);
            }while (cursor.moveToNext());
        }
        cursor.close();
        labelInfos = new LabelInfo[list.size()];
        list.toArray(labelInfos);
        return labelInfos;
    }
}
