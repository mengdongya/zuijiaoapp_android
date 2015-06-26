package com.zuijiao.android.zuijiao.model;

import com.google.gson.annotations.SerializedName;
import com.zuijiao.android.zuijiao.model.Banquent.Order;

/**
 * Created by xiaqibo on 2015/6/25.
 */
public class OrderAuth {
    @SerializedName("apiVersion")
    private String apiVersion;
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private Data data;

    public String getAppId() {
        return data.paySign.appId;
    }

    public Integer getTimeStamp() {
        return data.paySign.timeStamp;
    }

    public String getNonceStr() {
        return data.paySign.nonceStr;
    }

    public String getPackage() {
        return data.paySign.packages;
    }

    public String getPrepayId() {
        return data.paySign.prepayId;
    }

    public String getSign() {
        return data.paySign.sign;
    }

    public String getPartnerId() {
        return data.paySign.partnerId;
    }

    class Data {
        @SerializedName("order")
        private Order order;
        @SerializedName("paySign")
        private PaySign paySign;
    }

    class PaySign {
        @SerializedName("queryString")
        private String query;
        @SerializedName("sign")
        private String sign;
        @SerializedName("partnerid")
        private String partnerId;
        @SerializedName("package")
        private String packages;
        @SerializedName("noncestr")
        private String nonceStr;
        @SerializedName("timestamp")
        private Integer timeStamp;
        @SerializedName("appid")
        private String appId;
        @SerializedName("prepayid")
        private String prepayId;
    }

}
