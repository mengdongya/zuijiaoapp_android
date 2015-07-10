package com.zuijiao.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * designed for user avatar download ,not used current
 * Created by xiaqibo on 2015/6/30.
 */

public class ImageDownloader {

    public static boolean download(String url, String desFilePath) {
        try {
            Bitmap bmp = BitmapFactory.decodeStream(getImageStream(url));
            saveFile(bmp, desFilePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static InputStream getImageStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    public static void saveFile(Bitmap bm, String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists())
            file.createNewFile();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    public byte[] getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return readStream(inStream);
        }
        return null;
    }
}
