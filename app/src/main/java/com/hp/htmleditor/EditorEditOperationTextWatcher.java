package com.hp.htmleditor;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.LinkedList;

/**
 * 监听编辑器的删除和插入操作
 */
public class EditorEditOperationTextWatcher implements TextWatcher {
    private final LinkedList<EditOperation> undoOpts = new LinkedList<>();//撤销栈
    private final LinkedList<EditOperation> redoOpts = new LinkedList<>();//恢复栈
    private EditOperation opt;
    private Boolean enable = true;
    private EditText editText;

    EditorEditOperationTextWatcher(EditText editText){
        this.editText = editText;
    }

    private void setEnable(Boolean enable){
        this.enable = enable;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(enable) {
            if (opt == null) opt = new EditOperation();
            int end = start + count;
            opt.setSrc(s.subSequence(start, end), start, end);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(enable){
            int end = start + count;
            opt.setDst(s.subSequence(start, end), start, end);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(enable){
            if(!redoOpts.isEmpty()) redoOpts.clear();
            if(opt != null) undoOpts.push(opt);
        }
        opt = null;
    }

    public void undo(){
        if(!undoOpts.isEmpty()){
            EditOperation opt = undoOpts.pop();

            setEnable(false);//屏蔽撤销产生的事件
            opt.undo(editText);
            setEnable(true);

            redoOpts.push(opt);
        }
    }

    public void redo(){
        if(!redoOpts.isEmpty()){
            EditOperation opt = redoOpts.pop();

            setEnable(false);//屏蔽撤销产生的事件
            opt.redo(editText);
            setEnable(true);

            undoOpts.push(opt);
        }
    }
}
