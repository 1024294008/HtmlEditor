package com.hp.util;

/**
 * 给定一个字符串，得到距离指定位置最近的特定字符的索引
 */
public class NearestCharacterSearchUtil {
    //向前查找
    public static int forwardsSearch(CharSequence s, int currentCursor, char target){
        int length = s.length();
        if(currentCursor <= 0 || currentCursor > length) return -1;
        int index = currentCursor - 1;
        for(; index >= 0; index --){
            if(s.charAt(index) == target) break;
        }
        return index;
    }

    //向后查找
    public static int backwardsSearch(CharSequence s, int currentCursor, char target){
        int length = s.length();
        if(currentCursor < 0 || currentCursor >= length) return -1;
        int index = currentCursor;
        for(; index < length; index ++){
            if(s.charAt(index) == target) return index;
        }
        return -1;
    }

    //获得当前行的开始位置
    public static int getCurrentLineStart(CharSequence s, int currentCursor){
        int lineStart = NearestCharacterSearchUtil.forwardsSearch(s, currentCursor, '\n');
        if(lineStart == -1) lineStart = 0;
        return lineStart;
    }

    //获得当前行的结束位置
    public static int getCurrentLineEnd(CharSequence s, int currentCursor){
        int lineEnd = NearestCharacterSearchUtil.backwardsSearch(s, currentCursor, '\n');
        if(lineEnd == -1) lineEnd = s.length();
        return lineEnd;
    }
}
