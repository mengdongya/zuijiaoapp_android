package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yitianhao on 2015/7/31.
 */
public class OrderCreateErrorMessage {
    public static final String MOBILE = "invalidParameter:mobile";
    public static final String EVENTID = "invalidParameter:eventID";
    public static final String QUANTITY = "invalidParameter:quantity";
    public static final String CLOSED = "invalidEventStatus:closed";
    public static final String OVERTIME = "invalidEventStatus:overTime";
    public static final String OUTOFSTOCK = "invalidEventStatus:outOfStock";
    public static final String BEYONDMAXQUANTITY = "invalidEventStatus:beyondMaxQuantity";

    @SerializedName("status_code")
    private Integer mErrorCode;
    @SerializedName("error")
    private OrderError mError;

    public Integer getErrorCode() {
        return mErrorCode;
    }

    public String getErrorDomain() {
        return mError.domain;
    }

    public String getErrorReason() {
        return mError.reason;
    }

    public String getErrorMessage() {
        return mError.message;
    }

    private class OrderError {
        @SerializedName("domain")
        private String domain;
        @SerializedName("reason")
        private String reason;
        @SerializedName("message")
        private String message;

    }
}
