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

    class Data {
        @SerializedName("order")
        private Order order;
        @SerializedName("paySign")
        private PaySign params;
    }

    class PaySign {
        @SerializedName("queryString")
        private String query;
        @SerializedName("parameters")
        private Params params;
    }

    class Params {
        @SerializedName("_input_charset")
        private String inputChar;
        @SerializedName("notify_url")
        private String notifyUrl;
        @SerializedName("out_trade_no")
        private String outTradeNo;
        @SerializedName("partner")
        private String partner;
        @SerializedName("payment_type")
        private Integer paymentType;
        @SerializedName("seller_id")
        private String sellerId;
        @SerializedName("service")
        private String service;
        @SerializedName("subject")
        private Integer subject;
        @SerializedName("total_fee")
        private Float totalFee;
        @SerializedName("sign")
        private String sign;
        @SerializedName("sign_type")
        private String signType;
    }
}
