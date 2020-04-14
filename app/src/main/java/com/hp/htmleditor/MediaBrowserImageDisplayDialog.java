package com.hp.htmleditor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

public class MediaBrowserImageDisplayDialog extends Dialog{
    private String imagePath;
    private Context context;

    MediaBrowserImageDisplayDialog(@NonNull Context context, String imagePath) {
        super(context);
        this.imagePath = imagePath;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_editor_media_browser_image_display);
        setCanceledOnTouchOutside(true);
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = (int)((context.getResources().getDisplayMetrics()).widthPixels * 0.8);
        p.height = (int)((context.getResources().getDisplayMetrics()).heightPixels * 0.6);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setAttributes(p);
        ((ImageView)(findViewById(R.id.imageDisplay))).setImageBitmap(BitmapFactory.decodeFile(imagePath));
        findViewById(R.id.closeDisplay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaBrowserImageDisplayDialog.this.dismiss();
            }
        });
    }
}
