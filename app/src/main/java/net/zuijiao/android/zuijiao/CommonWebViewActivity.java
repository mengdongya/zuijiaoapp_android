package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquent;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.utils.StrUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * display a designated web content
 */
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
        if (mTendIntent != null) {
            title = mTendIntent.getStringExtra("title");
            contentUrl = mTendIntent.getStringExtra("content_url");
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(title);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWvClient = new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("content_url")) {
                    if (url.contains("banquetDetail")) {
                        Map params = StrUtil.parseHttpString(url);
                        createDialog();
                        tryLoginFirst(new LambdaExpression() {
                            @Override
                            public void action() {
                                Router.getBanquentModule().theme(Integer.valueOf((String) params.get("id")), new OneParameterExpression<Banquent>() {
                                    @Override
                                    public void action(Banquent banquent) {
                                        Intent intent = new Intent(mContext, BanquetDetailActivity.class);
                                        intent.putExtra("banquet", banquent);
                                        startActivity(intent);
                                        finalizeDialog();
                                    }
                                }, new OneParameterExpression<String>() {
                                    @Override
                                    public void action(String errorMsg) {
                                        Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                                        finalizeDialog();
                                    }
                                });
                            }
                        }, new OneParameterExpression<Integer>() {
                            @Override
                            public void action(Integer integer) {
                                Toast.makeText(mContext, getString(R.string.notify_net2), Toast.LENGTH_SHORT).show();
                                finalizeDialog();
                            }
                        });
                    }
                } else
                    view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.setVisibility(View.VISIBLE);
            }

        };
        mWebView.setWebViewClient(mWvClient);
        //get web view title
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title != null) {
                    mToolbar.setTitle(title);
                }
            }
        });
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
