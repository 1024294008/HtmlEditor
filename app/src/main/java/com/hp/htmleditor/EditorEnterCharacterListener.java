package com.hp.htmleditor;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.hp.util.NearestCharacterSearchUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorEnterCharacterListener implements View.OnKeyListener {
    private EditText editText;

    EditorEnterCharacterListener(EditText editText){
        this.editText = editText;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
            enterQuickFunction(editText.getText(), editText.getSelectionStart() - 1);
        }
        return false;
    }

    //回车快捷功能集
    private void enterQuickFunction(Editable editable, int startCursor){
        int lineStart = NearestCharacterSearchUtil.getCurrentLineStart(editable, startCursor);
        lineStart = (lineStart == 0)?(lineStart):(lineStart + 1);//调整行起始坐标

        //回车对齐
        CharSequence keyLabel = editable.subSequence(lineStart, startCursor);//获取当前行字符串
        Pattern pattern = Pattern.compile("(\\s+)?.*");
        Matcher matcher = pattern.matcher(keyLabel);
        String forwardsSpace = "";
        if(matcher.matches())
            forwardsSpace = (matcher.group(1) == null)?(""):(matcher.group(1));//前导空格，用于调整格式

        editable.insert(startCursor + 1, forwardsSpace);//回车格式对齐

        //若光标位于><或{}时进行的特殊处理
        int leftCursor = startCursor - 1;//回车前光标前一个位置
        int rightCursor = startCursor + forwardsSpace.length() + 1;//回车后光标后一个位置
        if(leftCursor >= 0){//判断合法性
            //对><型处理
            if((editable.charAt(leftCursor) == '>') && (rightCursor < editable.length()) && (editable.charAt(rightCursor) == '<')){
                editable.insert(startCursor, '\n' + forwardsSpace + '\t');
                editText.setSelection(startCursor + forwardsSpace.length() + 2);
            }
            //对{}型处理，若只有一个"{"，提供补全"}"
            if(editable.charAt(leftCursor) == '{'){
                if((rightCursor < editable.length()) && (editable.charAt(rightCursor) == '}')){
                    editable.insert(startCursor, '\n' + forwardsSpace + '\t');
                    editText.setSelection(startCursor + forwardsSpace.length() + 2);
                }else if(rightCursor == editable.length() || editable.charAt(rightCursor) == '\n'){
                    editable.insert(rightCursor, "}");
                    editable.insert(startCursor, '\n' + forwardsSpace + '\t');
                    editText.setSelection(startCursor + forwardsSpace.length() + 2);
                }
            }
        }
    }
}
