package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_concerning)
public class ConcerningActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.concerning_toolbar)
	private Toolbar mToolbar = null;
	@ViewInject(R.id.user_protocol)
	private TextView mTextUserProtocol = null;
	@ViewInject(R.id.zuijiao_team)
	private TextView mTextOurTeam = null;
	@ViewInject(R.id.concerning_tv_version_code)
	private TextView mTextVersionCode = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void findViews() {

	}

	@Override
	protected void registeViews() {
		try {
			String versionCode = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
			mTextVersionCode.setText(versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		mTextUserProtocol.setOnClickListener(this);
		mTextOurTeam.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent = new Intent(ConcerningActivity.this,
				CommonWebViewActivity.class);
		String strTitle = null;
		if (v.getId() == R.id.user_protocol) {
			strTitle = getResources().getString(R.string.user_protocol);
		} else if (v.getId() == R.id.zuijiao_team) {
			strTitle = getResources().getString(R.string.zuijiao_team);
		}
		intent.putExtra("title", strTitle);
		startActivity(intent);
	}
}
