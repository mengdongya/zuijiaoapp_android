package com.zuijiao.android.zuijiao.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yitianhao on 2015/7/30.
 */
public class OrderAuthV3 {
    @SerializedName("apiVersion")
    private String apiVersion;
    @SerializedName("kind")
    private String kind;
    @SerializedName("data")
    private Data data;

    public String getQueryString() {
        return data.queryString;
    }

    public String getPackageName() {
        return data.packageName;
    }

    public String getAppid() {
        return data.appid;
    }

    public String getPayWaySign() {
        return data.sign;
    }

    public String getPartnerid() {
        return data.partnerid;
    }

    public String getPrepayid() {
        return data.prepayid;
    }

    public String getNoncestr() {
        return data.noncestr;
    }

    public Integer getTimestamp() {
        return data.timestamp;
    }

    public String getPayment_type() {
        return data.parameters.payment_type;
    }

    public String getOut_trade_no() {
        return data.parameters.out_trade_no;
    }

    public String getPartner() {
        return data.parameters.partner;
    }

    public String getInput_charset() {
        return data.parameters.input_charset;
    }

    public String getService() {
        return data.parameters.service;
    }

    public String getSubject() {
        return data.parameters.subject;
    }

    public Number getTotal_fee() {
        return data.parameters.total_fee;
    }

    public String getAlipaySign() {
        return data.parameters.sign;
    }

    public String getNotify_url() {
        return data.parameters.notify_url;
    }

    public String getSign_type() {
        return data.parameters.sign_type;
    }

    public String getSeller_id() {
        return data.parameters.seller_id;
    }

    public String getShow_url() {
        return data.parameters.show_url;
    }

    class Data {
        @SerializedName("queryString")
        private String queryString;
        @SerializedName("package")
        private String packageName;
        @SerializedName("appid")
        private String appid;
        @SerializedName("sign")
        private String sign;
        @SerializedName("partnerid")
        private String partnerid;
        @SerializedName("prepayid")
        private String prepayid;
        @SerializedName("noncestr")
        private String noncestr;
        @SerializedName("timestamp")
        private Integer timestamp;
        @SerializedName("parameters")
        private Parameters parameters;
    }

    class Parameters {
        @SerializedName("payment_type")
        private String payment_type;
        @SerializedName("out_trade_no")
        private String out_trade_no;
        @SerializedName("partner")
        private String partner;
        @SerializedName("_input_charset")
        private String input_charset;
        @SerializedName("service")
        private String service;
        @SerializedName("subject")
        private String subject;
        @SerializedName("total_fee")
        private Number total_fee;
        @SerializedName("sign")
        private String sign;
        @SerializedName("notify_url")
        private String notify_url;
        @SerializedName("sign_type")
        private String sign_type;
        @SerializedName("seller_id")
        private String seller_id;
        @SerializedName("show_url")
        private String show_url;

    }
}
