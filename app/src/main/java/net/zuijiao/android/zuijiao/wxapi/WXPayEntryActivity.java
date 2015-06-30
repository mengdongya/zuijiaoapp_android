package net.zuijiao.android.zuijiao.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.zuijiao.thirdopensdk.WeixinApi;

import net.zuijiao.android.zuijiao.R;

import org.json.JSONException;
import org.json.JSONObject;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    protected final String WEIXIN_ID = "wx908961ddfd5cade9";
    protected final String WEIXIN_PWD = "b04cac11c4477cf5e07ecffd6ca6bf86";
    private final String Tag = "WXEntryActivity";
    private IWXAPI mApi = null;
    private ProgressDialog mDialog = null;
    private String mRereshToken = null;
    private String mAccessToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mApi = WeixinApi.mWeiXinApi;
        Intent i = getIntent();
        if (mApi != null) {
            mApi.handleIntent(i, this);
//            mApi = WXAPIFactory.createWXAPI(this, WEIXIN_ID, false);
        } else {
            mApi = WXAPIFactory.createWXAPI(this, WEIXIN_ID, false);
            mApi.handleIntent(i, this);
        }
    }

    @Override
    public void onReq(BaseReq req) {
        Log.i(Tag, "onreq");
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    private String openId = null;
    private String code = null;
    private String url = null;
    private String token = null;

    @Override
    public void onResp(BaseResp resp) {
        Log.i(Tag, "onResp");
        if (mApi == null) {
            finish();
            return;
        }
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.banquet);
            builder.setMessage(getString(R.string.banquet, resp.errStr + ";code=" + String.valueOf(resp.errCode)));
            builder.show();
        }
    }

    public JSONObject getJSON(String sb) throws JSONException {
        return new JSONObject(sb);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mApi.handleIntent(intent, this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}