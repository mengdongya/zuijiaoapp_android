package net.zuijiao.android.zuijiao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.zuijiao.adapter.BanquetAdapter;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.Banquents;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.view.RefreshAndInitListView;

import java.lang.reflect.Field;

/**
 * Created by xiaqibo on 2015/6/8.
 */
public class BanquetDisplayFragment extends Fragment {
    private RefreshAndInitListView mListView;
    private SwipeRefreshLayout mRefreshLayout;
    private View mContentView;
    private ImageView closeBanner;
    private WebViewClient mWvClient = null;
    private BanquetAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null)
            mContentView = inflater.inflate(R.layout.fragment_banquet, null);
        mListView = (RefreshAndInitListView) mContentView.findViewById(R.id.banquet_list_view);
        mListView.setPullRefreshEnable(false);
        mAdapter = new BanquetAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mAdapter);
//        mListView.setOnItemClickListener(mListItemListener);
        mRefreshLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.banquet_swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                netWorkStep();
            }
        });
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.i("MainFragment", "onGlobalLayout");
            }
        });
        netWorkStep();
        return mContentView;
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


    private void netWorkStep() {
        mRefreshLayout.setRefreshing(true);
        Router.getBanquentModule().themesOfPublic(0, 20, new OneParameterExpression<Banquents>() {
            @Override
            public void action(Banquents banquents) {
                Log.d("banquetDisplayFragment", "success!");
                mAdapter.setData(banquents);
                mRefreshLayout.setRefreshing(false);
            }
        }, new OneParameterExpression<String>() {
            @Override
            public void action(String s) {
                Log.d("banquetDisplayFragment", "failed!");
                mRefreshLayout.setRefreshing(false);
            }
        });
    }
}
