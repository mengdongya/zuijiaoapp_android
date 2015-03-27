package net.zuijiao.android.zuijiao;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

@ContentView(R.layout.activity_feedback)
public class CommonWebViewActivity extends BaseActivity {
	@ViewInject(R.id.wv_feedback_content)
	private WebView mWebView = null;
	@ViewInject(R.id.feedback_toolbar)
	private Toolbar mToolbar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String contentUrl = intent.getStringExtra("content_url");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(title);
	}

	@Override
	protected void findViews() {

	}

	@Override
	protected void registeViews() {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Toast.makeText(getApplicationContext(), "onbackpressed", 1000)
					.show();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

}
