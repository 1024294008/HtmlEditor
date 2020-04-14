package com.hp.htmleditor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class EditorCodeTipsAdapter extends BaseAdapter {
    private List<Map<String, String>> editorCodeTipsList;//绑定的数据
    private LayoutInflater inflater;//布局填充服务
    private Integer resource;//列表每一项布局资源id
    private Integer flag;//0表示标签，1表示属性

    EditorCodeTipsAdapter(Context context, List<Map<String, String>> editorCodeTipsList, Integer resource, Integer flag) {
        this.editorCodeTipsList = editorCodeTipsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.flag = flag;
    }

    @Override
    public int getCount() {
        return editorCodeTipsList.size();
    }

    @Override
    public Object getItem(int position) {
        return editorCodeTipsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView editorCodeTipLabelName;
        ImageView editorCodeTipLabelVersion;
        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
            editorCodeTipLabelName = convertView.findViewById(R.id.editorCodeTipLabelName);
            editorCodeTipLabelVersion = convertView.findViewById(R.id.editorCodeTipLabelVersion);

            EditorCodeTipsAdapter.ViewCache cache = new EditorCodeTipsAdapter.ViewCache();
            cache.labelName = editorCodeTipLabelName;
            cache.labelVersion = editorCodeTipLabelVersion;
            convertView.setTag(cache);

        } else {
            EditorCodeTipsAdapter.ViewCache cache = (EditorCodeTipsAdapter.ViewCache) convertView.getTag();
            editorCodeTipLabelName = cache.labelName;
            editorCodeTipLabelVersion = cache.labelVersion;
        }
        String version;
        Map<String, String> editorCodeTipLabel = editorCodeTipsList.get(position);
        if(flag == 0){
            editorCodeTipLabelName.setText(editorCodeTipLabel.get("labelName"));
            version = editorCodeTipLabel.get("labelVersion");
        }else {
            String recmd = editorCodeTipLabel.get("labelAttrRecmd");
            if(recmd.equals("E") || recmd.equals("G"))
                editorCodeTipLabelName.setTextColor(0x77000000);
            else
                editorCodeTipLabelName.setTextColor(0xFF000000);
            editorCodeTipLabelName.setText(editorCodeTipLabel.get("labelAttrName"));
            version = editorCodeTipLabel.get("labelAttrVersion");
        }

        switch (version){
            case "F":
                editorCodeTipLabelVersion.setImageResource(R.drawable.editor_ic_code_tips_verison_f);
                break;
            case "A":
                editorCodeTipLabelVersion.setImageResource(R.drawable.editor_ic_code_tips_version_a);
                break;
            case "N":
                editorCodeTipLabelVersion.setImageResource(R.drawable.standard_xml_transparent);
                break;
            default:
                editorCodeTipLabelVersion.setImageResource(R.drawable.standard_xml_transparent);
                break;
        }
        return convertView;
    }

    private final class ViewCache {
        private TextView labelName;
        private ImageView labelVersion;
    }
}
