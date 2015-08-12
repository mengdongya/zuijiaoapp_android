package net.zuijiao.android.zuijiao;

import android.app.AlertDialog;
import android.content.Intent;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuijiao.utils.OSUtil;
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
    private boolean bApplyHost = false ;

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
            bApplyHost =mTendIntent.getBooleanExtra("apply_host" , false );
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        contentUrl = "http://www.baidu.com" ;
//        getSupportActionBar().setTitle(title);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWvClient = new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.contains("content_url")) {
                    if (url.contains("banquetDetail")) {
                        Map params = StrUtil.parseHttpString(url);
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

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("资料填写有误")
                        .setMessage(message)
                        .setPositiveButton("确定", null);
                // 禁止响应按back键的事件
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                result.confirm();
                return true;
            }

        });
        mWebView.loadUrl(contentUrl);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(bApplyHost ){
            getMenuInflater().inflate(R.menu.web_view , menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(OSUtil.getAPILevel() >= android.os.Build.VERSION_CODES.KITKAT)
                    mWebView.evaluateJavascript("window.d_router.previousPage()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Toast.makeText(mContext, value , Toast.LENGTH_SHORT).show();
                        }
                    });
                else{
                    mWebView.loadUrl("javascript:window.d_router.previousPage()");
                }
                return true ;
            case R.id.menu_web_view:
                 if(OSUtil.getAPILevel() >= android.os.Build.VERSION_CODES.KITKAT)
                mWebView.evaluateJavascript("window.d_router.nextPage()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Toast.makeText(mContext, value , Toast.LENGTH_SHORT).show();
                    }
                });
                else{
                mWebView.loadUrl("javascript:window.d_router.nextPage()");
            }
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
