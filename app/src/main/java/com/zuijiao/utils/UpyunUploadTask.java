package com.zuijiao.utils;

import android.os.AsyncTask;

import com.upyun.block.api.listener.CompleteListener;
import com.upyun.block.api.listener.ProgressListener;
import com.upyun.block.api.main.UploaderManager;
import com.upyun.block.api.utils.UpYunUtils;
import com.zuijiao.android.util.DateUtil;
import com.zuijiao.android.util.HanyuPinyinHelper;
import com.zuijiao.android.zuijiao.network.Router;

import net.zuijiao.android.zuijiao.BuildConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;

/**
 * Created by user on 4/10/15.
 */
public final class UpyunUploadTask extends AsyncTask<Void, Void, String> {

    private static final String BUCKET = "zuijiao-app";
    private static final String API_SECRET = "zrwCHqOE1hVntGt+oSYWCufaWe8=";

    private String localFilePath = null;
    private String destinationPath = null;
    private CompleteListener successCallback;
    private ProgressListener progressListener;

    public UpyunUploadTask(String localFilePath, String destinationPath, ProgressListener progressListener, CompleteListener successCallback) {
        super();
        this.localFilePath = localFilePath;
        this.destinationPath = destinationPath;
        this.progressListener = progressListener;
        this.successCallback = successCallback;
    }

    @Override
    protected String doInBackground(Void... params) {
        File localFile = new File(localFilePath);
        try {
            UploaderManager uploaderManager = UploaderManager.getInstance(BUCKET);
            uploaderManager.setConnectTimeout(20);
            uploaderManager.setResponseTimeout(20);
            Map<String, Object> paramsMap = uploaderManager.fetchFileInfoDictionaryWith(localFile, destinationPath);
            String policyForInitial = UpYunUtils.getPolicy(paramsMap);
            String signatureForInitial = UpYunUtils.getSignature(paramsMap, API_SECRET);
            uploaderManager.upload(policyForInitial, signatureForInitial, localFile, progressListener, successCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "result";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null) {
            System.out.println("Success");
        } else {
            System.out.println("Upload failed");
        }
    }

    public static String avatarPath(Integer userId, String fileExtension) {
        Random randomGenerator = new Random();
        Integer randomNumber = randomGenerator.nextInt(1000);
        return String.format("%s/%s_%d.%s", BuildConfig.Avatar_Base_Url, userId, randomNumber, fileExtension);
    }

    public static String avatarPath(String relativeUrl) {
        return String.format("%s%s", Router.PicBaseUrl, relativeUrl);
    }

    public static String gourmetPath(Integer userId, String gourmetName, String fileExtension, Integer index) {
        String dateString = DateUtil.todayInString();
        final String gourmetImageName = String.format("%s/%d_%s_%s-%d.%s"
                , BuildConfig.Gourmet_Base_Url
                , userId
                , HanyuPinyinHelper.getPinyin(gourmetName).replaceAll(" ", "_")
                , dateString
                , index
                , fileExtension);
        return gourmetImageName;
    }

    public static List<String> gourmetImagePaths(final Integer userId, final String gourmetName, final Integer count, String ext) {
        List<String> result = new ArrayList<>();
        for (int index = 0; index <= count; index++) {
            final String gourmetImageName = gourmetPath(userId, gourmetName, ext, index);
            result.add(gourmetImageName);
        }
        return result;
    }

}
