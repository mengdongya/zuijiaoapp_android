package com.zuijiao.thirdopensdk;

import android.content.Context;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zuijiao.android.zuijiao.model.OrderAuth;

import java.util.Map;

/**
 * weixin pay
 * Created by xiaqibo on 2015/6/16.
 */
public class WeixinPay {
    private Map<String, String> resultunifiedorder;
    private PayReq req;
    private IWXAPI mWXApi;
    private OrderAuth orderAuth;

    public WeixinPay(Context context) {
        mWXApi = WXAPIFactory.createWXAPI(context, null);
    }

    public void pay(OrderAuth orderAuth) {
        this.orderAuth = orderAuth;
        req = new PayReq();
        genPayReq();
        mWXApi.registerApp(WeixinApi.WEIXIN_ID);
        mWXApi.sendReq(req);
    }

    private void genPayReq() {
        req.appId = orderAuth.getAppId();
        req.partnerId = orderAuth.getPartnerId();
        req.prepayId = orderAuth.getPrepayId();
        req.packageValue = orderAuth.getPackage();
        req.nonceStr = orderAuth.getNonceStr();
        req.timeStamp = String.valueOf(orderAuth.getTimeStamp());
        req.sign = orderAuth.getSign();
    }
}
