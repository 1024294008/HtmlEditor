package com.hp.htmleditor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class EditorTagAddPageAdapter extends PagerAdapter {
    private Context context;
    private EditorTagAddAttrAdapter privateAttrAdapter;
    private EditorTagAddAttrAdapter globalAttrAdapter;
    private EditorTagAddAttrAdapter eventAttrAdapter;
    private final String[] title = {"私有属性", "全局属性", "事件属性"};

    EditorTagAddPageAdapter(Context context, TextView labelName, List<Map<String, String>> privateAttrList, List<Map<String, String>> globalAttrList, List<Map<String, String>> eventAttrList) {
        this.context = context;
        this.privateAttrAdapter = new EditorTagAddAttrAdapter(context, labelName, privateAttrList);
        this.globalAttrAdapter = new EditorTagAddAttrAdapter(context, labelName, globalAttrList);
        this.eventAttrAdapter = new EditorTagAddAttrAdapter(context, labelName, eventAttrList);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.dialog_editor_tag_add_attr_list, null);
        initView(view, position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    private void initView(View view, Integer position){
        ListView listView = view.findViewById(R.id.editorTagAddAttrList);
        switch (position){
            case 0:
                listView.setAdapter(privateAttrAdapter);
                break;
            case 1:
                listView.setAdapter(globalAttrAdapter);
                break;
            case 2:
                listView.setAdapter(eventAttrAdapter);
                break;
        }
        listView.setEmptyView(view.findViewById(R.id.emptyView));
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        privateAttrAdapter.notifyDataSetChanged();
        globalAttrAdapter.notifyDataSetChanged();
        eventAttrAdapter.notifyDataSetChanged();
    }

    public List<Boolean> getEditorTagAddAttrSelectList(Integer position){
        List<Boolean> editorTagAddAttrSelectList = null;
        switch (position){
            case 0:
                editorTagAddAttrSelectList = privateAttrAdapter.getEditorTagAddAttrSelectList();
                break;
            case 1:
                editorTagAddAttrSelectList = globalAttrAdapter.getEditorTagAddAttrSelectList();
                break;
            case 2:
                editorTagAddAttrSelectList = eventAttrAdapter.getEditorTagAddAttrSelectList();
                break;
        }
        return editorTagAddAttrSelectList;
    }

    public String getMediaStoragePath(){
        return privateAttrAdapter.getMediaStoragePath();
    }
}
