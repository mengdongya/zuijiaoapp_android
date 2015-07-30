package com.zuijiao.utils;

import android.content.Context;
import android.os.Environment;

import com.zuijiao.controller.ActivityTask;
import com.zuijiao.controller.FileManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by xiaqibo on 2015/7/28.
 */
public class UnCaughtException implements Thread.UncaughtExceptionHandler {

    private Context mContext ;

    private static final String errorOutPutFile =
            Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator
                    + FileManager.APP_FOLDER_NAME + File.separator + ".log" ;

    public UnCaughtException(Context context){
        super();
        this.mContext = context ;
    }

    public UnCaughtException(){
        super();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String info = null;
        FileOutputStream fos = null ;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED))
                return;
            File file = new File(errorOutPutFile) ;
            if(!file.exists())
                file.createNewFile() ;
            fos = new FileOutputStream(file) ;
            info = ex.getLocalizedMessage() ;
            byte [] bytes=  info.getBytes() ;
            fos.write(info.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{fos.close(); fos = null ;}catch (Throwable t){t.printStackTrace();}
            ActivityTask.getInstance().exit();
        }
    }
}
