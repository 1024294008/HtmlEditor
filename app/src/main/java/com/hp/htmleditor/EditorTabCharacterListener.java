package com.hp.htmleditor;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.hp.util.NearestCharacterSearchUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorTabCharacterListener implements View.OnClickListener {
    private EditText editText;

    EditorTabCharacterListener(EditText editText){
        this.editText = editText;
    }

    @Override
    public void onClick(View v) {
        tabQuickFunction(editText.getText(), editText.getSelectionStart());
    }

    //tab快捷功能集
    private void tabQuickFunction(Editable editable, int startCursor){
        int lineStart = NearestCharacterSearchUtil.getCurrentLineStart(editable, startCursor);
        int lineEnd = NearestCharacterSearchUtil.getCurrentLineEnd(editable, startCursor);
        lineStart = (lineStart == 0)?(lineStart):(lineStart + 1);//调整行起始坐标

        if(startCursor != lineEnd){//行边界判定，若光标不位于行边界，则不启用快捷功能
            editable.insert(startCursor, "\t");
            return;
        }

        //进行识别，生成脚本
        CharSequence keyLabel = editable.subSequence(lineStart, startCursor);//获取当前行字符串
        Pattern pattern = Pattern.compile("(\\s+)?(\\w+>)*(\\w+)(\\*\\d+)?");
        Matcher matcher = pattern.matcher(keyLabel);

        StringBuilder result = new StringBuilder();
        if(matcher.matches()) {//若不匹配，则不启用快捷功能
            int finalCursor;//最终光标的位置
            StringBuilder forwardsSpace = new StringBuilder((matcher.group(1) == null)?(""):(matcher.group(1)));//前导空格，用于调整格式
            String[] fatherLabels = keyLabel.toString().trim().split(">");//连续父标签
            String sonLabel = matcher.group(3);//子标签
            if(matcher.group(2) != null) {
                for(int i = 0;i < fatherLabels.length - 1; i++) {
                    result.append(forwardsSpace).append("<").append(fatherLabels[i]).append(">\n");
                    forwardsSpace.append('\t');
                }
            }
            if(matcher.group(4) != null) {
                Integer length = Integer.valueOf(matcher.group(4).substring(1));
                if(length == 0){//设置光标位置
                    result.append(forwardsSpace);
                    finalCursor = lineStart + result.length();
                    result.append('\n');
                }else {
                    finalCursor = lineStart + result.length() + forwardsSpace.length() + sonLabel.length() + 2;
                }
                for(int i = 0;i < length; i++) {
                    result.append(forwardsSpace).append("<").append(sonLabel).append("></").append(sonLabel).append(">\n");
                }
            }else {
                result.append(forwardsSpace).append("<").append(sonLabel).append(">");
                finalCursor = lineStart + result.length();//设置光标位置
                result.append("</").append(sonLabel).append(">\n");
            }
            if(matcher.group(2) != null) {
                for(int i = fatherLabels.length - 2;i >= 0; i --) {
                    forwardsSpace.deleteCharAt(forwardsSpace.length() - 1);
                    result.append(forwardsSpace).append("</").append(fatherLabels[i]).append(">\n");
                }
            }
            result.deleteCharAt(result.length() -1 );//删除最后一个多余的换行符
            editable.replace(lineStart, lineEnd, result);
            editText.setSelection(finalCursor);
        }else {
            editable.insert(startCursor, "\t");
        }
    }
}
