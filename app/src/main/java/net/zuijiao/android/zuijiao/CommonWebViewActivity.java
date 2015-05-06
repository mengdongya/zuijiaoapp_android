package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_feedback)
public class CommonWebViewActivity extends BaseActivity {
    @ViewInject(R.id.wv_feedback_content)
    private WebView mWebView = null;
    @ViewInject(R.id.feedback_toolbar)
    private Toolbar mToolbar = null;
    private String title = null;
    private String contentUrl = null;
    private WebViewClient mWvClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void registerViews() {
        try {
            title = mTendIntent.getStringExtra("title");
            contentUrl = mTendIntent.getStringExtra("content_url");
        } catch (Throwable t) {
            t.printStackTrace();
            finish();
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWvClient = new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.setVisibility(View.VISIBLE);
            }
        };
        // mWvClient.onPageFinished(mWebView,contentUrl);
//        mWebView.set
        mWebView.setWebViewClient(mWvClient);
        mWebView.loadUrl(contentUrl);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                Toast.makeText(getApplicationContext(), "onbackpressed", 1000)
//                        .show();
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
