package net.zuijiao.android.zuijiao.wxapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.controller.MessageDef;
import com.zuijiao.controller.PreferenceManager;
import com.zuijiao.entity.AuthorInfo;
import com.zuijiao.thirdopensdk.WeixinApi;

import net.zuijiao.android.zuijiao.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * weixin api call back activity ,for majority api except pay api
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    //    protected final String WEIXIN_ID = "wx908961ddfd5cade9";
//    protected final String WEIXIN_PWD = "b04cac11c4477cf5e07ecffd6ca6bf86";
    private final String Tag = "WXEntryActivity";
    private IWXAPI mApi = null;
    private ProgressDialog mDialog = null;
    private String mRereshToken = null;
    private String mAccessToken = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (msg.what == 0x101) {
//                Toast.makeText(
//                        WXEntryActivity.this,
//                        getResources().getString(
//                                R.string.login_error), Toast.LENGTH_SHORT)
//                        .show();

            }
        }
    };
    private String openId = null;
    private String code = null;
    private String url = null;
    private String token = null;

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
            mApi = WXAPIFactory.createWXAPI(this, WeixinApi.WEIXIN_ID, false);
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

    /**
     * called when return from we-chat app ;
     *
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {
        Log.i(Tag, "onResp");
        if (mApi == null) {
            finish();
            return;
        }
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Bundle bundle = new Bundle();
                if(!isFinishing() && this != null)
                    mDialog = ProgressDialog.show(this, null,
                            getResources().getString(R.string.loading));
                resp.toBundle(bundle);
                Resp sp = new Resp(bundle);
                code = sp.code;
                if (openId == null) {
                    // // 获取token
                    url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code", WeixinApi.WEIXIN_ID, WeixinApi.WEIXIN_PWD, code);
                    final HttpClient client = new DefaultHttpClient();
                    final HttpGet httpget = new HttpGet(url);
                    httpget.setHeader("encoding", "UTF-8");
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            Exception e0 = null;
                            try {
                                HttpResponse response = client.execute(httpget);
                                HttpEntity entity = response.getEntity();
                                org.apache.http.Header head = entity.getContentEncoding();
                                String strResult1 = EntityUtils.toString(response
                                        .getEntity());
                                JSONObject jsonObject1 = null;
                                jsonObject1 = getJSON(strResult1);
                                mAccessToken = jsonObject1
                                        .getString("access_token");
                                String mRereshToken = jsonObject1
                                        .getString("refresh_token");
                                String openid = jsonObject1.getString("openid");
                                String unionId = jsonObject1.getString("unionid");
                                url = "https://api.weixin.qq.com/sns/userinfo?access_token="
                                        + mAccessToken
                                        + "&openid="
                                        + openid
                                        + "&lang=zh_CN";
                                HttpGet httpget1 = new HttpGet(url);
                                response = client.execute(httpget1);
                                strResult1 = EntityUtils.toString(response
                                        .getEntity(), "UTF-8");
                                jsonObject1 = getJSON(strResult1);
                                String nickname = new String(jsonObject1.getString("nickname").getBytes(), "utf-8");
                                String headimgurl = jsonObject1.getString("headimgurl");
                                Router.getOAuthModule().register(nickname, headimgurl, null
                                        , unionId, "wechat"
                                        , Optional.of(PreferenceManager.mDeviceToken)
                                        , Optional.of(mRereshToken)
                                        , new OneParameterExpression<Boolean>() {
                                            @Override
                                            public void action(Boolean isNew) {
                                                Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                                AuthorInfo userInfo = new AuthorInfo();
                                                userInfo.setUid(unionId);
                                                userInfo.setToken(mRereshToken);
                                                userInfo.setPlatform("wechat");
                                                userInfo.setHeadPath(headimgurl);
                                                if (!isNew) {
                                                    String avataUrl = null;
                                                    if (Router.getInstance().getCurrentUser().get().getAvatarURLSmall().isPresent()) {
                                                        avataUrl = Router.getInstance().getCurrentUser().get().getAvatarURLSmall().get();
                                                        userInfo.setHeadPath(avataUrl);
                                                    }
                                                }
                                                userInfo.setUserId(Router.getInstance().getCurrentUser().get().getIdentifier());
                                                userInfo.setUserName(Router.getInstance().getCurrentUser().get().getNickName());
                                                PreferenceManager.getInstance(getApplicationContext()).saveThirdPartyLoginMsg(userInfo);
                                                Intent intent = new Intent(
                                                        MessageDef.ACTION_GET_THIRD_PARTY_USER);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("name", nickname);
                                                bundle.putString("head_url", userInfo.getHeadPath());
                                                intent.putExtra("userinfo", bundle);
                                                WXEntryActivity.this.sendBroadcast(intent);
                                            }
                                        }, new OneParameterExpression<Integer>() {
                                            @Override
                                            public void action(Integer integer) {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } catch (ClientProtocolException e1) {
                                e1.printStackTrace();
                                e0 = e1;
                            } catch (IOException e1) {
                                e1.printStackTrace();
                                e0 = e1;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                e0 = e;
                            } catch (Exception e) {
                                e.printStackTrace();
                                e0 = e;
                            } finally {
                                if (e0 != null) {
                                    mHandler.sendEmptyMessage(0x101);
                                }
                                if (mDialog != null)
                                    mDialog.dismiss();
                                finish();
                            }
                        }
                    }).start();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                Toast.makeText(WXEntryActivity.this,
//                        getResources().getString(R.string.login_cancel), Toast.LENGTH_LONG)
//                        .show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                Toast.makeText(WXEntryActivity.this,
//                        getResources().getString(R.string.login_deny), Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                break;
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
//        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

}
