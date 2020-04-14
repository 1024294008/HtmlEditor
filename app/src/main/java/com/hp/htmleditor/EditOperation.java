package com.hp.htmleditor;

import android.text.Editable;
import android.widget.EditText;

import java.io.Serializable;

/**
 * 存储编辑器的基本操作，包括删除和插入，用于实现撤销和恢复功能
 */
public class EditOperation implements Serializable{
    //原始内容，删除的部分
    public String src = "";
    private int srcStart;
    private int srcEnd;

    //目标内容，插入的部分
    private String dst = "";
    private int dstStart;
    private int dstEnd;

    public void setSrc(CharSequence src, int srcStart, int srcEnd){
        this.src = (src == null)?(""):(src.toString());
        this.srcStart = srcStart;
        this.srcEnd = srcEnd;
    }

    public void setDst(CharSequence dst, int dstStart, int dstEnd){
        this.dst = (dst == null)?(""):(dst.toString());
        this.dstStart = dstStart;
        this.dstEnd = dstEnd;
    }

    public void undo(EditText editText){
        Editable editable = editText.getText();
        int cursor = -1;

        if(!dst.equals("")){
            editable.delete(dstStart, dstEnd);
            if(src.equals("")) cursor = dstStart;
        }
        if(!src.equals("")){
            editable.insert(srcStart, src);
            cursor = srcStart + src.length();
        }
        if(cursor >= 0) editText.setSelection(cursor);
    }

    public void redo(EditText editText){
        Editable editable = editText.getText();
        int cursor = -1;

        if(!src.equals("")){
            editable.delete(srcStart, srcEnd);
            if(dst.equals("")) cursor = srcStart;
        }
        if(!dst.equals("")){
            editable.insert(dstStart, dst);
            cursor = dstStart + dst.length();
        }
        if(cursor >= 0) editText.setSelection(cursor);
    }
}
