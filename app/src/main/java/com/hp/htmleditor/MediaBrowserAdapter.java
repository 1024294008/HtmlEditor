package com.hp.htmleditor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MediaBrowserAdapter extends BaseAdapter {
    private Integer mediaType;
    private List<String> medialDataList;
    private List<String> mediaNameList;
    private LayoutInflater inflater;
    private Context context;

    MediaBrowserAdapter(Context context, Integer mediaType, List<String> medialDataList, List<String> mediaNameList) {
        this.mediaType = mediaType;
        this.medialDataList = medialDataList;
        this.mediaNameList = mediaNameList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mediaNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView mediaName;
        ImageView mediaIcon;
        ImageView mediaLook;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dialog_editor_media_browser, null);
            mediaName = convertView.findViewById(R.id.mediaName);
            mediaIcon = convertView.findViewById(R.id.mediaIcon);
            mediaLook = convertView.findViewById(R.id.mediaLook);

            MediaBrowserAdapter.ViewCache cache = new MediaBrowserAdapter.ViewCache();
            cache.mediaName = mediaName;
            cache.mediaIcon = mediaIcon;
            cache.mediaLook = mediaLook;
            convertView.setTag(cache);

        } else {
            MediaBrowserAdapter.ViewCache cache = (MediaBrowserAdapter.ViewCache) convertView.getTag();
            mediaName = cache.mediaName;
            mediaIcon = cache.mediaIcon;
            mediaLook = cache.mediaLook;
        }
        if (mediaType != 0){
            mediaLook.setImageResource(R.drawable.standard_xml_transparent);
            mediaLook.setClickable(false);
        }
        switch (mediaType){
            //图片资源
            case 0:
                mediaIcon.setImageResource(R.drawable.editor_ic_media_image);
                mediaLook.setClickable(true);
                mediaLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaBrowserImageDisplayDialog mediaBrowserImageDisplayDialog = new MediaBrowserImageDisplayDialog(context, medialDataList.get(position));
                        mediaBrowserImageDisplayDialog.show();
                    }
                });
                break;
            //音频资源
            case 1:
                mediaIcon.setImageResource(R.drawable.editor_ic_media_audio);
                break;
            //视频资源
            case 2:
                mediaIcon.setImageResource(R.drawable.editor_ic_media_video);
                break;
            //链接资源
            case 3:
                mediaIcon.setImageResource(R.drawable.editor_ic_media_link);
                break;
            default:
                mediaIcon.setImageResource(R.drawable.standard_xml_transparent);
                break;
        }
        mediaName.setText(mediaNameList.get(position));
        return convertView;
    }

    private final class ViewCache {
        private TextView mediaName;
        private ImageView mediaIcon;
        private ImageView mediaLook;
    }
}
