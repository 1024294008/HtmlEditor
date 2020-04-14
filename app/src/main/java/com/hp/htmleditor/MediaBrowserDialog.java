package com.hp.htmleditor;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hp.factory.ServiceFactory;
import com.hp.vo.HtmlFile;

import java.util.ArrayList;
import java.util.List;

public class MediaBrowserDialog extends Dialog implements AdapterView.OnItemClickListener{
    private Context context;
    private List<String> mediaDataList;
    private List<String> mediaNameList;
    private MediaBrowserAdapter mediaBrowserAdapter;
    private Integer mediaType;
    private String mediaStoragePath;

    MediaBrowserDialog(@NonNull Context context, Integer mediaType) {
        super(context);
        this.context = context;
        this.mediaDataList = new ArrayList<>();
        this.mediaNameList = new ArrayList<>();
        this.mediaType = mediaType;
        this.mediaStoragePath = "";
        mediaBrowserAdapter = new MediaBrowserAdapter(context, mediaType, mediaDataList, mediaNameList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_editor_media_browser_list);
        setCanceledOnTouchOutside(true);
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = (int)((context.getResources().getDisplayMetrics()).widthPixels * 0.9);
        p.height = (int)((context.getResources().getDisplayMetrics()).heightPixels * 0.7);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setAttributes(p);
        initView();
    }

    private void initView(){
        ListView mediaList = findViewById(R.id.mediaList);
        mediaList.setAdapter(mediaBrowserAdapter);
        mediaList.setEmptyView(findViewById(R.id.emptyView));
        mediaList.setOnItemClickListener(this);
        findViewById(R.id.closeMedia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaBrowserDialog.this.dismiss();
            }
        });
    }

    //获取媒体资源列表
    private void getMediaList(Uri mediaUri){
        mediaDataList.clear();
        mediaNameList.clear();
        Cursor cursor = context.getContentResolver().query(mediaUri, null, null, null, null);
        if (cursor == null) return;
        while (cursor.moveToNext()){
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            mediaDataList.add(new String(data, 0, data.length - 1));
            mediaNameList.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
        }
        cursor.close();
    }

    //获取链接资源列表
    private void getLinkList(){
        mediaDataList.clear();
        mediaNameList.clear();
        HtmlFile[] htmlFiles = ServiceFactory.getHtmlFileServiceInstance(context).getList();
        for(HtmlFile htmlFile:htmlFiles){
            mediaDataList.add(htmlFile.getStoragepath());
            mediaNameList.add(htmlFile.getHtmlname());
        }
    }

    //@param mediaType 0:图片资源, 1:音频资源, 2:视频资源，3:链接资源
    public void mediaManager(){
        switch (mediaType){
            case 0:
                getMediaList(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                break;
            case 1:
                getMediaList(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                break;
            case 2:
                getMediaList(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                break;
            case 3:
                getLinkList();
        }
        mediaBrowserAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.mediaStoragePath = "file://" + mediaDataList.get(position);
        MediaBrowserDialog.this.dismiss();
    }

    public String getMediaStoragePath(){
        return mediaStoragePath;
    }
}
