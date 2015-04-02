package com.zuijiao.controller;

import android.content.Context;
import android.os.Environment;

import com.zuijiao.android.zuijiao.model.Gourmet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileManager {
	private Context mContext = null;
	private static FileManager mInstance = null;
	public final static String APP_FOLDER_NAME = "Zuijiao";
	public static String APP_FOLDER_PATH = "";
	public final static String THIRD_PARTY_HEAD = "third_party_head.jpg";
	private static final int COPY_BLOCK_SIZE = 4096;
    //main fragment data
    public static List<Gourmet> tmpGourmets = null;
    //my favor fragment data
    public static List<Gourmet> tmpFavorGourmets= null ;

	private FileManager(Context context) {
		this.mContext = context;
	}

	public static FileManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new FileManager(context);
		}
		return mInstance;
	}

	public static String getThirdPartyUserHeadPath() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ APP_FOLDER_NAME
				+ File.separator + THIRD_PARTY_HEAD;
		return path;
	}

	public static boolean createRootFolder(){
		return createFolder(getAppRootPath()) ;
	}

	public static String getAppRootPath() {
		if (APP_FOLDER_PATH == null || APP_FOLDER_PATH.equals("")) {
			APP_FOLDER_PATH = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + APP_FOLDER_NAME;
		}
		return APP_FOLDER_PATH;
	}

	public static boolean createFolder(String path) {
		if (checkMount()) {
			return false;
		}
		File desFile = new File(path);
		if (desFile.exists() && desFile.isDirectory()) {
			return true;
		}
		return desFile.mkdirs();
		// path.trim() ;
		// String[] folderNames = path.split(File.separator) ;
		// for(int i = 0 ;i < folderNames.length ; i++){
		//
		// }
	}

	//
	public static boolean checkMount() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (!sdCardExist) {
			return true;
		} else {
			String dataPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			if (dataPath == null || dataPath.equals("")
					|| !(new File(dataPath)).exists()) {
				return true;
			}
		}
		return false;
	}

	public static void saveImageToDisk(InputStream inputStream)
			throws IOException {
		byte[] data = new byte[1024];
		int len = 0;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(getThirdPartyUserHeadPath());
			while ((len = inputStream.read(data)) != -1) {
				fileOutputStream.write(data, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
