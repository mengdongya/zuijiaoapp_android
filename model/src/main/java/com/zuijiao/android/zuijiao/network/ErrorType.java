package com.zuijiao.android.zuijiao.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by xiaqibo on 2015/8/3.
 */
public class ErrorType {


    private Integer errorCode ;
    private String errorBody ;

    public ErrorType(Integer errorCode, String errorBody) {
        this.errorBody = errorBody;
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(String errorBody) {
        this.errorBody = errorBody;
    }



    public Map<String ,Object> body(){
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(errorBody, type);
    }


}
