package com.hp.htmleditor;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditorTagAddAttrAdapter extends BaseAdapter{
    private List<Map<String, String>> attrList;
    private LayoutInflater inflater;
    private TextView labelName;
    private List<Boolean> editorTagAddAttrSelectList;
    private Context context;
    private String mediaStoragePath;

    EditorTagAddAttrAdapter(Context context, TextView labelName, List<Map<String, String>> attrList) {
        this.attrList = attrList;
        this.labelName = labelName;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        editorTagAddAttrSelectList = new ArrayList<>();
        this.context = context;
        this.mediaStoragePath = "";
        initEditorTagAddAttrSelectList();
    }

    @Override
    public int getCount() {
        return attrList.size();
    }

    @Override
    public Object getItem(int position) {
        return attrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView editorTagAddAttrVersion;
        final TextView editorTagAddAttrName;
        ImageView editorTagAddAttrSrc;
        final CheckBox editorTagAddAttrSelect;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dialog_editor_tag_add_attr, null);
            editorTagAddAttrVersion = convertView.findViewById(R.id.editorTagAddAttrVersion);
            editorTagAddAttrName = convertView.findViewById(R.id.editorTagAddAttrName);
            editorTagAddAttrSrc = convertView.findViewById(R.id.editorTagAddAttrSrc);
            editorTagAddAttrSelect = convertView.findViewById(R.id.editorTagAddAttrSelect);

            EditorTagAddAttrAdapter.ViewCache cache = new EditorTagAddAttrAdapter.ViewCache();
            cache.editorTagAddAttrVersion = editorTagAddAttrVersion;
            cache.editorTagAddAttrName = editorTagAddAttrName;
            cache.editorTagAddAttrSrc = editorTagAddAttrSrc;
            cache.editorTagAddAttrSelect = editorTagAddAttrSelect;
            convertView.setTag(cache);

        } else {
            EditorTagAddAttrAdapter.ViewCache cache = (EditorTagAddAttrAdapter.ViewCache) convertView.getTag();
            editorTagAddAttrVersion = cache.editorTagAddAttrVersion;
            editorTagAddAttrName = cache.editorTagAddAttrName;
            editorTagAddAttrSrc = cache.editorTagAddAttrSrc;
            editorTagAddAttrSelect = cache.editorTagAddAttrSelect;
        }
        Map<String, String> attr = attrList.get(position);
        String version = attr.get("labelAttrVersion");
        switch (version){
            case "F":
                editorTagAddAttrVersion.setImageResource(R.drawable.editor_ic_code_tips_verison_f);
                break;
            case "A":
                editorTagAddAttrVersion.setImageResource(R.drawable.editor_ic_code_tips_version_a);
                break;
            case "N":
                editorTagAddAttrVersion.setImageResource(R.drawable.standard_xml_transparent);
                break;
            default:
                editorTagAddAttrVersion.setImageResource(R.drawable.standard_xml_transparent);
                break;
        }
        editorTagAddAttrName.setText(attr.get("labelAttrName"));
        editorTagAddAttrSrc.setImageResource(R.drawable.standard_xml_transparent);
        if(attr.get("labelAttrName").equals("src") || attr.get("labelAttrName").equals("href"))
            editorTagAddAttrSrcManager(editorTagAddAttrSrc);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editorTagAddAttrSelectList.get(position)){
                    editorTagAddAttrSelect.setChecked(false);
                    editorTagAddAttrSelectList.set(position, false);
                }else{
                    editorTagAddAttrSelect.setChecked(true);
                    editorTagAddAttrSelectList.set(position, true);
                }
            }
        });
        editorTagAddAttrSelect.setChecked(editorTagAddAttrSelectList.get(position));
        return convertView;
    }

    private final class ViewCache {
        private ImageView editorTagAddAttrVersion;
        private TextView editorTagAddAttrName;
        private ImageView editorTagAddAttrSrc;
        private CheckBox editorTagAddAttrSelect;
    }

    //管理快捷路径添加功能
    private void editorTagAddAttrSrcManager(final ImageView editorTagAddAttrSrc){
        switch (labelName.getText().toString()){
            case "img":
                editorTagAddAttrSrc.setImageResource(R.drawable.editor_ic_tag_add_attr_src0);
                editorTagAddAttrSrcSet(editorTagAddAttrSrc, 0);
                break;
            case "audio":
                editorTagAddAttrSrc.setImageResource(R.drawable.editor_ic_tag_add_attr_src0);
                editorTagAddAttrSrcSet(editorTagAddAttrSrc, 1);
                break;
            case "video":
                editorTagAddAttrSrc.setImageResource(R.drawable.editor_ic_tag_add_attr_src0);
                editorTagAddAttrSrcSet(editorTagAddAttrSrc, 2);
                break;
            case "a":
                editorTagAddAttrSrc.setImageResource(R.drawable.editor_ic_tag_add_attr_src0);
                editorTagAddAttrSrcSet(editorTagAddAttrSrc, 3);
            default:
                break;
        }
    }

    //快捷路径功能设置
    private void editorTagAddAttrSrcSet(final ImageView editorTagAddAttrSrc, final Integer mediaType){
        editorTagAddAttrSrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaBrowserDialog mediaBrowserDialog  = new MediaBrowserDialog(context, mediaType);
                mediaBrowserDialog.mediaManager();
                mediaBrowserDialog.show();
                mediaBrowserDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String path = mediaBrowserDialog.getMediaStoragePath();
                        if(!path.equals("")){
                            EditorTagAddAttrAdapter.this.mediaStoragePath = path;
                            editorTagAddAttrSrc.setImageResource(R.drawable.editor_ic_tag_add_attr_src1);
                            Toast.makeText(context, "文件已获取", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //将editorTagAddAttrSelectList中的数据进行初始化
    private void initEditorTagAddAttrSelectList(){
        editorTagAddAttrSelectList.clear();
        for(Map<String, String> attr:attrList){
            if(attr.get("labelAttrRecmd").equals("R"))
                editorTagAddAttrSelectList.add(true);
            else
                editorTagAddAttrSelectList.add(false);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        initEditorTagAddAttrSelectList();
        mediaStoragePath = "";
    }

    public List<Boolean> getEditorTagAddAttrSelectList(){
        return editorTagAddAttrSelectList;
    }

    public String getMediaStoragePath(){
        return mediaStoragePath;
    }
}
