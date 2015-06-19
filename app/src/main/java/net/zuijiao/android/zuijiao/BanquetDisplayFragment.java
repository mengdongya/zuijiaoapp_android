package net.zuijiao.android.zuijiao;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.zuijiao.adapter.BanquetAdapter;
import com.zuijiao.view.RefreshAndInitListView;

import java.lang.reflect.Field;

/**
 * Created by xiaqibo on 2015/6/8.
 */
public class BanquetDisplayFragment extends Fragment {
    private WebView mBannerView;
    private RefreshAndInitListView mListView;
    private SwipeRefreshLayout mRefreshLayout;
    private View mContentView;
    private ImageView closeBanner;
    private WebViewClient mWvClient = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null)
            mContentView = inflater.inflate(R.layout.fragment_banquet, null);
        mListView = (RefreshAndInitListView) mContentView.findViewById(R.id.banquet_list_view);
        mBannerView = (WebView) mContentView.findViewById(R.id.banquet_banner);
        showBannerView();
//        mBannerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity() , CommonWebViewActivity.class) ;
//                intent.putExtra("title" , getString(R.string.comment)) ;
//                intent.putExtra("content_url" , mBannerView.getUrl()) ;
//                startActivity(intent);
//            }
//        });
        mListView.setPullRefreshEnable(false);
        mListView.setAdapter(new BanquetAdapter(getActivity()));
        mListView.setOnItemClickListener(mListItemListener);
        mRefreshLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.banquet_swipe_refresh);
        closeBanner = (ImageView) mContentView.findViewById(R.id.banquet_close_banner);
        closeBanner.setOnClickListener(closeBannerListener);
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.i("MainFragment", "onGlobalLayout");
            }
        });
        return mContentView;
    }

    private void showBannerView() {
        mBannerView.getSettings().setJavaScriptEnabled(false);
        mBannerView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
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
        mBannerView.setWebViewClient(mWvClient);
        mBannerView.loadUrl(getString(R.string.protocol_url));
    }


    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private AdapterView.OnItemClickListener mListItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), BanquetDetailActivity.class);
            startActivity(intent);
        }
    };


    private View.OnClickListener closeBannerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f);
            mHiddenAction.setDuration(500);
            mBannerView.startAnimation(mHiddenAction);
            mBannerView.setVisibility(View.GONE);
        }
    };
}
