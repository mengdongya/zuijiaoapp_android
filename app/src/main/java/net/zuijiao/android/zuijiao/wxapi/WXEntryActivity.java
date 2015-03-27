package net.zuijiao.android.zuijiao.wxapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zuijiao.controller.FileManager;
import com.zuijiao.controller.MessageDef;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
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
		mApi = WXAPIFactory.createWXAPI(this, WEIXIN_ID, true);
		mApi.handleIntent(getIntent(), this);

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
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			Bundle bundle = new Bundle();
			mDialog = ProgressDialog.show(this, null,
					getResources().getString(R.string.loading));
			resp.toBundle(bundle);
			Resp sp = new Resp(bundle);
			code = sp.code;
			if (openId == null) {
				// // 获取token
				url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",new String[] { WEIXIN_ID, WEIXIN_PWD, code });
				final HttpClient client = new DefaultHttpClient();
				final HttpGet httpget = new HttpGet(url);
				new Thread(new Runnable() {

					@Override
					public void run() {
						Exception e0 = null;
						try {
							HttpResponse response = client.execute(httpget);
							HttpEntity entity = response.getEntity();
							String strResult1 = EntityUtils.toString(response
									.getEntity());
							JSONObject jsonObject1 = null;
							jsonObject1 = getJSON(strResult1);
							mAccessToken = jsonObject1
									.getString("access_token");
							String mRereshToken = jsonObject1
									.getString("refresh_token");
							String openid = jsonObject1.getString("openid");
							url = "https://api.weixin.qq.com/sns/userinfo?access_token="
									+ mAccessToken
									+ "&openid="
									+ openid
									+ "&lang=zh_CN";
							HttpGet httpget1 = new HttpGet(url);
							response = client.execute(httpget1);
							strResult1 = EntityUtils.toString(response
									.getEntity());
							jsonObject1 = getJSON(strResult1);
							String nickname = jsonObject1.getString("nickname");
							String headimgurl = jsonObject1
									.getString("headimgurl");
							InputStream inputStream = null;
							HttpURLConnection httpURLConnection = null;
							try {
								URL url = new URL(headimgurl);
								if (url != null) {
									httpURLConnection = (HttpURLConnection) url
											.openConnection();
									httpURLConnection.setConnectTimeout(3000);
									httpURLConnection.setDoInput(true);
									httpURLConnection.setRequestMethod("GET");
									int responseCode = httpURLConnection
											.getResponseCode();
									if (responseCode == 200) {
										inputStream = httpURLConnection
												.getInputStream();
									}
								}
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
							FileManager.saveImageToDisk(inputStream);
							Intent intent = new Intent(
									MessageDef.ACTION_GET_THIRD_PARTY_USER);
							Bundle bundle = new Bundle();
							bundle.putString("name", nickname);
							intent.putExtra("userinfo", bundle);
							WXEntryActivity.this.sendBroadcast(intent);
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
								Toast.makeText(
										WXEntryActivity.this,
										getResources().getString(
												R.string.login_error), 5000)
										.show();
							}
							mDialog.dismiss();
							finish();
						}
					}
				}).start();
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(WXEntryActivity.this,
					getResources().getString(R.string.login_cancel), 5000)
					.show();
			finish();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Toast.makeText(WXEntryActivity.this,
					getResources().getString(R.string.login_deny), 5000).show();
			break;
		default:
			break;
		}
	}

	public JSONObject getJSON(String sb) throws JSONException {
		return new JSONObject(sb);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		mApi.handleIntent(intent, this);
	}
}
