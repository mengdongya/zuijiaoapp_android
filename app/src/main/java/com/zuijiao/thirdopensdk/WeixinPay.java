package com.zuijiao.thirdopensdk;

import android.content.Context;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zuijiao.android.zuijiao.model.OrderAuth;
import com.zuijiao.android.zuijiao.model.OrderAuthV3;

import java.util.Map;

/**
 * weixin pay
 * Created by xiaqibo on 2015/6/16.
 */
public class WeixinPay {
    private Map<String, String> resultunifiedorder;
    private PayReq req;
    private IWXAPI mWXApi;
    private OrderAuthV3 orderAuth;

    public WeixinPay(Context context) {
        mWXApi = WXAPIFactory.createWXAPI(context, null);
    }

    public void pay(OrderAuthV3 orderAuth) {
        this.orderAuth = orderAuth;
        req = new PayReq();
        genPayReq();
        mWXApi.registerApp(WeixinApi.WEIXIN_ID);
        mWXApi.sendReq(req);
    }

    private void genPayReq() {
        req.appId = orderAuth.getAppid();
        req.partnerId = orderAuth.getPartnerid();
        req.prepayId = orderAuth.getPrepayid();
        req.packageValue = orderAuth.getPackageName();
        req.nonceStr = orderAuth.getNoncestr();
        req.timeStamp = String.valueOf(orderAuth.getTimestamp());
        req.sign = orderAuth.getPayWaySign();
    }
}
