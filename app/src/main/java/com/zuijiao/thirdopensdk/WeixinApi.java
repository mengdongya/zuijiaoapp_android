package com.zuijiao.thirdopensdk;

import android.content.Context;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WeixinApi extends AbsSDK {
    protected final String WEIXIN_ID = "wx908961ddfd5cade9";
    protected final String WEIXIN_PWD = "b04cac11c4477cf5e07ecffd6ca6bf86";
    private Context mContext;
    public static IWXAPI mWeiXinApi = null;

    public WeixinApi(Context context) {
        super();
        this.mContext = context;
        mWeiXinApi = WXAPIFactory.createWXAPI(mContext, WEIXIN_ID, false);
    }

    @Override
    public void Login(final LoginListener mListener) {
        if (!mWeiXinApi.isWXAppInstalled()) {
            Toast.makeText(mContext, "weixin not  installed!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        mWeiXinApi.unregisterApp();
        registeToWeixin();
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "release";
        mWeiXinApi.sendReq(req);
    }

    private void registeToWeixin() {
        mWeiXinApi.registerApp(WEIXIN_ID);
    }

    @Override
    public void logout() {
        mWeiXinApi.unregisterApp();
    }

    @Override
    public void getUserInfo() {
    }

    @Override
    public boolean isLogin(String token) {
        // TODO Auto-generated method stub
        return false;
    }

}
