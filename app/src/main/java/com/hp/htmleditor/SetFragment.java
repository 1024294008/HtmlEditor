package com.hp.htmleditor;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class SetFragment extends Fragment implements View.OnClickListener{
    private View view;
    private AppCompatActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_set, container, false);
        initView();
        return view;
    }

    private void initView(){
        activity = (AppCompatActivity) getActivity();

        view.findViewById(R.id.setHtmlTemplate).setOnClickListener(this);
        view.findViewById(R.id.setButtonDefine).setOnClickListener(this);
        view.findViewById(R.id.setLinkSrc).setOnClickListener(this);
        view.findViewById(R.id.setImageSrc).setOnClickListener(this);
        view.findViewById(R.id.setAudioSrc).setOnClickListener(this);
        view.findViewById(R.id.setVideoSrc).setOnClickListener(this);
    }

    //处理点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setHtmlTemplate:
                startActivity(new Intent(activity, HtmlTemplateListActivity.class));
                break;
            case R.id.setButtonDefine:
                startActivity(new Intent(activity, ButtonDefineListActivity.class));
                break;
            case R.id.setImageSrc:
                getMediaSrc(0);
                break;
            case R.id.setAudioSrc:
                getMediaSrc(1);
                break;
            case R.id.setVideoSrc:
                getMediaSrc(2);
                break;
            case R.id.setLinkSrc:
                getMediaSrc(3);
                break;
        }
    }

    private void getMediaSrc(Integer mediaType){
        final MediaBrowserDialog mediaBrowserDialog = new MediaBrowserDialog(activity, mediaType);
        mediaBrowserDialog.mediaManager();
        mediaBrowserDialog.show();
        mediaBrowserDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String mediaStoragePath = mediaBrowserDialog.getMediaStoragePath();
                if(!mediaStoragePath.equals("")){
                    ClipboardManager clipboardManager = (ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("mediaPath", mediaStoragePath));
                    Toast.makeText(activity, "路径已剪切到粘贴板上", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
