package com.hp.htmleditor;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoadHtmlHandler extends Handler {
    private AppCompatActivity activity;
    public LoadHtmlHandler(AppCompatActivity activity){
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case 0:
                ((LoaderActivity)activity).loadProgress.setVisibility(View.INVISIBLE);
                ((LoaderActivity)activity).displayLoadHtml.setText((String)msg.obj);
                ((LoaderActivity)activity).loadHtml.setEnabled(true);
                ((LoaderActivity)activity).loadHtml.setAlpha((float)1.0);
                break;
        }
    }
}
