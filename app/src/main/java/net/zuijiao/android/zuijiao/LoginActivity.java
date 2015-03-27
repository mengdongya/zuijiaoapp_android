package net.zuijiao.android.zuijiao;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.zuijiao.controller.ThirdPartySdkManager;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.iv_weixin)
	private ImageView mBtnWechat = null;
	@ViewInject(R.id.iv_weibo)
	private ImageView mBtnWebo = null;
	@ViewInject(R.id.iv_qq)
	private ImageView mBtnQQ = null;
	private boolean mBClicked = false;
	private ThirdPartySdkManager mCloudMng = null;
	private int mLoginType = ThirdPartySdkManager.CLOUD_TYPE_NONE;
	private ProgressDialog mDialog = null;
	@ViewInject(R.id.login_toolbar)
	private Toolbar mToolbar = null;
	@ViewInject(R.id.et_login_email)
	private EditText mEmailEdit = null;
	@ViewInject(R.id.et_login_passsword)
	private EditText mPwdEdit = null;
	@ViewInject(R.id.tv_login_register)
	private TextView mRegisterText = null;
	@ViewInject(R.id.button1)
	private Button testBtn = null ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mCloudMng = ThirdPartySdkManager.getInstance(LoginActivity.this);
	}

	@Override
	protected void findViews() {

	}

	@Override
	protected void registeViews() {
		mBtnQQ.setOnClickListener(this);
		mBtnWebo.setOnClickListener(this);
		mBtnWechat.setOnClickListener(this);
		mRegisterText.setOnClickListener(this);
		testBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCloudMng.logout() ;
			}
		});
		initRegisterTextview();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if(mEmailEdit.getText().toString()==null ||
		// mPwdEdit.getText().toString() ==null){
		// return false ;
		// }
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.login) {
			Toast.makeText(getApplicationContext(), "sure", 1000).show();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (mBClicked) {
			return;
		}
		mBClicked = true;
		switch (v.getId()) {
		case R.id.iv_weixin:
			mLoginType = ThirdPartySdkManager.CLOUD_TYPE_WEIXIN;
			break;
		case R.id.iv_weibo:
			mLoginType = ThirdPartySdkManager.CLOUD_TYPE_WEIBO;
			break;
		case R.id.iv_qq:
			mLoginType = ThirdPartySdkManager.CLOUD_TYPE_QQ;
			break;
		case R.id.tv_login_register:
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		if (mLoginType != ThirdPartySdkManager.CLOUD_TYPE_NONE) {
			mCloudMng = ThirdPartySdkManager.getInstance(LoginActivity.this);
			mCloudMng.login(mLoginType);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
		if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
			Toast.makeText(getApplicationContext(), "��¼�ɹ�", 1000).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// mCloudMng.onLoginResult(requestCode, resultCode, data);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
		mBClicked = false;
	}

	@Override
	protected void onThirdPartyUserInfoGot() {
		super.onThirdPartyUserInfoGot();
		finish();
	}

	protected void onLoginFinish() {
		super.onLoginFinish();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				mCloudMng.refreshThirdPartyUserInfo();
			}
		}).start(); 
	}

	private void initRegisterTextview() {
		String str = getResources().getString(R.string.notify_register);
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new ForegroundColorSpan(Color.RED), str.length() - 4,
				str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		TextView tvColor = (TextView) findViewById(R.id.tv_login_register);
		tvColor.setText(style);
	}
}
