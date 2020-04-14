package com.hp.dao;

import android.content.Context;
import android.os.Environment;

import com.hp.htmleditor.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class LabelDatabaseManager {
    public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/com.hp.htmleditor/databases";
    public static final String DB_NAME = "labelinfo.db3";

    private Context context;

    public LabelDatabaseManager(Context context){
        this.context = context;
    }

    public void manage(){
        File dbDirectory = new File(DB_PATH);
        File dbFile = new File(DB_PATH + "/" + DB_NAME);
        try{
            if(!dbDirectory.exists()){
                if(!dbDirectory.mkdirs())
                    return;
                if(!dbFile.exists()){
                    if(!dbFile.createNewFile())
                        return;
                }
                InputStream is = this.context.getResources().openRawResource(R.raw.labelinfo);
                FileOutputStream fos = new FileOutputStream(dbFile);

                byte[] buffer = new byte[1024];
                int count;
                while ((count = is.read(buffer)) > 0){
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
