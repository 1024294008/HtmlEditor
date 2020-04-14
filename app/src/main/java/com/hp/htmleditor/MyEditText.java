package com.hp.htmleditor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.Toast;

public class MyEditText extends AppCompatEditText {
    private Paint dividePaint;
    private Paint numberPaint;
    private Paint lineSelectPaint;
    private int lineHeight;

    public MyEditText(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        dividePaint=new Paint();
        numberPaint = new Paint();
        lineSelectPaint = new Paint();
        lineHeight = getLineHeight();

        dividePaint.setColor(Color.GRAY);
        dividePaint.setStrokeWidth(5);
        numberPaint.setColor(Color.GRAY);
        numberPaint.setTextSize(50);
        lineSelectPaint.setColor(0x11000000);
        setPadding(115,0,50,0);
    }

    @Override
    protected void onDraw(final Canvas canvas){
        int lineCount = getLineCount();
        int currentLineY = (getLayout().getLineForOffset(getSelectionStart()) + 1) * lineHeight;
        float lineStartY;
        int srollX = getScrollX();
        int lineNum = (lineCount >= 14)?(lineCount):14;

        //绘制行号和行背景
        for(int l = 0;l < lineNum; l ++){
            lineStartY = (l + 1) * lineHeight;
            canvas.drawText(String.valueOf(l+1), srollX + 10, lineStartY - 8, numberPaint);
            canvas.save();
        }
        //绘制竖直分割线
        canvas.drawLine( srollX +100,0,srollX + 100,lineNum * lineHeight, dividePaint);
        canvas.drawRect(srollX + 105,8 + currentLineY - lineHeight, srollX + getWidth() - 40,8 + currentLineY, lineSelectPaint);//水平指示线
        canvas.save();
        canvas.restore();
        super.onDraw(canvas);
    }

}
