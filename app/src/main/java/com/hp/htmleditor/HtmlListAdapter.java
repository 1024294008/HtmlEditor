package com.hp.htmleditor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class HtmlListAdapter extends BaseAdapter {
    private List<Map<String, String>> htmlFileList;//绑定的数据
    private LayoutInflater inflater;//布局填充服务
    private Integer resource;//列表每一项布局资源id

    HtmlListAdapter(Context context, List<Map<String, String>> htmlFileList, Integer resource) {
        this.htmlFileList = htmlFileList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return htmlFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return htmlFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView htmlFileName;
        TextView htmlFileModifiedDate;
        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
            htmlFileName = convertView.findViewById(R.id.htmlFileName);
            htmlFileModifiedDate = convertView.findViewById(R.id.htmlFileModifiedDate);

            ViewCache cache = new ViewCache();
            cache.htmlFileName = htmlFileName;
            cache.htmlFileModifiedDate = htmlFileModifiedDate;
            convertView.setTag(cache);

        } else {
            ViewCache cache = (ViewCache) convertView.getTag();
            htmlFileName = cache.htmlFileName;
            htmlFileModifiedDate = cache.htmlFileModifiedDate;
        }
        htmlFileName.setText(htmlFileList.get(position).get("htmlFileName"));
        htmlFileModifiedDate.setText(htmlFileList.get(position).get("htmlFileModifiedDate"));
        return convertView;
    }

    private final class ViewCache {
        public TextView htmlFileName = null;
        public TextView htmlFileModifiedDate = null;
    }
}
