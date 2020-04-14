package com.hp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期与字符串转换工具类
 */
public class DateTransferUtil {
    //字符串转换为日期
    public static Date toDate(String dateStr){
        if(dateStr == null) return null;
        Date date = null;
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = formater.parse(dateStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    //日期转换为字符串
    public static String toDateStr(Date date){
        if(date == null) return "";
        String time;
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = formater.format(date);
        return time;
    }
}
